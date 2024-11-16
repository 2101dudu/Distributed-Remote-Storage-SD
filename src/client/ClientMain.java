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
        try (Socket socket = new Socket("localhost", 8080)) {
            System.out.println("Connected to server!");

            // ask for key from stdin
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Enter key: ");
            String key = reader.readLine();

            // ask for data from stdin
            System.out.print("Enter data: ");
            String data = reader.readLine();
            // reader.close(); ??

            // convert data to []byte
            byte[] value = data.getBytes();

            System.out.print("Asking server to store key: " + key + " and data: ");
            for (byte v : value) {
                System.out.print(v);
            }
            System.out.print("\n");
            Client client = new Client(socket);
            client.put(key, value);

            System.out.println("Asking server to give the data corresponding to the key: " + key);
            byte[] dataFromServer = client.get(key);
            System.out.print("Data received: ");
            for (byte dfs : dataFromServer) {
                System.out.print(dfs);
            }
            System.out.print("\n");

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
