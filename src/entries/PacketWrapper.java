package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import utils.PacketType;

public class PacketWrapper {
    private int type;
    private Object packet;

    public PacketWrapper(int type, Object packet) {
        this.type = type;
        this.packet = packet;
    }

    public void setType(int type) {this.type = type;}

    public int getType() {return this.type;}

    public void setPacket(Object packet) {this.packet = packet;}

    public Object getPacket() {return this.packet;}

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.type);
        switch (this.type) {
            case PacketType.PUT:
                PutPacket entryPut = (PutPacket) this.packet;
                entryPut.serialize(out);
                break;
            case PacketType.GET:
                GetPacket entryGet = (GetPacket) this.packet;
                entryGet.serialize(out);
                break;
            case PacketType.REGISTER, PacketType.LOGIN:
                AuthPacket auth = (AuthPacket) this.packet;
                auth.serialize(out);
                break;
            case PacketType.ACK:
                AckPacket ack = (AckPacket) this.packet;
                ack.serialize(out);
                break;
            case PacketType.MULTI_PUT:
                MultiPutPacket multiPut = (MultiPutPacket) this.packet;
                multiPut.serialize(out);
                break;
            case PacketType.MULTI_GET:
                MultiGetPacket multiGet = (MultiGetPacket) this.packet;
                multiGet.serialize(out);
                break;
        }
    }

    public static PacketWrapper deserialize(DataInputStream in) throws IOException {
        int type = in.readInt();
        Object packet;
        switch (type) {
            case PacketType.PUT:
                packet = PutPacket.deserialize(in);
                break;
            case PacketType.GET:
                packet = GetPacket.deserialize(in);
                break;
            case PacketType.REGISTER, PacketType.LOGIN:
                packet = AuthPacket.deserialize(in);
                break;
            case PacketType.ACK:
                packet = AckPacket.deserialize(in);
                break;
            case PacketType.MULTI_PUT:
                packet = MultiPutPacket.deserialize(in);
                break;
            case PacketType.MULTI_GET:
                packet = MultiGetPacket.deserialize(in);
                break;
            default:
                throw new IOException("Unknown packet type");
        }
        return new PacketWrapper(type, packet);
    }
}