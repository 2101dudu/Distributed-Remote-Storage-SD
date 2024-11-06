package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(8080)) {
            System.out.println("Server starting...");

            while (true) {
                try {
                    handleConnection(ss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(ServerSocket ss) throws IOException {
        Socket socket = ss.accept();

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());

        String stringToRead = in.readLine();
        System.out.println("Received from client: " + stringToRead);

        String stringToSend = "Hello from server!";
        out.println(stringToSend);
        out.flush();

        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
    }
}
