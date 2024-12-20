package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class MultiPutPacket {
    
    private Map<String, byte[]> pairs;

    public MultiPutPacket() {
        this.pairs = new HashMap<>();
    }

    public MultiPutPacket(Map<String, byte[]> pairs) {
        this.pairs = pairs;
    }

    public Map<String, byte[]> getPairs() {
        return this.pairs;
    }

    public void setPairs(Map<String, byte[]> pairs) {
        this.pairs = pairs;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.pairs.size());
        for (Map.Entry<String, byte[]> pair : this.pairs.entrySet()) {
            out.writeUTF(pair.getKey());

            if (pair.getValue() != null) {
                int dataLength = pair.getValue().length;
                out.writeInt(dataLength);
                out.write(pair.getValue(), 0, dataLength);
            } else {
                out.writeInt(0);
            }
        }
    }

    public static MultiPutPacket deserialize(DataInputStream in) throws IOException {
        Map<String, byte[]> map = new HashMap<>();

        int mapSize = in.readInt();
        for(int i=0; i<mapSize; i++) {
            String key = in.readUTF();
            int dataLength = in.readInt();
            if (dataLength == 0) {
                map.put(key, null);
                continue;
            }
            byte[] data = in.readNBytes(dataLength);
            map.put(key, data);
        }

        return new MultiPutPacket(map);
    }
}
