package client;

import java.io.*;
import java.net.Socket;

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

    // [ATENÇÃO] A implementação do método put() não está a ter em conta
    // diferentes tipos de mensagens, mais propriamente, diferentes headers.
    public void put(String key, byte[] value) throws IOException {
        PutPacket putPacket = new PutPacket(key, value);
        PacketWrapper packetWrapper = new PacketWrapper(1, putPacket);

        System.out.println(putPacket);

        conn.send(packetWrapper);
    }


    // Operação de leitura:
    //
    // byte[] get(String key)
    //
    // Para uma chave key, deverá devolver ao cliente o respetivo valor,
    // ou null caso a chave não exista.


    // [ATENÇÃO] A implementação do método get() não está a ter em conta
    // diferentes tipos de mensagens, mais propriamente, diferentes headers.
    public byte[] get(String key) throws IOException {
        GetPacket getPacket = new GetPacket(key);
        PacketWrapper packetWrapper = new PacketWrapper(2, getPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        PutPacket putPacket = (PutPacket) p.getPacket();
        return putPacket.getData();
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
            System.out.print("Choose a option: ");

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
            System.out.println("1. Write in the server map");
            System.out.println("2. Request information from the server map");
            System.out.println("3. Exit");
            System.out.print("Choose a option: ");

            String choice = this.reader.readLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter key: ");
                    String key = this.reader.readLine();

                    System.out.print("Enter data: ");
                    String data = this.reader.readLine();

                    byte[] value = data.getBytes();

                    System.out.println("Asking server to store key: " + key + " and data: " + data);
                    this.put(key, value);
                    break;

                case "2":
                    System.out.print("Enter key to fetch data: ");
                    String fetchKey = this.reader.readLine();

                    System.out.println("Asking server for data corresponding to key: " + fetchKey);
                    byte[] dataFromServer = this.get(fetchKey);

                    if (dataFromServer != null && dataFromServer.length > 0) {
                        System.out.print("Data received: ");
                        System.out.println(new String(dataFromServer));
                    } else {
                        System.out.println("No data found for the given key.");
                    }
                    break;

                case "3":
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

