package server;

import entries.*;

import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Server {
    private HashMap<String, byte[]> entries;
    private Lock entriesLock = new ReentrantLock();

    private HashMap<String, String> clients;
    private Lock clientsLock = new ReentrantLock();

    // each condition is associated with the entries map's lock
    private HashMap<String, Condition> getWhenConditions;
    private Lock whenConditionsLock = new ReentrantLock();

    private int S;
    private int sessionsCount;
    private Lock sessionsLock = new ReentrantLock();
    private Condition full = sessionsLock.newCondition();


    public Server(int s) {
        this.entries = new HashMap<>();
        this.clients = new HashMap<>();
        this.getWhenConditions = new HashMap<>();

        this.sessionsCount = 0;
        this.S = s;
    }

    public void update(String key, byte[] data) {
        entriesLock.lock();
        try {
            this.entries.put(key, data);
        } finally {
            entriesLock.unlock();
        }

        String compositeKey = key + Arrays.hashCode(data);

        Condition condition;
        whenConditionsLock.lock();
        try {
            condition = this.getWhenConditions.get(compositeKey);
        } finally {
            whenConditionsLock.unlock();
        }

        entriesLock.lock();
        try {
            if (condition != null) condition.signalAll();
        } finally {
            entriesLock.unlock();
        }
    }

    public void multiUpdate(Map<String, byte[]> pairs) {
        entriesLock.lock();
        try {
            this.entries.putAll(pairs);
        } finally {
            entriesLock.unlock();
        }

        Set<String> compositeKey = new HashSet<>();
        for (Map.Entry<String, byte[]> pair : pairs.entrySet()) {
            String key = pair.getKey();
            byte[] data = pair.getValue();
            compositeKey.add(key + Arrays.hashCode(data));
        }

        Set<Condition> conditions = new HashSet<>();
        whenConditionsLock.lock();
        try {
            for (String hashedKey : compositeKey) {
                Condition condition = this.getWhenConditions.get(hashedKey);
                if (condition != null) conditions.add(condition);
            }
        } finally {
            whenConditionsLock.unlock();
        }

        entriesLock.lock();
        try {
            for (Condition condition : conditions)
                condition.signalAll();
        } finally {
            entriesLock.unlock();
        }
    }

    public PutPacket getEntry(String key) {
        PutPacket entry = new PutPacket();
        entriesLock.lock();
        try {
            byte[] data = this.entries.get(key);

            entry.setKey(key);
            entry.setData(data);
            
            return entry;
        } finally {
            entriesLock.unlock();
        }
    }

    public MultiPutPacket mutliGetEntry(Set<String> keys) {
        HashMap<String, byte[]> pairs = new HashMap<>();
        entriesLock.lock();
        try {
            for (String key : keys) 
                pairs.put(key, this.entries.get(key));
            
            return new MultiPutPacket(pairs);
        } finally {
            entriesLock.unlock();
        }
    }



    public PutPacket getEntryWhen(String key, String keyCond, byte[] dataCond) throws InterruptedException {
        PutPacket entry = new PutPacket();

        String compositeKey = keyCond + Arrays.hashCode(dataCond);

        Condition condition;

        whenConditionsLock.lock();
        try {
            condition = this.getWhenConditions.get(compositeKey);
            if (condition == null) {
                condition = entriesLock.newCondition();
                this.getWhenConditions.put(compositeKey, condition);
            }
        } finally {
            whenConditionsLock.unlock();
        }


        entriesLock.lock();
        try {
            byte[] actualData = this.entries.get(keyCond);
            while (actualData == null || !Arrays.equals(dataCond, actualData)) {
                condition.await();
                actualData = this.entries.get(keyCond);
            }

            byte[] data = this.entries.get(key);

            entry.setKey(key);
            entry.setData(data);

            return entry;
        } finally {
            entriesLock.unlock();
        }
    }

    public boolean register(String username, String password) {
        clientsLock.lock();
        try {
            if (this.clients.containsKey(username)) {
                return false;
            }
            this.clients.put(username, password);
            return true;
        } finally {
            clientsLock.unlock();
        }
    }

    public boolean authenticate(String username, String password) throws InterruptedException {
        String existingPassword;
        clientsLock.lock();
        try {
            existingPassword = this.clients.get(username);
        } finally {
            clientsLock.unlock();
        }

        if (existingPassword == null || !existingPassword.equals(password)) {
            return false;
        } 

        sessionsLock.lock();
        try {
            while (this.sessionsCount >= this.S) {
                full.await();
            }
            this.sessionsCount++;

            return true;
        } finally {
            sessionsLock.unlock();
        }
    }

    public void decreaseSessionsCount() {
        sessionsLock.lock();
        try {
            this.sessionsCount--;
            full.signal();
        } finally {
            sessionsLock.unlock();
        }
    }
}
