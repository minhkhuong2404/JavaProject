package com.minhkhuonglu;
/**
 * Class for creating an Edge object
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class Edge extends Abstract{
    private final String id;
    private final Vertex source;
    private final Vertex destination;
    private final String weight;

    /**
     * Constructor for creating an Edge object
     * @param id id of the edge
     * @param source source vertex of edge
     * @param destination target vertex of edge
     * @param weight weight of edge
     */
    public Edge(String id, Vertex source, Vertex destination, String weight) {
        super(id);
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
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
     *
     * @return destination vertex
     */
    public Vertex getDestination() {
        return destination;
    }

    /**
     *
     * @return source vertex
     */
    public Vertex getSource() {
        return source;
    }

    /**
     *
     * @return weight of the edge
     */
    public int getWeight() {
        return Integer.parseInt(weight.trim());
    }

    /**
     * override toString in abstract
     * change name of Edge into String
     */
    @Override
    public String toString() {
        return source + " " + destination;
    }

    /**
     * override printMe in abstract
     * print out ID of edge
     */
    @Override
    public void printMe() {
        System.out.println("Edge " + id + " has weight " + weight);
    }


}
