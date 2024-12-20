package entries;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class AckPacket {
    private boolean ack;

    public AckPacket() {
        this.ack = false;
    }

    public AckPacket(boolean ack) {
        this.ack = ack;
    }

    public boolean getAck() {
        return this.ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeBoolean(this.ack);
    }

    public static AckPacket deserialize(DataInputStream in) throws IOException {
        return new AckPacket(in.readBoolean());
    }
}
