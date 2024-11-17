package entries;

import com.sun.jdi.connect.spi.ClosedConnectionException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet {

    private int type;
    private Object packet;

    public Packet(int type, Object packet) {
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
                SingleEntry entry = (SingleEntry) this.packet;
                entry.serialize(out);
                break;
            case 2:
                AtomicGetPacket entryGet = (AtomicGetPacket) this.packet;
                entryGet.serialize(out);
                break;
            case 3:
                CloseConnectionPacket entryClose = (CloseConnectionPacket) this.packet;
                entryClose.serialize(out);
                break;
        }
    }

    public static Object deserialize(DataInputStream in) throws IOException {
        int type = in.readInt();
        switch (type) {
            case 1:
                return SingleEntry.deserialize(in);
            case 2:
                return AtomicGetPacket.deserialize(in);
            case 3:
                return CloseConnectionPacket.deserialize(in);
            default:
                throw new IOException("Unknown packet type");
        }
    }
}
