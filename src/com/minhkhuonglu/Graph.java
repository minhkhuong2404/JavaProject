package com.minhkhuonglu;

import java.util.*;
/**
 * Class for creating the Graph with list of Vertex and Edge
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class Graph {
    private final List<Vertex> vertices;
    private final List<Edge> edges;

    /**
     * Constructor for creating the Graph with list of Vertex and Edge
     * @param vertices List of vertex
     * @param edges List of Edge
     */
    public Graph(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
    }

    /**
     * @return list of vertices
     */
    public List<Vertex> getVertices() {
        return vertices;
    }

    /**
     * @return list of edges
     */
    public List<Edge> getEdges() {
        return edges;
    }
}
