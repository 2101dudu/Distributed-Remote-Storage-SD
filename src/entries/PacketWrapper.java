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
                PutPacket entry = (PutPacket) this.packet;
                entry.serialize(out);
                break;
            case 2:
                GetPacket entryGet = (GetPacket) this.packet;
                entryGet.serialize(out);
                break;
        }
    }

    public static Object deserialize(DataInputStream in) throws IOException {
        int type = in.readInt();
        switch (type) {
            case 1:
                return PutPacket.deserialize(in);
            case 2:
                return GetPacket.deserialize(in);
            default:
                throw new IOException("Unknown packet type");
        }
    }
}