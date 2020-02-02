package com.minhkhuonglu;

import java.util.logging.*;

/**
 * this multi-threading is used for dividing the task require for
 * calculating all pair shortest path into half
 * first thread calculate from 0 to number of vertex / 2
 * second thread calculate the other half
 */
class MultiThreading implements Runnable {
    /**
     * first thread
     */
    private Thread t1;

    /**
     * Constructor for open a thread to calculate from start vertex to end vertex
     * this will be used to create 2 instances to calculate from start to vertex/2 and vertex/2 to end
     * @param start vertex
     * @param end vertex
     */
    MultiThreading(int start, int end){
        Main.calculateAllDijkstra(start,end);
    }

    /**
     * this function is used to run a thread
     */
    @Override
    public void run() {
        Logger.getLogger("Running ");

        try{
            Logger.getLogger("Starting multi-threading");
            Thread.sleep(1);
        }catch (InterruptedException e){
            Logger.getLogger("Thread " + "interrupted");
        }
    }

    /**
     * this function will start a new thread
     */
    public void start(){
        if (t1 == null){
            t1 = new Thread();
            t1.start();
        }
    }
}
