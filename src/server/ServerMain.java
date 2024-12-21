package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java ServerMain <MAX_CONCURRENT_USERS>");
            return;
        }

        int S = Integer.parseInt(args[0]);

        Server server = new Server(S); // Create server instance
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
