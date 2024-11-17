package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AtomicGetPacket {
    private String key;

    public AtomicGetPacket(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(this.key);
    }

    public static AtomicGetPacket deserialize(DataInputStream in) throws IOException {
        return new AtomicGetPacket(in.readUTF());
    }
}
