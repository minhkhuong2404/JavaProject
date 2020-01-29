package com.minhkhuonglu;
/**
 * Class for the edge and vertex object to extends from
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
abstract class Abstract implements Interface{
    private int id;
    private String name;

    /**
     * Constructor for Edge
     * @param id id od Edge
     */
    protected Abstract(int id) {
        this.id = id;
    }

    /**
     * Constructor for vertex
     * @param id id of vertex
     * @param name String value of vertex
     */
    protected Abstract(int id, String name) {
        this.id = id;
        this.name = name;
    }
    /**
     * getID of an object
     * @return ID of an object
     */
    public int getID() {
        return this.id;
    }

    /**
     * getName of an object
     * @return its name
     */
    public String getName() {return this.name;}

    /**
     * print out the object
     */
    public void printMe(){
        System.out.println("Print myself ");
    }
}
