package client;

import java.io.IOException;
import java.net.Socket;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket("localhost", 8080)) {
            Client client = new Client(socket);
            ClientHandler clientHandler = new ClientHandler(client);
            clientHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
