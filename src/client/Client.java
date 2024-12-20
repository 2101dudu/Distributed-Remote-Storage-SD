package client;

import java.io.*;
import java.net.Socket;
import java.util.*;

import entries.*;
import connection.ConnectionManager;

public class Client {
    private ConnectionManager conn;
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public Client(Socket socket) throws IOException {
       this.conn = new ConnectionManager(socket);
    }


    // Operação de escrita, enviando par chave-valor:
    //
    //      void put(String key, byte[] value) 
    //
    // Se a chave não existir, é criada uma nova entrada no servidor, com o par 
    // chave-valor enviado. Caso contrário, a entrada deverá ser atualizada com o novo valor.
    public void put(String key, byte[] value) throws IOException {
        PutPacket putPacket = new PutPacket(key, value);
        PacketWrapper packetWrapper = new PacketWrapper(1, putPacket);

        conn.send(packetWrapper);
    }

    // Operação de escrita composta:
    //
    // void multiPut(Map<String, byte[]> pairs).
    //
    // Todos os pares chave-valor deverão ser atualizados / inseridos
    // atomicamente.
    public void multiPut(Map<String, byte[]> pairs) throws IOException {
        MultiPutPacket multiPutPacket = new MultiPutPacket(pairs);
        PacketWrapper packetWrapper = new PacketWrapper(6, multiPutPacket);

        conn.send(packetWrapper);
    }

    // Operação de leitura:
    //
    // byte[] get(String key)
    //
    // Para uma chave key, deverá devolver ao cliente o respetivo valor,
    // ou null caso a chave não exista.
    public byte[] get(String key) throws IOException {
        GetPacket getPacket = new GetPacket(key);
        PacketWrapper packetWrapper = new PacketWrapper(2, getPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        PutPacket putPacket = (PutPacket) p.getPacket();
        return putPacket.getData();
    }

    // Operação de leitura composta:
    //
    // Map<String, byte[]> multiGet(Set<String> keys).
    //
    // Dado um conjunto de chaves, devolve o conjunto de pares chave-valor 
    // respetivo.
    public Map<String, byte[]> multiGet(Set<String> keys) throws IOException {
        MultiGetPacket multiGetPacket = new MultiGetPacket(keys);
        PacketWrapper packetWrapper = new PacketWrapper(7, multiGetPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        MultiPutPacket multiPutPacket = (MultiPutPacket) p.getPacket();
        return multiPutPacket.getPairs();
    }
  
    private boolean authenticate(String username, String password, int authenticationType) throws IOException {
        AuthPacket auth = new AuthPacket(username, password);
        PacketWrapper packetWrapper = new PacketWrapper(authenticationType, auth);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        AckPacket ackPacket = (AckPacket) p.getPacket();
        return ackPacket.getAck();
    }

    public void closeConnection() throws IOException {
        conn.close();
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
                    boolean accountCreated = this.authenticate(username, password, 3);

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
                    boolean authenticated = this.authenticate(username, password, 4);

                    // if client was able to log in, show main menu
                    if (authenticated) {
                        this.mainMenu();
                    } else {
                        System.out.println("Authentication failed.");
                    }
                    break;

                case "3":
                    System.out.println("Exiting...");
                    this.closeConnection();
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

                    this.put(key, value);
                    break;

                case "2":
                    System.out.print("Enter key to fetch data: ");
                    String fetchKey = this.reader.readLine();

                    byte[] dataFromServer = this.get(fetchKey);

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

                    this.multiPut(pairs);
                    break;

                case "4":
                    System.out.print("How many keys do you want to retrieve information from: ");
                    int numKeys = Integer.parseInt(this.reader.readLine());

                    Set<String> keys = new HashSet<>();
                    for (int i = 0; i < numKeys; i++) {
                        System.out.print("Enter the key you want to fetch data from: ");
                        keys.add(this.reader.readLine());
                    }
                    
                    Map<String, byte[]> multiDataFromServer = this.multiGet(keys);

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


