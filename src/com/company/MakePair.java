package com.company;
/**
 * Class for creating a Pair to store the value for the source and target vertex in the Edge
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class MakePair<L,R> {
    private L l;
    private R r;

    /**
     * Constructor for creating a Pair to store the value for the source and target vertex in the Edge
     * @param l source vertex
     * @param r target vertex
     */
    public MakePair(L l, R r){
        this.l = l;
        this.r = r;
    }

    /**
     * getter methods to get the source or target vertex
     * @return the source or target vertex
     */
    public L getL(){ return l; }
    public R getR(){ return r; }

    /**
     * setter methods to set the sourve or target vertex
     * @param l set value for the source vertex, r to set value for the target vertex
     */
    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
}

