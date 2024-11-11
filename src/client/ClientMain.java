package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientMain {
//    public static void main(String[] args) {
//        Client client = new Client();
//        client.put("key", "value".getBytes());
//    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);

            System.out.println("Connected to server!");

            // ask for key from stdin
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter key: ");
            String key = reader.readLine();

            // ask for data from stdin
            System.out.print("Enter data: ");
            String data = reader.readLine();

            // convert data to []byte
            byte[] value = data.getBytes();

            Client client = new Client(socket);
            client.put(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
