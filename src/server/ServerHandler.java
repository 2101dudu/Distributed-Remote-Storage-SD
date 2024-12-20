package server;

import java.io.*;
import java.net.Socket;

import entries.GetPacket;
import entries.PacketWrapper;
import entries.PutPacket;

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
                    Object packet = PacketWrapper.deserialize(in);
                    switch (packet.getClass().getSimpleName()) {
                        case "PutPacket":
                            PutPacket putPacket = (PutPacket) packet;
                            System.out.println("Entry received from client: " + putPacket.toString());
                            server.update(putPacket.getKey(), putPacket.getData());
                            break;
                        case "GetPacket":
                            GetPacket getPacket = (GetPacket) packet;
                            server.getEntry(getPacket.getKey()).serialize(out);
                            out.flush();
                            break;
                        case 5: // MultiPut
                            MultiPutPacket receivedMultiPutPacket = (MultiPutPacket) packetData;
    
                            server.multiUpdate(receivedMultiPutPacket.getPairs());
                            break;
                        case 6: // MultiGet
                            MultiGetPacket receivedMultiGetPacket = (MultiGetPacket) packetData;
    
                            MultiPutPacket multiPutPacket = server.mutliGetEntry(receivedMultiGetPacket.getKeys());
                            PacketWrapper multiPutPacketWrapper = new PacketWrapper(6, multiPutPacket);

                            conn.send(multiPutPacketWrapper);
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