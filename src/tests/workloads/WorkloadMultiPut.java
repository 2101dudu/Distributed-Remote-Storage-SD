package tests.workloads;

import java.io.IOException;
import java.util.*;

import client.Client;

public class WorkloadMultiPut implements Workload {
    private Random random = new Random();
    private double multiPutPercentage;

    public WorkloadMultiPut(double multiPutPercentage) {
        this.multiPutPercentage = multiPutPercentage;
    }

    @Override
    public void execute(Client client) throws IOException {
        if (random.nextDouble() < multiPutPercentage) {
            Map<String, byte[]> pairs = new HashMap<>();
            for (int i = 0; i < 5; i++) {
                pairs.put("key-" + random.nextInt(1000), ("value-" + random.nextInt(1000)).getBytes());
            }
            client.multiPut(pairs);
        } else {
            client.get("key-" + random.nextInt(1000));
        }
    }

    @Override
    public String getName() {
        return "WorkloadMultiPut-" + (int) (multiPutPercentage * 100) + "W";
    }
}
