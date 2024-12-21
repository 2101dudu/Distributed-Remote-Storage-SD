package client;

import java.io.*;
import java.net.Socket;
import java.util.*;

import entries.*;
import connection.ConnectionManager;
import utils.PacketType;

public class Client {
    private ConnectionManager conn;

    public Client(Socket socket) throws IOException {
       this.conn = new ConnectionManager(socket);
    }


    // Operação de escrita, enviando par chave-valor:
    //
    //      void put(String key, byte[] value) 
    //
    // Se a chave não existir, é criada uma nova entrada no servidor, com o par 
    // chave-valor enviado. Caso contrário, a entrada deverá ser atualizada com o novo valor.
    public void put(String key, byte[] value) throws IOException {
        PutPacket putPacket = new PutPacket(key, value);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.PUT, putPacket);

        conn.send(packetWrapper);
    }

    // Operação de escrita composta:
    //
    // void multiPut(Map<String, byte[]> pairs).
    //
    // Todos os pares chave-valor deverão ser atualizados / inseridos
    // atomicamente.
    public void multiPut(Map<String, byte[]> pairs) throws IOException {
        MultiPutPacket multiPutPacket = new MultiPutPacket(pairs);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.MULTI_PUT, multiPutPacket);

        conn.send(packetWrapper);
    }

    // Operação de leitura:
    //
    // byte[] get(String key)
    //
    // Para uma chave key, deverá devolver ao cliente o respetivo valor,
    // ou null caso a chave não exista.
    public byte[] get(String key) throws IOException {
        GetPacket getPacket = new GetPacket(key);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.GET, getPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        PutPacket putPacket = (PutPacket) p.getPacket();
        return putPacket.getData();
    }

    // Operação de leitura composta:
    //
    // Map<String, byte[]> multiGet(Set<String> keys).
    //
    // Dado um conjunto de chaves, devolve o conjunto de pares chave-valor 
    // respetivo.
    public Map<String, byte[]> multiGet(Set<String> keys) throws IOException {
        MultiGetPacket multiGetPacket = new MultiGetPacket(keys);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.MULTI_GET, multiGetPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        MultiPutPacket multiPutPacket = (MultiPutPacket) p.getPacket();
        return multiPutPacket.getPairs();
    }

    // Leitura condicional:
    //
    // byte[] getWhen(String key, String keyCond, byte[] valueCond).
    //
    // Deverá ser devolvido o valor da chave key quando o valor relativo à
    // chave keyCond seja igual a valueCond, devendo a operação ficar bloqueada
    // até tal acontecer.
    public byte[] getWhen(String key, String keyCond, byte[] valueCond) throws IOException {
        GetWhenPacket getWhenPacket = new GetWhenPacket(key, keyCond, valueCond);
        PacketWrapper packetWrapper = new PacketWrapper(PacketType.GET_WHEN, getWhenPacket);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        PutPacket putPacket = (PutPacket) p.getPacket();
        return putPacket.getData();
    }

    // Operação de autenticação — registo e login.
    public boolean authenticate(String username, String password, int authenticationType) throws IOException {
        AuthPacket auth = new AuthPacket(username, password);
        PacketWrapper packetWrapper = new PacketWrapper(authenticationType, auth);

        conn.send(packetWrapper);

        PacketWrapper p = conn.receive();
        AckPacket ackPacket = (AckPacket) p.getPacket();
        return ackPacket.getAck();
    }

    public void closeConnection() throws IOException {
        conn.close();
    }
}


