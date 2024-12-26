package tests.workloads;

import java.io.IOException;
import java.util.Random;

import client.Client;

public class WorkloadMixedPutGet implements Workload {
    private final Random random = new Random();
    private final double getProbability;

    public WorkloadMixedPutGet(double getProbability) {
        this.getProbability = getProbability;
    }

    @Override
    public void execute(Client client) throws IOException {
        String key = "key-" + random.nextInt(1000);
        if (random.nextDouble() < getProbability) {
            // Executa um get
            byte[] result = client.get(key);
            if (result != null) {
                System.out.println("GET: " + key + " -> " + new String(result));
            } else {
                System.out.println("GET: " + key + " -> null");
            }
        } else {
            // Executa um put
            byte[] value = ("value-" + random.nextInt(1000)).getBytes();
            client.put(key, value);
            System.out.println("PUT: " + key + " -> " + new String(value));
        }
    }

    @Override
    public String getName() {
        return "WorkloadMixedPutGet-" + (int) (getProbability * 100) + "Get";
    }
}
