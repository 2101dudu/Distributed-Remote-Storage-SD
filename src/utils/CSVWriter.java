package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CSVWriter {
    private static final String CSV_FILE = "benchmark_results.csv";
    private static final Lock lock = new ReentrantLock();

    public static void writeResult(String workload, int threads, int operations, long duration, double avgLatency, double throughput) {
        lock.lock();
        try {
            File file = new File(CSV_FILE);
            boolean isNewFile = !file.exists();

            try (PrintWriter writer = new PrintWriter(new FileWriter(CSV_FILE, true))) {
                if (isNewFile) {
                    writer.println("Workload,Threads,Operations,Duration(ms),AvgLatency(ms),Throughput(ops/sec)");
                }

                writer.printf("%s,%d,%d,%d,%.2f,%.2f%n", workload, threads, operations, duration, avgLatency, throughput);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
