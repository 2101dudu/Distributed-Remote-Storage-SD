package server;

import entries.*;

import java.util.*;

import java.util.concurrent.locks.*;

public class Server {
    private HashMap<String, byte[]> entries;
    private final ReadWriteLock readWriteEntriesLock = new ReentrantReadWriteLock();
    private final Lock writeEntriesLock = readWriteEntriesLock.writeLock();
    private final Lock readEntriesLock = readWriteEntriesLock.readLock();

    private HashMap<String, String> clients;
    private final ReadWriteLock readWriteClientsLock = new ReentrantReadWriteLock();
    private final Lock writeClientsLock = readWriteClientsLock.writeLock();
    private final Lock readClientsLock = readWriteClientsLock.readLock();

    // each condition is associated with the entries map's lock
    private HashMap<String, Condition> getWhenConditions;
    private final ReadWriteLock readWriteWhenConditionsLock = new ReentrantReadWriteLock();
    private final Lock writeWhenConditionsLock = readWriteWhenConditionsLock.writeLock();
    private final Lock readWhenConditionsLock = readWriteWhenConditionsLock.readLock();

    private int S;
    private int sessionsCount;
    private final ReadWriteLock readWriteSessionsLock = new ReentrantReadWriteLock();
    private final Lock writeSessionsLock = readWriteSessionsLock.writeLock();
    private final Lock readSessionsLock = readWriteSessionsLock.readLock();
    private final Condition full = writeSessionsLock.newCondition();


    public Server(int s) {
        this.entries = new HashMap<>();
        this.clients = new HashMap<>();
        this.getWhenConditions = new HashMap<>();

        this.sessionsCount = 0;
        this.S = s;
    }

    public void update(String key, byte[] data) {
        writeEntriesLock.lock();
        try {
            this.entries.put(key, data);
        } finally {
            writeEntriesLock.unlock();
        }

        String compositeKey = key + Arrays.hashCode(data);

        Condition condition;
        readWhenConditionsLock.lock(); 
        try {
            condition = this.getWhenConditions.get(compositeKey);
        } finally {
            readWhenConditionsLock.unlock();
        }

        writeEntriesLock.lock();
        try {
            if (condition != null) condition.signalAll();
        } finally {
            writeEntriesLock.unlock();
        }
    }

    public void multiUpdate(Map<String, byte[]> pairs) {
        writeEntriesLock.lock();
        try {
            this.entries.putAll(pairs);
        } finally {
            writeEntriesLock.unlock();
        }

        Set<String> compositeKey = new HashSet<>();
        for (Map.Entry<String, byte[]> pair : pairs.entrySet()) {
            String key = pair.getKey();
            byte[] data = pair.getValue();
            compositeKey.add(key + Arrays.hashCode(data));
        }

        Set<Condition> conditions = new HashSet<>();
        readWhenConditionsLock.lock();
        try {
            for (String hashedKey : compositeKey) {
                Condition condition = this.getWhenConditions.get(hashedKey);
                if (condition != null) conditions.add(condition);
            }
        } finally {
            readWhenConditionsLock.unlock();
        }

        writeEntriesLock.lock();
        try {
            for (Condition condition : conditions)
                condition.signalAll();
        } finally {
            writeEntriesLock.unlock();
        }
    }

    public PutPacket getEntry(String key) {
        byte[] data;
        readEntriesLock.lock();
        try {
            data = this.entries.get(key);
        } finally {
            readEntriesLock.unlock();
        }
        return new PutPacket(key, data);
    }

    public MultiPutPacket mutliGetEntry(Set<String> keys) {
        HashMap<String, byte[]> pairs = new HashMap<>();
        readEntriesLock.lock();
        try {
            for (String key : keys) 
                pairs.put(key, this.entries.get(key));
        } finally {
            readEntriesLock.unlock();
        }

        return new MultiPutPacket(pairs);
    }

    // REMINDER: in between the await inside writeEntriesLock and the get 
    // inside readEntriesLock, the data can be updated
    public PutPacket getEntryWhen(String key, String keyCond, byte[] dataCond) throws InterruptedException {
        String compositeKey = keyCond + Arrays.hashCode(dataCond);

        Condition condition;

        readWhenConditionsLock.lock();
        try {
            condition = this.getWhenConditions.get(compositeKey);
        } finally {
            readWhenConditionsLock.unlock();
        }

        if (condition == null) {
            condition = writeEntriesLock.newCondition();

            writeWhenConditionsLock.lock();
            try {
                this.getWhenConditions.put(compositeKey, condition);
            } finally {
                writeWhenConditionsLock.unlock();
            }
        }

        // a write lock is required to ensure that the condition is not signaled before the data is update
        writeEntriesLock.lock();
        try {
            byte[] actualData = this.entries.get(keyCond);
            while (actualData == null || !Arrays.equals(dataCond, actualData)) {
                condition.await();
                actualData = this.entries.get(keyCond);
            }
        } finally {
            writeEntriesLock.unlock();
        }

        byte[] data;
        readEntriesLock.lock();
        try {
            data = this.entries.get(key);
        } finally {
            readEntriesLock.unlock();
        }

        return new PutPacket(key, data);
    }

    public boolean register(String username, String password) {
        readClientsLock.lock();
        try {
            if (this.clients.containsKey(username)) return false;
        } finally {
            readClientsLock.unlock();
        }

        writeClientsLock.lock();
        try {
            this.clients.put(username, password);
            return true;
        } finally {
            writeClientsLock.unlock();
        }
    }

    public boolean authenticate(String username, String password) throws InterruptedException {
        String existingPassword;
        readClientsLock.lock();
        try {
            existingPassword = this.clients.get(username);
        } finally {
            readClientsLock.unlock();
        }

        if (existingPassword == null || !existingPassword.equals(password))
            return false;

        readSessionsLock.lock();
        try {
            while (this.sessionsCount >= this.S) {
                full.await();
            }
        } finally {
            readSessionsLock.unlock();
        }

        writeSessionsLock.lock();
        try {
            this.sessionsCount++;
            return true;
        } finally {
            writeSessionsLock.unlock();
        }
    }

    public void decreaseSessionsCount() {
        writeSessionsLock.lock();
        try {
            this.sessionsCount--;
            full.signal();
        } finally {
            writeSessionsLock.unlock();
        }
    }
}
