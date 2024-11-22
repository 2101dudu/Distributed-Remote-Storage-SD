package server;

import entries.PutPacket;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private HashMap<String, byte[]> entries;
    private Lock lock = new ReentrantLock();

    public Server() {
        this.entries = new HashMap<>();
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
}
