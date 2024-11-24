package server;

import java.io.*;
import java.net.Socket;

import entries.GetPacket;
import entries.PacketWrapper;
import entries.PutPacket;
import entries.AckPacket;
import entries.AuthPacket;

public class ServerHandler implements Runnable {
    private Socket socket;
    private Server server;

    public ServerHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
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
                    PacketWrapper packetWrapper = PacketWrapper.deserialize(in);
                    Object packet = packetWrapper.getPacket();
                    switch (packetWrapper.getType()) {
                        case 1: // Put
                            PutPacket putPacket = (PutPacket) packet;
                            System.out.println("Entry received from client: " + putPacket.toString());
                            server.update(putPacket);
                            break;
                        case 2: // Get
                            GetPacket getPacket = (GetPacket) packet;
                            server.getEntry(getPacket.getKey()).serialize(out);
                            out.flush();
                            break;
                        case 3: // Register
                            AuthPacket regPacket = (AuthPacket) packet;
                            System.out.println("Reg packet received");
                            AckPacket ackPacket = new AckPacket(server.register(regPacket.getUsername(), regPacket.getPassword()));
                            ackPacket.serialize(out);
                            out.flush();
                            break;
                        case 4: // Login
                            AuthPacket loginPacket = (AuthPacket) packet;
                            System.out.println("Login packet received");
                            ackPacket = new AckPacket(server.authenticate(loginPacket.getUsername(), loginPacket.getPassword()));
                            ackPacket.serialize(out);
                            out.flush();
                            break;
                        default:
                            System.out.println("Entry type invalid");
                            break;
                    }
                } catch (EOFException e) {
                    System.out.println("Closing connection");
                    flag = false;
                }
            }
        } finally {
            this.socket.close();
        }
    }
}