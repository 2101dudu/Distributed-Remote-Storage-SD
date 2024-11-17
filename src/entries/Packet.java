package entries;

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
                break; // ADDED BREAK STATEMENT BECAUSE IN JAVA IT IS REQUIRED
            case 2:
                AtomicGetPacket entryGet = (AtomicGetPacket) this.packet;
                entryGet.serialize(out);
                break; // ADDED BREAK STATEMENT BECAUSE IN JAVA IT IS REQUIRED
        }
    }

    public static Object deserialize(DataInputStream in) throws IOException {
        int type = in.readInt();
        switch (type) {
            case 1:
                System.out.println("SingleEntry deserialized");
                SingleEntry entry = SingleEntry.deserialize(in);
                return entry;
            case 2:
                System.out.println("AtomicGetEntry deserialized");
                AtomicGetPacket entryGet = AtomicGetPacket.deserialize(in);
                return entryGet;
            default:
                return null;
        }
    }
}
