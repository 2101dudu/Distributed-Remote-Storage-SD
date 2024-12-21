package server;

import java.io.*;
import java.net.Socket;

import entries.*;
import connection.ConnectionManager;
import utils.PacketType;

public class ServerHandler implements Runnable {
    private ConnectionManager conn;
    private Server server;

    public ServerHandler(Socket socket, Server server) throws IOException {
        this.conn = new ConnectionManager(socket);
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
        boolean error = false;
        while (!error) {
            try {
                PacketWrapper packetWrapper = conn.receive();

                int packetType = packetWrapper.getType();
                Object packetData = packetWrapper.getPacket();

                switch (packetType) {
                    case PacketType.PUT:
                        PutPacket receivedPutPacket = (PutPacket) packetData;

                        server.update(receivedPutPacket.getKey(), receivedPutPacket.getData());
                        break;

                    case PacketType.GET:
                        GetPacket receivedGetPacket = (GetPacket) packetData;

                        PutPacket putPacket = server.getEntry(receivedGetPacket.getKey());
                        PacketWrapper getPacketWrapper = new PacketWrapper(1, putPacket);

                        conn.send(getPacketWrapper);
                        break;

                    case PacketType.REGISTER:
                        AuthPacket regPacket = (AuthPacket) packetData;

                        boolean registered = server.register(regPacket.getUsername(), regPacket.getPassword());
                        AckPacket ackPacket = new AckPacket(registered);
                        PacketWrapper registerPacketWrapper = new PacketWrapper(5, ackPacket);
                        
                        conn.send(registerPacketWrapper);
                        break;

                    case PacketType.LOGIN:
                        AuthPacket loginPacket = (AuthPacket) packetData;

                        boolean loggedIn = server.authenticate(loginPacket.getUsername(), loginPacket.getPassword());
                        AckPacket loginAckPacket = new AckPacket(loggedIn);
                        PacketWrapper loginPacketWrapper = new PacketWrapper(5, loginAckPacket);
                        
                        conn.send(loginPacketWrapper);
                        break;

                    case PacketType.ACK:
                        break;
                    
                    case PacketType.MULTI_PUT:
                        MultiPutPacket receivedMultiPutPacket = (MultiPutPacket) packetData;
    
                        server.multiUpdate(receivedMultiPutPacket.getPairs());
                        break;
                    
                    case PacketType.MULTI_GET:
                        MultiGetPacket receivedMultiGetPacket = (MultiGetPacket) packetData;
    
                        MultiPutPacket multiPutPacket = server.mutliGetEntry(receivedMultiGetPacket.getKeys());
                        PacketWrapper multiPutPacketWrapper = new PacketWrapper(6, multiPutPacket);

                        conn.send(multiPutPacketWrapper);
                        break;

                    default:
                        System.out.println("Entry type invalid");
                        break;
                }
            } catch (IOException e ) {
                System.out.println("Closing connection");
                error = true;
            }
        }
    }
}
