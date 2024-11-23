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
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Client client = new Client(socket);
            boolean flag = true;

            while (flag) {
                System.out.println("\nMenu:");
                System.out.println("1. Write in the server map");
                System.out.println("2. Request information from the server map");
                System.out.println("3. Exit");
                System.out.print("Choose a option: ");

                String choice = reader.readLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter key: ");
                        String key = reader.readLine();

                        System.out.print("Enter data: ");
                        String data = reader.readLine();

                        byte[] value = data.getBytes();

                        System.out.println("Asking server to store key: " + key + " and data: " + data);
                        client.put(key, value);
                        break;

                    case "2":
                        System.out.print("Enter key to fetch data: ");
                        String fetchKey = reader.readLine();

                        System.out.println("Asking server for data corresponding to key: " + fetchKey);
                        byte[] dataFromServer = client.get(fetchKey);

                        if (dataFromServer != null && dataFromServer.length > 0) {
                            System.out.print("Data received: ");
                            System.out.println(new String(dataFromServer));
                        } else {
                            System.out.println("No data found for the given key.");
                        }
                        break;

                    case "3":
                        System.out.println("Exiting...");
                        client.closeConnection();
                        flag = false;
                        break;

                    default:
                        System.out.println("Invalid option. Try again.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
