package client;

import java.io.*;
import java.net.Socket;
import java.util.*;

import entries.*;
import connection.ConnectionManager;
import utils.PacketType;

public class Client {
    private ConnectionManager conn;

    public Client(Socket socket) throws IOException {
       this.conn = new ConnectionManager(socket);
    }


    // This method performs a write operation by sending a key-value pair to the server. 
    // If the key does not exist on the server, a new entry is created with the provided key and value. 
    // If the key already exists, the associated entry is updated with the new value.
    public void put(String key, byte[] value) throws IOException {
        PutPacket putPacket = new PutPacket(key, value);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.PUT, putPacket);

        conn.send(packetWrapper);
    }

    // This method handles multiple write operations by sending a set of key-value pairs to the server.
    // If a key does not exist on the server, a new entry is created with the provided key and value.
    // If a key already exists, the associated entry is updated with the new value.
    public void multiPut(Map<String, byte[]> pairs) throws IOException {
        MultiPutPacket multiPutPacket = new MultiPutPacket(pairs);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.MULTI_PUT, multiPutPacket);

        conn.send(packetWrapper);
    }

    // This method performs a read operation by sending a key to the server.
    // If the key exists on the server, the associated value is returned.
    // If the key does not exist on the server, a null value is returned.
    public byte[] get(String key) throws IOException {
        GetPacket getPacket = new GetPacket(key);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.GET, getPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        PutPacket putPacket = (PutPacket) p.getPacket();
        return putPacket.getData();
    }

    // This method handles multiple read operations by sending a set of keys to the server.
    // For each key that exists on the server, the associated value is returned.
    // For each key that does not exist on the server, a null value is returned.
    public Map<String, byte[]> multiGet(Set<String> keys) throws IOException {
        MultiGetPacket multiGetPacket = new MultiGetPacket(keys);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.MULTI_GET, multiGetPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        MultiPutPacket multiPutPacket = (MultiPutPacket) p.getPacket();
        return multiPutPacket.getPairs();
    }

    // This method performs a conditional write operation by sending a key, a conditional key, and a conditional value to the server.
    // If the conditional key exists on the server and its associated value matches the conditional value, the key-value pair is written to the server.
    // If the conditional key does not exist on the server or its associated value does not match the conditional value, the operation is aborted.
    public byte[] getWhen(String key, String keyCond, byte[] valueCond) throws IOException {
        GetWhenPacket getWhenPacket = new GetWhenPacket(key, keyCond, valueCond);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.GET_WHEN, getWhenPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        PutPacket putPacket = (PutPacket) p.getPacket();
        return putPacket.getData();
    }

    // This method registers a new user with the server by sending a username and password.
    // If the username is not already registered, a new user is created with the provided username and password.
    // If the username is already registered, the operation is aborted.
    public boolean authenticate(String username, String password, int authenticationType) throws IOException {
        AuthPacket auth = new AuthPacket(username, password);
        PacketWrapper packetWrapper = new PacketWrapper(authenticationType, auth);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        AckPacket ackPacket = (AckPacket) p.getPacket();
        return ackPacket.getAck();
    }

    // This method closes the connection to the server.
    public void closeConnection() throws IOException {
        conn.close();
    }

    // This method sends a shutdown request to the server to initiate the server shutdown process.
    public void shutdownServer() throws IOException {
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.SHUTDOWN, null);
        conn.send(packetWrapper);
    }
}


