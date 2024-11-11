package server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerHandler implements Runnable {
    private Socket socket;

    public ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleConnection() throws IOException {
        BufferedInputStream in = new BufferedInputStream(this.socket.getInputStream());

        byte[] data = new byte[4096];
        int bytesRead = in.read(data);

        if (bytesRead == -1) {
            System.out.println("Client disconnected");
            this.socket.close();
            return;
        } else {
            byte headerLength = data[0];

            System.out.println("Received " + bytesRead + " bytes, header lenth of " + headerLength + " and data " + "\"" + new String(data, 1, bytesRead) + "\"");
        }

        this.socket.shutdownInput();
        this.socket.close();
    }
}
