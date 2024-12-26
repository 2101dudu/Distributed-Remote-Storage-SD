package tests;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import client.Client;
import tests.workloads.*;
import utils.CSVWriter;

public class BenchmarkRunner {
    public static void main(String[] args) {
        String serverHost = "localhost";
        int serverPort = 8080;
        int[] numThreads = {1, 5, 10, 20}; // Setup different number of threads to be tested
        int operationsPerThread = 100;    // Setup number of operations per thread

        List<Workload> workloads = new ArrayList<>(WorkloadsConfig.allWorkloads());

        for (Workload workload : workloads) {
            System.out.println("Running workload: " + workload.getName());
            for (int threads : numThreads) {
                try {
                    runWorkload(serverHost, serverPort, workload, threads, operationsPerThread);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void runWorkload(String serverHost, int serverPort, Workload workload, int threads, int operationsPerThread) throws IOException {
        List<Thread> threadList = new ArrayList<>();
        List<Long> latencies = new ArrayList<>();
        Lock latenciesLock = new ReentrantLock();

        for (int i = 0; i < threads; i++) {
            threadList.add(new Thread(new Worker(serverHost, serverPort, workload, operationsPerThread, latencies, latenciesLock)));
        }

        long startTime = System.currentTimeMillis();
        for (Thread thread : threadList) thread.start();
        for (Thread thread : threadList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long endTime = System.currentTimeMillis();

        long totalDuration = endTime - startTime;
        double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0) / 1e6; // ms
        double throughput = (latencies.size() * 1000.0) / totalDuration;

        CSVWriter.writeResult(workload.getName(), threads, operationsPerThread, totalDuration, avgLatency, throughput);
    }
}

class Worker implements Runnable {
    private final String serverHost;
    private final int serverPort;
    private final Workload workload;
    private final int operationsPerThread;
    private final List<Long> latencies;
    private final Lock latenciesLock;

    public Worker(String serverHost, int serverPort, Workload workload, int operationsPerThread, List<Long> latencies, Lock latenciesLock) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.workload = workload;
        this.operationsPerThread = operationsPerThread;
        this.latencies = latencies;
        this.latenciesLock = latenciesLock;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(serverHost, serverPort)) {
            Client client = new Client(socket);

            for (int op = 0; op < operationsPerThread; op++) {
                long start = System.nanoTime();
                workload.execute(client);
                long end = System.nanoTime();
                latenciesLock.lock();
                try {
                    latencies.add(end - start);
                } finally {
                    latenciesLock.unlock();
                }
            }

            client.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
