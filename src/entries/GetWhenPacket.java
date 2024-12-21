package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GetWhenPacket {
    private String key;
    private String keyCond;
    private byte[] dataCond;

    public GetWhenPacket() {
        this.key = null;
        this.keyCond = null;
        this.dataCond = null;
    }

    public GetWhenPacket(String key, String keyCond, byte[] dataCond) {
        this.key = key;
        this.keyCond = keyCond;
        this.dataCond = dataCond;

    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyCond() {
        return this.keyCond;
    }

    public void setKeyCond(String keyCond) {
        this.keyCond = keyCond;
    }

    public byte[] getDataCond() {
        return this.dataCond;
    }

    public void setDataCond(byte[] dataCond) {
        this.dataCond = dataCond;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeUTF(this.key);
        out.writeUTF(this.keyCond);
        if (this.dataCond != null) {
            int dataLength = this.dataCond.length;
            out.writeInt(dataLength);
            out.write(this.dataCond, 0, dataLength);
        } else {
            out.writeInt(0);
        }
    }

    public static GetWhenPacket deserialize(DataInputStream in) throws IOException {
        String key = in.readUTF();
        String keyCond = in.readUTF();
        int dataLength = in.readInt();
        if (dataLength == 0) {
            return new GetWhenPacket(key, keyCond, null);
        }
        byte[] data = in.readNBytes(dataLength);
        return new GetWhenPacket(key, keyCond, data);
    }
}
