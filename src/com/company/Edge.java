package com.company;

public class Edge {
    private final String id;
    private final Vertex source;
    private final Vertex destination;
    private final String weight;

    public Edge(String id, Vertex source, Vertex destination, String weight) {
        this.id = id;
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public String getId() {
        return id;
    }
    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }
    public int getWeight() {
        return Integer.parseInt(weight.trim());
    }

    @Override
    public String toString() {
        return source + " " + destination;
    }

}
