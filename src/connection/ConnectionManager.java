package connection;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import entries.PacketWrapper;

public class ConnectionManager {
    private final Socket socket;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private final DataInputStream in;
    private final DataOutputStream out; 

    public ConnectionManager(Socket socket) throws IOException {
        this.socket = socket;

        InputStream socketIn = socket.getInputStream();
        this.in = new DataInputStream(new BufferedInputStream(socketIn));

        OutputStream socketOut = socket.getOutputStream();
        this.out = new DataOutputStream(new BufferedOutputStream(socketOut));
    }

    public void send(PacketWrapper p) throws IOException {
        writeLock.lock();
        try {
            p.serialize(this.out);
            out.flush();
        } finally {
            writeLock.unlock();
        }
    }

    public PacketWrapper receive() throws IOException {
        readLock.lock();
        try {
            return PacketWrapper.deserialize(this.in);
        } finally {
            readLock.unlock();
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }
}
