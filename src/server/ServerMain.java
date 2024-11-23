package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        Server server = new Server(); // Create server instance
        try (ServerSocket ss = new ServerSocket(8080)) {
            System.out.println("Server starting...");
            
            while (true) {
                Socket socket = ss.accept();
                Thread thread = new Thread(new ServerHandler(socket, server)); // Pass server instance
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
