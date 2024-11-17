package server;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore.Entry;
import java.util.Arrays;

import entries.AtomicGetPacket;
import entries.CloseConnectionPacket;
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
                try {
                    Object packet = Packet.deserialize(in);
                    if (packet instanceof SingleEntry) {
                        SingleEntry singleEntry = (SingleEntry) packet;
                        System.out.println("Entry received from client: " + singleEntry.toString());
                        server.update(singleEntry);
                    } else if (packet instanceof AtomicGetPacket) {
                        AtomicGetPacket atomicGetPacket = (AtomicGetPacket) packet;
                        server.getEntry(atomicGetPacket.getKey()).serialize(out);
                        out.flush();
                    } else if (packet instanceof CloseConnectionPacket) {
                        System.out.println("Closing connection as requested by client.");
                        flag = false;
                    } else {
                        System.out.println("Entry type invalid");
                    }
                } catch (EOFException e) {
                    flag = false;
                }
            }
        } finally {
            this.socket.close();
        }
    }
}