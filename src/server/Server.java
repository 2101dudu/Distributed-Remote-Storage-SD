package server;

import entries.*;

import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class Server {
    private HashMap<String, byte[]> entries;
    private HashMap<String, String> clients;

    private int sesionsCount;
    private int S;

    private Lock lock = new ReentrantLock();
    private Condition full = lock.newCondition();

    public Server(int s) {
        this.entries = new HashMap<>();
        this.clients = new HashMap<>();

        this.sesionsCount = 0;
        this.S = s;
    }

    public void update(String key, byte[] data) {
        lock.lock();
        try {
            this.entries.put(key, data);
        } finally {
            lock.unlock();
        }
    }

    public void multiUpdate(Map<String, byte[]> pairs) {
        lock.lock();
        try {
            this.entries.putAll(pairs);
        } finally {
            lock.unlock();
        }
    }

    public PutPacket getEntry(String key) {
        PutPacket entry = new PutPacket();
        lock.lock();
        try {
            entry.setKey(key);
            byte[] data = this.entries.get(key);
            entry.setData(data);
            
            return entry;
        } finally {
            lock.unlock();
        }
    }

    public MultiPutPacket mutliGetEntry(Set<String> keys) {
        HashMap<String, byte[]> pairs = new HashMap<>();
        lock.lock();
        try {
            for (String key : keys) 
                pairs.put(key, this.entries.get(key));
            
            return new MultiPutPacket(pairs);
        } finally {
            lock.unlock();
        }
    }

    public boolean register(String username, String password) {
        lock.lock();
        try {
            if (this.clients.containsKey(username)) {
                return false;
            }
            this.clients.put(username, password);
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean authenticate(String username, String password) throws InterruptedException {
        lock.lock();
        try {
            String existingPassword = this.clients.get(username);
            if (existingPassword == null || !existingPassword.equals(password)) {
                return false;
            } 

            while (this.sesionsCount >= this.S) {
                full.await();
            }
            this.sesionsCount++;

            return true;
        } finally {
            lock.unlock();
        }
    }

    public void decreaseSessionsCount() {
        lock.lock();
        try {
            this.sesionsCount--;
            full.signal();
        } finally {
            lock.unlock();
        }
    }
}
