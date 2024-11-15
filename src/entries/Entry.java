package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Entry {
    
    private String key;
    private byte[] data;



    public Entry(String key, byte[] data) {
        this.key = key;
        this.data = Arrays.copyOf(data, data.length); // shallow copy
    }



    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getData() {
        return Arrays.copyOf(this.data, this.data.length);
    }

    public void setData(byte[] data) {
        this.data = Arrays.copyOf(data, data.length); // shallow copy
    }



    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(this.key);
        int dataLength = this.data.length;
        out.writeInt(dataLength);
        out.write(this.data, 0, dataLength);
    }

    public static Entry deserialize(DataInputStream in) throws IOException {
        String key = in.readUTF();
        int dataLength = in.readInt();
        byte[] data = in.readNBytes(dataLength);

        return new Entry(key, data);
    }



    public String toString() {
        String s = "key: " + this.key + " --> Data: ";
        for (Byte b : this.data) {
            s += b;
        }
        return s;
    }
}
