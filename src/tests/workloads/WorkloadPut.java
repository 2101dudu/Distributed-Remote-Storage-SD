package tests.workloads;

import java.io.IOException;
import java.util.Random;

import client.Client;

public class WorkloadPut implements Workload {
    private Random random = new Random();
    private double writePercentage;

    public WorkloadPut(double writePercentage) {
        this.writePercentage = writePercentage;
    }

    @Override
    public void execute(Client client) throws IOException {
        String key = "key-" + random.nextInt(1000);
        if (random.nextDouble() < writePercentage) {
            client.put(key, ("value-" + random.nextInt(1000)).getBytes());
        } else {
            client.get(key);
        }
    }

    @Override
    public String getName() {
        return "WorkloadPut-" + (int) (writePercentage * 100) + "W";
    }
}
