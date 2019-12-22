package com.company;

import static com.company.Main.vertexNum;

import java.util.*;
import java.util.logging.*;

public class Dijkstra {

    private final List<Vertex> nodes;
    private final List<Edge> edges;
    private Set<Vertex> visitedNodes;
    private Set<Vertex> unVisitedNodes;
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;
    private ArrayList<MakePair<Vertex,Integer>> multiplie_distance = new ArrayList<>();

    public Dijkstra(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<>(graph.getVertexes());
        this.edges = new ArrayList<>(graph.getEdges());
    }

    public void executeDijkstra(Vertex source) {
        visitedNodes = new HashSet<>();
        unVisitedNodes = new HashSet<>();

        // contains the shortest distance from the source Vertex
        distance = new HashMap<>();
        // contains the previous vertex that will create the shortest path
        predecessors = new HashMap<>();

        distance.put(source, 0);
        unVisitedNodes.add(source);

        // stop when all nodes have been visited and the unvisited one are empty
        while (unVisitedNodes.size() > 0) {
            Vertex node = getMinimum(unVisitedNodes);
            visitedNodes.add(node);
            unVisitedNodes.remove(node);
            findMinimalDistances(node);
        }
//        System.out.println("The total weight is: " + returnTotal_Weight(source));
    }
    // calculate the total weight from the source to the destination vertex
    public Integer returnTotal_Weight(Vertex destination){
        return distance.get(destination);
    }

    // find the minimal distances to get to a node
    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);
        for (Vertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                // add the following node into the list of step in the shortest path
                predecessors.put(target, node);
                unVisitedNodes.add(target);
                multiplie_distance.add( new MakePair<>(target,getShortestDistance(node) + getDistance(node, target)));
            }
        }
    }

    // calculate the distance between 2 node by calling its weight
    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
//                System.out.println(Integer.parseInt((edge.getSource().toString().trim())) + " -> " + Integer.parseInt((edge.getDestination().toString().trim())) + " : " + edge.getWeight() );
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    // take out all the neighbor vertices
    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !isVisited(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        LOG.fine("Neighbors: " + neighbors);
        return neighbors;
    }

    // find the minimum weight to get to a vertex by comparing to a set of all vertices
    private Vertex getMinimum(Set<Vertex> vertexes) {
        Vertex minimum = null;
        for (Vertex vertex : vertexes) {
            if (minimum == null) {
                minimum = vertex;
            } else {
                if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
                    minimum = vertex;
                }
            }
        }
        return minimum;
    }

    // check if a vertex has been visited
    private boolean isVisited(Vertex vertex) {
        return visitedNodes.contains(vertex);
    }

    // get the shortest distance to get to a vertex
    private int getShortestDistance(Vertex destination) {
        Integer d = distance.get(destination);
        return Objects.requireNonNullElse(d, Integer.MAX_VALUE);
    }

    /*
     * Returns the path from the source to the selected target and
     * NULL if no path exists
     */
    // add all the predecessors vertex to a linked list
    public LinkedList<Vertex> getPath(Vertex target) {
        LinkedList<Vertex> path = new LinkedList<>();
        Vertex step = target;
        // check if a path exists
        if (predecessors.get(target) == null) {
            return null;
        }
        path.add(target);
        // add Vertex to check the shortest path
        while (predecessors.get(step) != null) {
            step = predecessors.get(step);
            path.add(step);
            LOG.fine("adding step");
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }

    // check if all vertices are visited by comparing the visitedNodes size
    public boolean isConnected(){
        return visitedNodes.size() == vertexNum;
    }

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

}

