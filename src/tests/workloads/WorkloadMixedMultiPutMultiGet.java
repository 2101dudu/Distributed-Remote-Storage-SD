package tests.workloads;

import java.io.IOException;
import java.util.*;

import client.Client;

public class WorkloadMixedMultiPutMultiGet implements Workload {
    private final Random random = new Random();
    private final double getProbability;

    public WorkloadMixedMultiPutMultiGet(double getProbability) {
        this.getProbability = getProbability;
    }

    @Override
    public void execute(Client client) throws IOException {
        if (random.nextDouble() < getProbability) {
            Set<String> keys = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                keys.add("key-" + random.nextInt(1000));
            }
            Map<String, byte[]> result = client.multiGet(keys);
            for (String key : keys) {
                byte[] value = result.get(key);
                System.out.println("MULTIGET: " + key + " -> " + (value != null ? new String(value) : "null"));
            }
        } else {
            Map<String, byte[]> pairs = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                pairs.put("key-" + random.nextInt(1000), ("value-" + random.nextInt(1000)).getBytes());
            }
            client.multiPut(pairs);
            for (Map.Entry<String, byte[]> entry : pairs.entrySet()) {
                System.out.println("MULTIPUT: " + entry.getKey() + " -> " + new String(entry.getValue()));
            }
        }
    }

    @Override
    public String getName() {
        return "WorkloadMixedMultiPutMultiGet-" + (int) (getProbability * 100) + "Get";
    }
}
