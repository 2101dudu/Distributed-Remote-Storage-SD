package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientMain {
//    public static void main(String[] args) {
//        Client client = new Client();
//        client.put("key", "value".getBytes());
//    }

    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 8080)) {
            Client client = new Client(socket);
            
            client.start();
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
