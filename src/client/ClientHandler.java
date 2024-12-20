package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class ClientHandler {
    private Client client;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public ClientHandler(Client client) throws IOException {
        this.client = client;
    }
    
    public void start() throws IOException {
        this.loginMenu();
    }

    private void loginMenu() throws IOException {
        boolean exited = false;

        while (!exited) {
            System.out.println("\nMenu:");
            System.out.println("1. Create account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = this.reader.readLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter username: ");
                    String username = this.reader.readLine();

                    System.out.print("Enter password: ");
                    String password = this.reader.readLine();

                    System.out.println("Creating account...");
                    boolean accountCreated = this.client.authenticate(username, password, 3);

                    if (accountCreated) {
                        System.out.println("Account created successfully.");
                    } else {
                        System.out.println("Account creation failed.");
                    }

                    break;

                case "2":
                    System.out.print("Enter username: ");
                    username = this.reader.readLine();

                    System.out.print("Enter password: ");
                    password = this.reader.readLine();

                    System.out.println("Logging in...");
                    boolean authenticated = this.client.authenticate(username, password, 4);

                    // if client was able to log in, show main menu
                    if (authenticated) {
                        this.mainMenu();
                    } else {
                        System.out.println("Authentication failed.");
                    }
                    break;

                case "3":
                    System.out.println("Exiting...");
                    this.client.closeConnection();
                    exited = true;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    private void mainMenu() throws IOException {
        boolean exited = false;

        while (!exited) {
            System.out.println("\nMenu:");
            System.out.println("1. Write in the server's map");
            System.out.println("2. Request information from the server's map");
            System.out.println("3. Write multiple information in the server's map");
            System.out.println("4. Request multiple information from the server's map");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            String choice = this.reader.readLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter key: ");
                    String key = this.reader.readLine();

                    System.out.print("Enter data: ");
                    String data = this.reader.readLine();

                    byte[] value = data.getBytes();

                    this.client.put(key, value);
                    break;

                case "2":
                    System.out.print("Enter key to fetch data: ");
                    String fetchKey = this.reader.readLine();

                    byte[] dataFromServer = this.client.get(fetchKey);

                    if (dataFromServer != null && dataFromServer.length > 0) {
                        System.out.print("Data received: ");
                        System.out.println(new String(dataFromServer));
                    } else {
                        System.out.println("No data found for the given key.");
                    }
                    break;

                case "3":
                    HashMap<String, byte[]> pairs = new HashMap<>();

                    System.out.print("How many entries do you want to put: ");
                    int numEntries = Integer.parseInt(this.reader.readLine());
                    
                    for (int i = 0; i < numEntries; i++) {
                        System.out.print("Enter key: ");
                        String multiPutkey = this.reader.readLine();

                        System.out.print("Enter data: ");
                        String multiPutdata = this.reader.readLine();

                        byte[] multiPutvalue = multiPutdata.getBytes();

                        pairs.put(multiPutkey, multiPutvalue);
                    }

                    this.client.multiPut(pairs);
                    break;

                case "4":
                    System.out.print("How many keys do you want to retrieve information from: ");
                    int numKeys = Integer.parseInt(this.reader.readLine());

                    Set<String> keys = new HashSet<>();
                    for (int i = 0; i < numKeys; i++) {
                        System.out.print("Enter the key you want to fetch data from: ");
                        keys.add(this.reader.readLine());
                    }
                    
                    Map<String, byte[]> multiDataFromServer = this.client.multiGet(keys);

                    for (Map.Entry<String, byte[]> entry : multiDataFromServer.entrySet()) {
                        if (entry.getValue() != null && entry.getValue().length > 0) {
                            System.out.print("Data received: ");
                            System.out.println(new String(entry.getValue()));
                        } else {
                            System.out.println("No data found for the given key.");
                        }
                    }
                    break;

                case "5":
                    System.out.println("Exiting...");
                    exited = true;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }
}
