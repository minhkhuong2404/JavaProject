package com.company;
/**
 * Class for the edge and vertex object to extends from
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
abstract class Abstract implements Interface{
    private String id;

    protected Abstract(String id) {
        this.id = id;
    }
    /**
     * getID of an object
     * @return ID of an object
     */
    public String getID() {
        return this.id;
    }

    /**
     * print out the object
     */
    public void printMe(){
        System.out.println("Print myself ");
    }
}
