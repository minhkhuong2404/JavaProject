package com.minhkhuonglu;
/**
 * Class to create a Vertex object
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class Vertex extends Abstract{
    private int id;
    private String name;

    /**
     * Constructor for the vertex with id and name
     * @param id id of Vertex
     * @param name name of Vertex
     */
    public Vertex(int id, String name) {
        super(id);
        this.id = id;
        this.name = name;
    }

    /**
     * override getID in abstract
     * @return ID of edge
     */
    @Override
    public int getID() {
        return super.getID();
    }

    public String getName(){
        return this.name;
    }

    /**
     * override toString in abstract
     * change name of Vertex into String
     */
    @Override
    public String toString() {
        return name;
    }
    /**
     * override printMe in abstract
     * print out ID of vertex
     */
    @Override
    public void printMe() {
        System.out.println("Vertex " + id);
    }
}
