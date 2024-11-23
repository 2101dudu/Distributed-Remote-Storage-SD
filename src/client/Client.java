package client;

import java.io.*;
import java.net.Socket;

import entries.GetPacket;
import entries.PacketWrapper;
import entries.PutPacket;

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
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));

        PutPacket putPacket = new PutPacket(key, value);
        PacketWrapper packetWrapper = new PacketWrapper(1, putPacket);
        System.out.println(putPacket.toString());
        packetWrapper.serialize(out);
        out.flush();
        // out.close(); ??
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
        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(this.socket.getOutputStream()));
        DataInputStream in = new DataInputStream(new BufferedInputStream(this.socket.getInputStream()));

        GetPacket getPacket = new GetPacket(key);
        PacketWrapper packetWrapper = new PacketWrapper(2, getPacket);
        packetWrapper.serialize(out);
        out.flush();
        //out.close(); ??

        PutPacket putPacket = PutPacket.deserialize(in);
        //in.close(); ??

        return putPacket.getData();
    }

    public void closeConnection() throws IOException {
        this.socket.close();
    }
}

