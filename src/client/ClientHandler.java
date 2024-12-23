package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

import utils.PacketType;
import utils.LogWriter;

public class ClientHandler {
    private final Client client;
    private final LogWriter logWriter = new LogWriter();
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private final Lock lock = new ReentrantLock();
    private final Condition exitCondition = lock.newCondition();

    private int numberOfOperations = 0;


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
            System.out.println("4. Shutdown server");
            System.out.print("Choose an option: ");

            String choice = this.reader.readLine();

            switch (choice) {
                case "1":
                    createAccount();
                    break;

                case "2":
                    login();
                    break;

                case "3":
                    lock.lock();
                    try {
                        System.out.println("Exiting...");

                        while (numberOfOperations > 0) {
                            System.out.println("Waiting for operations to finish...");
                            exitCondition.await();
                        }

                        this.client.closeConnection();
                        exited = true;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
                    break;

                case "4":
                    lock.lock();
                    try {
                        System.out.println("Shutting down server...");

                        while (numberOfOperations > 0) {
                            System.out.println("Waiting for operations to finish...");
                            exitCondition.await();
                        }

                        this.client.shutdownServer();
                        exited = true;

                        this.client.closeConnection();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        lock.unlock();
                    }
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
            System.out.println("1. Write in the server's map (put)");
            System.out.println("2. Request information from the server's map (get)");
            System.out.println("3. Write multiple information in the server's map (multiPut)");
            System.out.println("4. Request multiple information from the server's map (multiGet)");
            System.out.println("5. Request information from the setver's map when a condition is met (getWhen)");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            String choice = this.reader.readLine();

            switch (choice) {
                case "1":
                    putData();
                    break;

                case "2":
                    getData();
                    break;

                case "3":
                    multiPutData();
                    break;

                case "4":
                    multiGetData();
                    break;

                case "5":
                    getWhenCondition();
                    break;

                case "6":
                    System.out.println("Exiting...");
                    exited = true;
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    private void createAccount() throws IOException {
        System.out.print("Enter username: ");
        String username = reader.readLine();

        System.out.print("Enter password: ");
        String password = reader.readLine();

        System.out.println("Creating account...");
        try {
            boolean accountCreated = client.authenticate(username, password, PacketType.REGISTER);

        if (accountCreated) {
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Account creation failed.");
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void login() throws IOException {
        System.out.print("Enter username: ");
        String username = this.reader.readLine();

        System.out.print("Enter password: ");
        String password = this.reader.readLine();

        System.out.println("Logging in...");
        boolean authenticated = this.client.authenticate(username, password, PacketType.LOGIN);

        // if client was able to log in, show main menu
        if (authenticated) {
            this.mainMenu();
        } else {
            System.out.println("Authentication failed.");
        }
    }

    private void putData() throws IOException {
        System.out.print("Enter key: ");
        String key = reader.readLine();

        System.out.print("Enter data: ");
        String data = reader.readLine();

        byte[] value = data.getBytes();

        Thread putThread = new Thread(() -> {
            addOperation();
            try {
                client.put(key, value);
                logWriter.println("Data successfully written to the server.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            removeOperation();
        });
        putThread.start();
    }

    private void multiPutData() throws IOException {
        HashMap<String, byte[]> pairs = new HashMap<>();

        System.out.print("How many entries do you want to put: ");
        int numEntries = Integer.parseInt(reader.readLine());

        for (int i = 0; i < numEntries; i++) {
            System.out.print("Enter key: ");
            String multiPutKey = reader.readLine();

            System.out.print("Enter data: ");
            String multiPutData = reader.readLine();

            byte[] multiPutValue = multiPutData.getBytes();
            pairs.put(multiPutKey, multiPutValue);
        }

        Thread multiPutThread = new Thread(() -> {
            addOperation();
            try {
            client.multiPut(pairs);
            logWriter.println("Data successfully written to the server.");
            } catch (IOException e) {
                e.printStackTrace();
            }
            removeOperation();
        });
        multiPutThread.start();
    }

    private void getData() throws IOException {
        System.out.print("Enter key to fetch data: ");
        String key = reader.readLine();

        Thread getThread = new Thread(() -> {
            addOperation();
            try {
            byte[] data = client.get(key);

            if (data != null && data.length > 0) {
                logWriter.println("Data received: " + new String(data));
            } else {
                logWriter.println("No data found for the given key.");
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            removeOperation();
        });
        getThread.start();
    }

    private void multiGetData() throws IOException {
        System.out.print("How many keys do you want to retrieve information from: ");
        int numKeys = Integer.parseInt(reader.readLine());

        Set<String> keys = new HashSet<>();
        for (int i = 0; i < numKeys; i++) {
            System.out.print("Enter the key you want to fetch data from: ");
            keys.add(reader.readLine());
        }

        Thread multiGetThread = new Thread(() -> {
            addOperation();
            try {
            Map<String, byte[]> multiDataFromServer = client.multiGet(keys);

            for (Map.Entry<String, byte[]> entry : multiDataFromServer.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length > 0) {
                    logWriter.println("Data received: " + new String(entry.getValue()));
                } else {
                    logWriter.println("No data found for the given key.");
                }
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            removeOperation();
        });
        multiGetThread.start();
    }

    private void getWhenCondition() throws IOException {
        System.out.print("Enter key to fetch data from: ");
        String keyWhen = reader.readLine();

        System.out.print("Enter key condition: ");
        String keyCond = reader.readLine();

        System.out.print("Enter data condition: ");
        String dataCond = reader.readLine();

        byte[] dataCondBytes = dataCond.getBytes();

        Thread getWhenThread = new Thread(() -> {
            addOperation();
            try {
            byte[] dataWhen = client.getWhen(keyWhen, keyCond, dataCondBytes);

            if (dataWhen != null && dataWhen.length > 0) {
                logWriter.println("Data received: " + new String(dataWhen));
            } else {
                logWriter.println("No data found for the given key.");
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
            removeOperation();
        });
        getWhenThread.start();
    }

    public void addOperation() {
        lock.lock();
        try {
            numberOfOperations++;
        } finally {
            lock.unlock();
        }
    }

    public void removeOperation() {
        lock.lock();
        try {
            numberOfOperations--;
            exitCondition.signal();
        } finally {
            lock.unlock();
        }
    }
}
