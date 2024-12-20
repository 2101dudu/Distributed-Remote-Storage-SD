package server;

import entries.PutPacket;

import java.util.HashMap;
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

    public void update(PutPacket entry) {
        lock.lock();
        try {
            this.entries.put(entry.getKey(), entry.getData());
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
        } finally {
            lock.unlock();
        }
        return entry;
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
