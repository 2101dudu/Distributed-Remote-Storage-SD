package entries;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class MultiGetPacket {
    
    private Set<String> keys;

    public MultiGetPacket() {
        this.keys = new HashSet<>();
    }

    public MultiGetPacket(Set<String> keys) {
        this.keys = keys;
    }

    public Set<String> getKeys() {
        return this.keys;
    }

    public void setKeys(Set<String> keys) {
        this.keys = keys;
    }

    public void serialize(DataOutputStream out) throws IOException {
        out.writeInt(this.keys.size());
        for (String key : this.keys)
            out.writeUTF(key);
    }

    public static MultiGetPacket deserialize(DataInputStream in) throws IOException {
        Set<String> set = new HashSet<>();

        int setSize = in.readInt();
        for(int i=0; i<setSize; i++) 
            set.add(in.readUTF());
        
        return new MultiGetPacket(set);
    }
}
