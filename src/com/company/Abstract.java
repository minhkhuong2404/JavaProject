package com.company;

abstract class Abstract implements Interface{
    private String id;

    protected Abstract(String id) {
        this.id = id;
    }

    public String getID() {
        return this.id;
    }

    public void printMe(){
        System.out.println("Print myself ");
    }
}
