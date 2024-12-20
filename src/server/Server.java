package server;

import entries.*;

import java.util.*;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private HashMap<String, byte[]> entries;
    private HashMap<String, String> clients;
    private Lock lock = new ReentrantLock();

    public Server() {
        this.entries = new HashMap<>();
        this.clients = new HashMap<>();
    }

    public void update(String key, byte[] data) {
        lock.lock();
        try {
            this.entries.put(key, data);
        } finally {
            lock.unlock();
        }
    }

    public void multiUpdate(HashMap<String, byte[]> pairs) {
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
            entry.setData(this.entries.get(key));
            
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

    public boolean authenticate(String username, String password) {
        lock.lock();
        try {
            String existingPassword = this.clients.get(username);
            return existingPassword != null && existingPassword.equals(password);
        } finally {
            lock.unlock();
        }
    }
}
