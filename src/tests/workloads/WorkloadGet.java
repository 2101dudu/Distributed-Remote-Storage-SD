package tests.workloads;

import java.io.IOException;
import java.util.Random;

import client.Client;

public class WorkloadGet implements Workload {
    private Random random = new Random();
    private double readPercentage;

    public WorkloadGet(double readPercentage) {
        this.readPercentage = readPercentage;
    }

    @Override
    public void execute(Client client) throws IOException {
        String key = "key-" + random.nextInt(1000);
        if (random.nextDouble() < readPercentage) {
            client.get(key);
        } else {
            client.put(key, ("value-" + random.nextInt(1000)).getBytes());
        }
    }

    @Override
    public String getName() {
        return "WorkloadGet-" + (int) (readPercentage * 100) + "R";
    }
}
