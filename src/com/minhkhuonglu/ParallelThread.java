package com.minhkhuonglu;

import java.util.logging.*;

public class ParallelThread implements Runnable {
    private int start;
    private int end;
    private boolean print;
    public ParallelThread(int start, int end,  boolean print) {
        this.start = start;
        this.end = end;
        this.print = print;
    }

    /**
     * the first thread will count up from start to end
     * the second thread will count down from end to start, but its value will be divided by 3
     * there will be 100ms sleep between each loop
     */
    @Override
    public void run() {
        if (print) {
            if ( start < end) {
                for (int i = start; i <= end; i++) {
                    LOG.info(i + " ...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            } else{
                for (int i = start; i >= end; i--) {
                    LOG.info(i / 3 + " ...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        else{
            LOG.info("Now we start!");
        }
    }

    public static final Logger LOG = Logger.getLogger(Main.class.getName());

}