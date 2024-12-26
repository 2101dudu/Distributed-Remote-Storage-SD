package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LogWriter {
    private static final String LOG_FILE = "../out/log.txt";
    private static final Lock lock = new ReentrantLock();

    public void println(String message) {
        lock.lock();
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(LOG_FILE), true))) {
            writer.println("--------------------------------------------------");
            writer.println("Log at " + new java.text.SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(new java.util.Date()));
            writer.println(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally { 
            lock.unlock();
        }
    }
}
