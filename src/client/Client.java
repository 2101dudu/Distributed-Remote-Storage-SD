package client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
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
        try (OutputStream out = new BufferedOutputStream(this.socket.getOutputStream())) {
            byte[] header = key.getBytes(StandardCharsets.UTF_8);
            if (header.length > 255) throw new IllegalArgumentException("Key length exceeds 255 bytes");

            // criação do array de bytes a enviar
            // [headerLength, header[0], header[1], ..., header[headerLength-1], value[0], value[1], ..., value[n]]
            byte headerLength = (byte) header.length;
            byte[] message = new byte[1 + header.length + value.length];
            message[0] = headerLength;
            System.arraycopy(header, 0, message, 1, header.length);
            System.arraycopy(value, 0, message, 1 + header.length, value.length);

            out.write(message);
            out.flush();
        } finally {
            this.socket.close();
        }
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
        try (OutputStream out = new BufferedOutputStream(this.socket.getOutputStream());
                InputStream in = new BufferedInputStream(this.socket.getInputStream())) {

            byte[] header = key.getBytes(StandardCharsets.UTF_8);
            if (header.length > 255) throw new IllegalArgumentException("Key length exceeds 255 bytes");

            out.write(header);
            out.flush();

            byte[] data = new byte[4096];
            int bytesRead = in.read(data);

            if (bytesRead == -1) return null;
            return data;
        } finally {
            this.socket.close();
        } 
    }
}

