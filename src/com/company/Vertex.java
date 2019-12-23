package com.company;
/**
 * Class to create a Vertex object
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class Vertex extends Abstract{
    final private String id;
    private String name;

    /**
     * Constructor for the vertex with id and name
     * @param id id of Vertex
     * @param name name of Vertex
     */
    public Vertex(String id, String name) {
        super(id);
        this.id = id;
        this.name = name;
    }

    /**
     * override getID in abstract
     * @return ID of edge
     */
    @Override
    public String getID() {
        return super.getID();
    }

    /**
     * override method in Object to make it for specific when comparing object
     * @param obj object needs to be compared
     * @return if the object are null or not and if they are the same
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Vertex other = (Vertex) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
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
