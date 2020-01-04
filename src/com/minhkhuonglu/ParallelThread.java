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

    @Override
    public void run() {
        if (print) {
            if ( start < end) {
                for (int i = start; i <= end; i++) {
                    LOG.info(String.valueOf(i) + " ...");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            } else{
                for (int i = start; i >= end; i--) {
                    LOG.info(String.valueOf(i) + " ...");
                    try {
                        Thread.sleep(500);
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