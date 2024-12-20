package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PutPacket {
    
    private String key;
    private byte[] data;

    public PutPacket() {
        this.key = null;
        this.data = null;
    }

    public PutPacket(String key, byte[] data) {
        this.key = key;
        this.data = data;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }


    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(this.key);
        if (this.data != null) {
            int dataLength = this.data.length;
            out.writeInt(dataLength);
            out.write(this.data, 0, dataLength);
        } else {
            out.writeInt(0);
        }
    }

    public static PutPacket deserialize(DataInputStream in) throws IOException {
        String key = in.readUTF();
        int dataLength = in.readInt();
        if (dataLength == 0) {
            return new PutPacket(key, null);
        }
        byte[] data = in.readNBytes(dataLength);
        return new PutPacket(key, data);
    }


    public String toString() {
        String s = "key: " + this.key + " --> Data: ";
        for (Byte b : this.data) {
            s += b;
        }
        return s;
    }
}
