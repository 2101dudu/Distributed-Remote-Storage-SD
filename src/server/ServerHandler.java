package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyStore.Entry;
import java.util.Arrays;

import entries.SingleEntry;

public class ServerHandler implements Runnable {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
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


            // client put method debug ---------------------------------------------------------------------
            SingleEntry singleEntry = SingleEntry.deserialize(in);
            System.out.println("Entry received from client: " + singleEntry.toString());
            // ---------------------------------------------------------------------------------------------


            // client get method debug ---------------------------------------------------------------------
            String key = in.readUTF();
            // in.close(); ??
            // **[SIMULATION]** get data corresponding to key from map ***************************
            byte[] dataToSend = Arrays.copyOf(singleEntry.getData(),singleEntry.getData().length);
            // ***********************************************************************************
            SingleEntry entryToSend = new SingleEntry(key, dataToSend);
            entryToSend.serialize(out);
            out.flush();
            // out.close(); ??
            // ---------------------------------------------------------------------------------------------
        

        } finally {
            this.socket.close();
        }
    }
}
