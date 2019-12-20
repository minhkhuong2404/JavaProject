package com.company;

// create a Pair to store the value for the source and target Node in the Edge
public class MakePair<L,R> {
    private L l;
    private R r;

    public MakePair(L l, R r){
        this.l = l;
        this.r = r;
    }

    public L getL(){ return l; }
    public R getR(){ return r; }

    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
}

