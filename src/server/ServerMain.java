package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            System.out.println("Usage: java ServerMain <MAX_CONCURRENT_USERS> <FILE_PATH> ['--load']");
            return;
        }

        int S = Integer.parseInt(args[0]);
        String filePath = args[1];

        // Create an empty server instance and read the state from the file
        Server server = new Server();
        if (args.length == 3 && args[2].equals("--load")) server = Server.readState(filePath); 
        server.setS(S);
        server.running();

        try (ServerSocket ss = new ServerSocket(8080)) {
            System.out.println("Server starting...");
            
            while (true) {
                Socket socket = ss.accept();
                if (!server.isRunning()) break; // check if the server is still running

                Thread thread = new Thread(new ServerHandler(socket, server)); // Pass server instance
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Server shutting down...");
        server.writeState(filePath); // save the server's state to a file
    }
}
