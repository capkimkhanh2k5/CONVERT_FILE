package com.convertfile.service;

import com.convertfile.worker.fileWorker;

public class WockerService {
    private final int NUMBER_OF_WORKERS = 1; // Số lượng worker threads
    private final Thread[] workers;

    public WockerService() {
        workers = new Thread[NUMBER_OF_WORKERS];

        for (int i = 0; i < NUMBER_OF_WORKERS; i++) {
            workers[i] = new Thread(new fileWorker(i + 1), "FileWorker-" + (i + 1));
            workers[i].setDaemon(true);
            workers[i].start();
        }

        System.out.println(NUMBER_OF_WORKERS + " file worker threads started.");
    }

    public void stopWorkers() {
        for (Thread worker : workers) {
            if (worker.isAlive()) {
                worker.interrupt();
            }
        }
        
        System.out.println("All file worker threads stopped.");
    }
}
