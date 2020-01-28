package com.minhkhuonglu;

import static com.minhkhuonglu.Main.*;

import java.util.*;
import java.util.logging.*;

/**
 * Class for calculating the shortest path using Dijkstra's algorithms
 * @author minhkhuonglu
 * @since 13
 * @version 1.0
 */
public class Dijkstra {

    /**
     * 2 list interfaces below is used to store all nodes and edges of a graph
     */
    private final List<Vertex> nodes;
    private final List<Edge> edges;

    /**
     * 2 set interfaces below is used to store all nodes that have been visited or not
     */
    private Set<Vertex> visitedNodes;
    private Set<Vertex> unVisitedNodes;

    /**
     * The map interface predecessors contains the previous vertex that will create the shortest path
     * eg: -1-4-6-7 predecessors of 6 is 4, of 4 is 1
     * and the shortest distance to that vertex
     */
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;

    /**
     * this ArrayList is used when there are more than 1 shortest path between 2 nodes
     *
     */
    private Map<Vertex, List<Vertex>> multiplePredecessors;

    /**
     * first create ArrayList of nodes and edges
     * @param graph graph needs to be calculated
     */
    public Dijkstra(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.nodes = new ArrayList<>(graph.getVertexes());
        this.edges = new ArrayList<>(graph.getEdges());
    }

    /**
     * create 2 HashSet of visited and unvisited nodes
     * create 2 HashMap of distance and predecessors
     *
     * add the distance of the source, which is 0 and add it into the unvisited node set
     * keep add node that has been visited and removed them
     * until the unvisited nodes become 0
     *
     * at each step, find the shortest distance to reach that node
     * @param source the start vertex
     */
    public void executeDijkstra(Vertex source) {
        visitedNodes = new HashSet<>();
        unVisitedNodes = new HashSet<>();

        distance = new HashMap<>();
        predecessors = new HashMap<>();

        multiplePredecessors =  new HashMap<>();

        distance.put(source, 0);
        unVisitedNodes.add(source);

        for (Vertex target: nodes){
            multiplePredecessors.put(target, new ArrayList<>());
        }

        multiplePredecessors.get(source).add(source);
        while (unVisitedNodes.size() > 0) {
            Vertex node = getMinimum(unVisitedNodes);
            LOG.fine("Node: " + node);
            visitedNodes.add(node);
            unVisitedNodes.remove(node);
            findMinimalDistances(node);
        }
        LOG.info("multi predecessor: "+ multiplePredecessors );
    }

    /**
     * calculate the total weight from the source to the destination vertex
     * @param destination the end vertex
     * @return the distance of the destination vertex
     */
    public int returnTotal_Weight(Vertex destination){
        return distance.get(destination);
    }

    /**
     * Returns the path from the source to the selected target and
     * NULL if no path exists
     * add all the predecessors vertex to a linked list
     *
     * @param target vertex needs to be reached
     */
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
//            LOG.fine("adding step");

        }
        // Put it into the correct order
        Collections.reverse(path);
