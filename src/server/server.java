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
                    Socket socket = ss.accept();
                    handleConnection(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleConnection(Socket socket) throws IOException {
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
