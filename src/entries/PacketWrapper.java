package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
            case 1:
                PutPacket entryPut = (PutPacket) this.packet;
                entryPut.serialize(out);
                break;
            case 2:
                GetPacket entryGet = (GetPacket) this.packet;
                entryGet.serialize(out);
                break;
            case 3, 4:
                AuthPacket auth = (AuthPacket) this.packet;
                auth.serialize(out);
                break;
            case 5:
                AckPacket ack = (AckPacket) this.packet;
                ack.serialize(out);
                break;
        }
    }

    public static PacketWrapper deserialize(DataInputStream in) throws IOException {
        int type = in.readInt();
        Object packet;
        switch (type) {
            case 1:
                packet = PutPacket.deserialize(in);
                break;
            case 2:
                packet = GetPacket.deserialize(in);
                break;
            case 3, 4:
                packet = AuthPacket.deserialize(in);
                break;
            case 5:
                packet = AckPacket.deserialize(in);
                break;
            default:
                throw new IOException("Unknown packet type");
        }
        return new PacketWrapper(type, packet);
    }
}