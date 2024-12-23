package tests.workloads;

import java.io.IOException;
import java.util.*;

import client.Client;

public class WorkloadMultiGet implements Workload {
    private Random random = new Random();
    private double multiGetPercentage;

    public WorkloadMultiGet(double multiGetPercentage) {
        this.multiGetPercentage = multiGetPercentage;
    }

    @Override
    public void execute(Client client) throws IOException {
        if (random.nextDouble() < multiGetPercentage) {
            Set<String> keys = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                keys.add("key-" + random.nextInt(1000));
            }
            client.multiGet(keys);
        } else {
            client.put("key-" + random.nextInt(1000), ("value-" + random.nextInt(1000)).getBytes());
        }
    }

    @Override
    public String getName() {
        return "WorkloadMultiGet-" + (int) (multiGetPercentage * 100) + "R";
    }
}
