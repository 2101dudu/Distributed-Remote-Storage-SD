package tests.workloads;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import client.Client;

public class WorkloadGetWhen implements Workload {
    private final Random random = new Random();

    @Override
    public void execute(Client mainClient) throws IOException {
        String key = "key-" + random.nextInt(1000);
        String condKey = "cond-" + random.nextInt(1000);
        byte[] condValue = ("value-" + random.nextInt(1000)).getBytes();

        Thread getWhenThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8080)) {
                Client getWhenClient = new Client(socket);
                System.out.println("Executing getWhen for key: " + key);
                byte[] result = getWhenClient.getWhen(key, condKey, condValue);
                if (result != null) {
                    System.out.println("getWhen unlocked: " + new String(result));
                } else {
                    System.out.println("getWhen returned null.");
                }
                getWhenClient.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Thread putThread = new Thread(() -> {
            try (Socket socket = new Socket("localhost", 8080)) {
                Client putClient = new Client(socket);
                Thread.sleep(15); // Little delay to ensure getWhen doesn't execute after put
                System.out.println("Executing put for condition key: " + condKey);
                putClient.put(condKey, condValue);
                putClient.closeConnection();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        getWhenThread.start();
        putThread.start();

        try {
            getWhenThread.join();
            putThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "WorkloadGetWhen";
    }
}
