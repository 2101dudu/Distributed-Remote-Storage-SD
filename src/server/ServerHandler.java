package server;

import java.io.*;
import java.net.Socket;

import entries.*;
import connection.ConnectionManager;
import utils.PacketType;

import exceptions.ShutdownException;

public class ServerHandler implements Runnable {
    private boolean clientHasLoggedIn;

    private Server server;
    private ConnectionManager conn;

    public ServerHandler(Socket socket, Server server) throws IOException {
        this.clientHasLoggedIn = false;
        
        this.server = server;
        this.conn = new ConnectionManager(socket);
    }

    @Override
    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ShutdownException e) {
            server.shutdown(); // set the isRunning flag to false
        }
    }

    private void handleConnection() throws IOException, ShutdownException {
        boolean left = false; // flag to check if the client has left
        while (!left) {
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
                        PacketWrapper getPacketWrapper = new PacketWrapper(PacketType.PUT, putPacket);

                        conn.send(getPacketWrapper);
                        break;

                    case PacketType.REGISTER:
                        AuthPacket regPacket = (AuthPacket) packetData;

                        boolean registered = server.register(regPacket.getUsername(), regPacket.getPassword());
                        AckPacket ackPacket = new AckPacket(registered);
                        PacketWrapper registerPacketWrapper = new PacketWrapper(PacketType.ACK, ackPacket);
                        
                        conn.send(registerPacketWrapper);
                        break;

                    case PacketType.LOGIN:
                        AuthPacket loginPacket = (AuthPacket) packetData;

                        boolean loggedIn = server.authenticate(loginPacket.getUsername(), loginPacket.getPassword());
                        AckPacket loginAckPacket = new AckPacket(loggedIn);
                        PacketWrapper loginPacketWrapper = new PacketWrapper(PacketType.ACK, loginAckPacket);

                        this.clientHasLoggedIn = loggedIn;
                        
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
                        PacketWrapper multiPutPacketWrapper = new PacketWrapper(PacketType.MULTI_PUT, multiPutPacket);

                        conn.send(multiPutPacketWrapper);
                        break;

                    case PacketType.GET_WHEN:
                        GetWhenPacket receivedGetWhenPacket = (GetWhenPacket) packetData;

                        PutPacket getWhenPutPacket = server.getEntryWhen(receivedGetWhenPacket.getKey(), receivedGetWhenPacket.getKeyCond(), receivedGetWhenPacket.getDataCond());
                        PacketWrapper getWhenPacketWrapper = new PacketWrapper(PacketType.PUT, getWhenPutPacket);

                        conn.send(getWhenPacketWrapper);
                        break;

                    case PacketType.SHUTDOWN:
                        throw new ShutdownException();

                    default:
                        System.out.println("Entry type invalid");
                        break;
                }
            } catch (IOException e ) {
                System.out.println("Closing connection");

                // if the client did not log in, no session was created, thus, no need to decrease the session count
                if (clientHasLoggedIn) 
                    server.decreaseSessionsCount();

                left = true; // client has left
            } catch ( InterruptedException e) {
                e.printStackTrace();
                left = true; // client has left
            }

        }
    }
}
