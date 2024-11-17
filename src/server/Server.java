package server;

import entries.SingleEntry;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    private HashMap<String, byte[]> entries;
    private Lock lock = new ReentrantLock();

    public Server() {
        this.entries = new HashMap<>();
    }

    public void update(SingleEntry entry) {
        lock.lock();
        try {
            this.entries.put(entry.getKey(), entry.getData());
        } finally {
            lock.unlock();
        }
    }

    public SingleEntry getEntry(String key) {
        SingleEntry entry = new SingleEntry();
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
