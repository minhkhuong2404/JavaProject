package com.company;

import java.util.*;

public class Graph {
    public final List<Vertex> vertexes;
    private final List<Edge> edges;
    public static LinkedList<Edge>[] adjacencyList = new LinkedList[100000];

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
        for (int i = 0; i < 100000;i++) {
            if (adjacencyList[i] == null)
                adjacencyList[i] = new LinkedList<>();
        }
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }
}
