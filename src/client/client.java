package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);

            // in & out socket
            // BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            System.out.println("Connected to server!");
            
            String stringToSend = "1";

            out.println(stringToSend);
            out.flush();

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

