public class ClientMain {
//    public static void main(String[] args) {
//        Client client = new Client();
//        client.put("key", "value".getBytes());
//    }

    public static void main(String[] args) {
        try {
            System.out.println("Connected to server!");

            // in & out socket
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            String stringToSend = "Hello from client!";
            out.println(stringToSend);
            out.flush();

            String stringToRead = in.readLine();
            System.out.println("Received from server: " + stringToRead);

            socket.shutdownOutput();
            socket.shutdownInput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