//        LOG.fine("number of shortest path: " + path.getLast() +" --- "+ calculateTotalShortestPath(path.getLast()));

        return path;
    }

    /**
     * check if all vertices are visited by comparing the visitedNodes size
     * @return true is the size of visitedNodes are the same as the number of vertex
     */
    public boolean isConnected(){
        return visitedNodes.size() == vertexNum;
    }

    /**
     * find the minimal distances to get to a vertex by comparing the shortest distance to reach them
     * using all neighbors, which mean other vertices when combine with this vertex will create an edge
     *
     * if it is smaller, add its distance to the distance HashMap and its predecessors
     * and also add it into the unvisitedNodes as we are just checking its neighbors only
     *
     * add its distance to the multiple_distance if there are more than 1 shortest path
     * @param node the vertex is being checked
     */
    private void findMinimalDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);

        for (Vertex target : adjacentNodes) {
            if (getShortestDistance(target) > getShortestDistance(node) + getDistance(node, target)) {
                distance.put(target, getShortestDistance(node) + getDistance(node, target));
                // add the following node into the list of step in the shortest path
                predecessors.put(target, node);
                multiplePredecessors.get(target).clear();
                multiplePredecessors.get(target).add(node);
                unVisitedNodes.add(target);
//                LOG.info("predecessors: "+ predecessors);
//                LOG.info("Shortest distance from target: "+ getShortestDistance(target));
//                LOG.info("Shortest distance from node: "+ getShortestDistance(node));
            }
            else if (getShortestDistance(target) == getShortestDistance(node) + getDistance(node, target)){
                multiplePredecessors.get(target).add(node);
            }
        }

    }

    public float calculateTotalShortestPath(Vertex node){
        HashMap<Vertex, Integer> tempMultiplePre = new HashMap<>();
        HashMap<Vertex, Integer> realMultiplePre = new HashMap<>();

        for (Vertex vertex:nodes) {
            int firstVertex = 0;
            for (int j = 0; j < multiplePredecessors.get(vertex).size(); j++) {
                firstVertex += multiplePredecessors.get(multiplePredecessors.get(vertex).get(j)).size();
            }
            realMultiplePre.put(vertex, firstVertex);
        }
//        LOG.info(tempMultiplePre + " ?? real " + realMultiplePre);

        while ( !tempMultiplePre.equals(realMultiplePre)){
            tempMultiplePre.putAll(realMultiplePre);
            for (Vertex vertex:nodes){
                int finalVertex = 0;
                for (int j = 0; j < multiplePredecessors.get(vertex).size(); j++) {
                    finalVertex += realMultiplePre.get(multiplePredecessors.get(vertex).get(j));
                }
                realMultiplePre.put(vertex, finalVertex);
            }
//            LOG.info(tempMultiplePre + " ?? " + realMultiplePre);
        }
        LOG.info(tempMultiplePre + " all " + realMultiplePre);

        return realMultiplePre.get(node);
    }

    public float calculateShortestPathPass(Vertex pass, Vertex destination){
        HashMap<Vertex, Integer> vertexPass = new HashMap<>();
        ArrayList<Vertex> vertexMarked = new ArrayList<Vertex>() {
        };

        for (Vertex vertex:nodes){
            if (multiplePredecessors.get(vertex).contains(pass)){
                vertexPass.put(vertex,1);
                vertexMarked.add(vertex);
            } else{
                vertexPass.put(vertex,0);
            }
        }
//        LOG.info("size of vertor marked: "+vertexMarked.size());
        while (vertexMarked.size() != 0){
            for (Vertex vertex:nodes){
                if (multiplePredecessors.get(vertex).contains(vertexMarked.get(0))){
                    int temp2 = vertexPass.get(vertex);
                    temp2++;
                    vertexPass.put(vertex, temp2);
                    vertexMarked.add(vertex);
                }
            }
            LOG.info("marked " + vertexMarked);
            vertexMarked.remove(vertexMarked.get(0));
        }
        LOG.info(vertexPass + " passes");
        return vertexPass.get(destination);
    }
    /**
     * calculate the distance between 2 node by return its weight
     * @param node the start vertex
     * @param target the end vertex
     * @return the weight of path between 2 nodes
     */
    private int getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
//                System.out.println(Integer.parseInt((edge.getSource().toString().trim())) + " -> " + Integer.parseInt((edge.getDestination().toString().trim())) + " : " + edge.getWeight() );
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    /**
     * take out all the neighbor vertices of a node
     * by checking if the start of vertex of an edge is the same as the vertex we want
     * and its end vertex has been visited
     *
     * @param node node needs to find its neighbor
     * @return neighbor of the vertex, which are the vertices that have the same edge
     */
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

    /**
     * find the minimum weight to get to a vertex by comparing to a set of all vertices
     * @param vertexes set of vertices
     * @return the minimum distance to reach that vertex
     */
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

    /**
     * check if a vertex has been visited
     * @param vertex vertex that needs checking
     * @return if that vertex appeared in the visitedNodes set
     */
    private boolean isVisited(Vertex vertex) {
        return visitedNodes.contains(vertex);
    }

    /**
     * get the shortest distance to get to a vertex
     * @param destination vertex that needs to be reached
     * @return the value of the vertex in the distance HashSet only when it is not null
     */
    private int getShortestDistance(Vertex destination) {
        Integer d = distance.get(destination);
        return Objects.requireNonNullElse(d, Integer.MAX_VALUE);
    }

    /**
     * This function is to create a Logger for further use
     */
    private final Logger LOG = Logger.getLogger(Main.class.getName());

}

