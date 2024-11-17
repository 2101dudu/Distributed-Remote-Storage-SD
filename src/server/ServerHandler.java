package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore.Entry;
import java.util.Arrays;

import entries.AtomicGetPacket;
import entries.Packet;
import entries.SingleEntry;

public class ServerHandler implements Runnable {
    private Socket socket;
    private Server server;

    public ServerHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server; // INITIALIZED SERVER INSTANCE BECAUSE IN JAVA IT IS REQUIRED 
    }

    @Override
    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection() throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
            DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()))) {

            boolean flag = true;
            while (flag) {
                System.out.println("Handler");
                Object packet = Packet.deserialize(in);
                System.out.println("Object deserialized!");
                if (packet instanceof SingleEntry) {
                    SingleEntry singleEntry = (SingleEntry) packet;
                    System.out.println("Entry received from client: " + singleEntry.toString());
                    server.update(singleEntry);
                    break;
                } else if (packet instanceof AtomicGetPacket) {
                    AtomicGetPacket atomicGetPacket = (AtomicGetPacket) packet;
                    server.getEntry(atomicGetPacket.getKey()).serialize(out);
                    out.flush();
                    break;
                } else {
                    System.out.println("Entry type invalid");
                    break;
                }
            }
        } finally {
            this.socket.close();
        }
    }
}
