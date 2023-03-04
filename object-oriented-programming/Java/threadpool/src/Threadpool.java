import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Threadpool {
    List<Thread> threads = new ArrayList<>();
    //Common mutable state. must be synchronised
    Queue<Runnable> tasks = new ArrayDeque<>();

    public Threadpool(int numThreads) {
        for (int i = 0 ; i < numThreads; i++) {
            Thread t = new Thread(new Worker());
            threads.add(t);

            t.start();
        }
    }

    private class Worker implements Runnable {
        @Override
        public void run() {
            while (true) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }

                Runnable r = null;
                synchronized (tasks) {
                    if (!tasks.isEmpty()) {
                        r = tasks.poll();
                    } else {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
                r.run();
            }
        }
    }

    void addTask(Runnable task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    void join() throws InterruptedException {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).join();
        }
    }

    void stop() {
        for (int i = 0; i < threads.size(); i++) {
            threads.get(i).interrupt();
        }
    }
}
