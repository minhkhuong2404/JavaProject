package com.minhkhuonglu;

import java.util.logging.*;

/**
 * Class for the edge and vertex object to extends from
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class ParallelThread implements Runnable {
    /**
     * first number
     */
    private int start;

    /**
     * second number
     */
    private int end;

    /**
     * if we are counting or print "Now we start"
     */
    private boolean print;

    /**
     * initialize a new Thread count from start to end
     * if it is used to print or count
     * @param start number
     * @param end number
     * @param print start calculating
     */
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
                    LOG.log(Level.FINE, i + " ...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LOG.log(Level.WARNING,"Thread is interrupted");
                    }
                }
            } else{
                for (int i = start; i >= end; i--) {
                    LOG.log(Level.FINE, i / 3 + " ...");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        LOG.log(Level.WARNING,"Thread is interrupted");
                    }
                }
            }
        }
        else{
            LOG.log(Level.FINE, "Now we start!");
        }
    }

    /**
     * This function is to create a Logger for further use
     */
    public static final Logger LOG = Logger.getLogger(Main.class.getName());

}