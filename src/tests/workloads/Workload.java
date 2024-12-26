package tests.workloads;

import java.io.IOException;

import client.Client;

public interface Workload {
    void execute(Client client) throws IOException;
    String getName();
}
