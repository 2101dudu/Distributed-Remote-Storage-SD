package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CloseConnectionPacket {

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(3);
    }

    public static CloseConnectionPacket deserialize(DataInputStream in) throws IOException {
        return new CloseConnectionPacket();
    }
}