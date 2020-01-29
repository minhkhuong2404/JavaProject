package com.minhkhuonglu;

import static com.minhkhuonglu.Main.*;

import java.util.*;
import java.util.logging.*;

/**
 * Class for calculating the shortest path using Dijkstra's algorithms
 * @author minhkhuonglu
 * @since 13
 * @version 2.0
 */
public class Dijkstra {

    /**
     * 2 list interfaces below is used to store all vertices and edges of a graph
     */
    private final List<Vertex> vertices;
    private final List<Edge> edges;

    /**
     * 2 set interfaces below is used to store all vertices that have been visited or not
     */
    private Set<Vertex> visitedVertices;
    private Set<Vertex> unVisitedVertices;

    /**
     * The map interface predecessors contains the previous vertex that will create the shortest path
     * eg: -1-4-6-7 predecessors of 6 is 4, of 4 is 1
     * and the shortest distance to that vertex
     */
    private Map<Vertex, Vertex> predecessors;
    private Map<Vertex, Integer> distance;

    /**
     * this ArrayList is used when there are more than 1 shortest path between 2 vertices
     * storing all predecessor that have the same length into a List
     */
    private Map<Vertex, List<Vertex>> multiplePredecessors;


    /**
     * find the minimal distances to get to a vertex by comparing the shortest distance to reach them
     * using all neighbors, which mean other vertices when combine with this vertex will create an edge
     *
     * if it is smaller, add its distance to the distance HashMap and its predecessors
     * and also add it into the unvisitedVertices as we are just checking its neighbors only
     *
     * add its distance to the multiple_distance if there are more than 1 shortest path
     * @param vertex the vertex is being checked
     */
    private void findMinimalDistances(Vertex vertex) {
        List<Vertex> adjacentVertices = getNeighbors(vertex);

        for (Vertex target : adjacentVertices) {
            if (getShortestDistance(target) > getShortestDistance(vertex) + getDistance(vertex, target)) {
                distance.put(target, getShortestDistance(vertex) + getDistance(vertex, target));
                // add the following vertex into the list of step in the shortest path
                predecessors.put(target, vertex);
                multiplePredecessors.get(target).clear();
                multiplePredecessors.get(target).add(vertex);
                unVisitedVertices.add(target);
            }
            else if (getShortestDistance(target) == getShortestDistance(vertex) + getDistance(vertex, target)){
                multiplePredecessors.get(target).add(vertex);
            }
        }
    }

    /**
     * calculate the distance between 2 vertex by return its weight
     * @param source the start vertex
     * @param target the end vertex
     * @return the weight of path between 2 vertices
     */
    private int getDistance(Vertex source, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(source) && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("Should not happen");
    }

    /**
     * take out all the neighbor vertices of a vertex
     * by checking if the start of vertex of an edge is the same as the vertex we want
     * and its end vertex has been visited
     *
     * @param vertex vertex needs to find its neighbor
     * @return neighbor of the vertex, which are the vertices that have the same edge
     */
    private List<Vertex> getNeighbors(Vertex vertex) {
        List<Vertex> neighbors = new ArrayList<>();
        for (Edge edge : edges) {
            if (edge.getSource().equals(vertex) && !isVisited(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        LOG.log(Level.FINE, "Neighbors: " + neighbors);
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
     * @return if that vertex appeared in the visitedVertices set
     */
    private boolean isVisited(Vertex vertex) {
        return visitedVertices.contains(vertex);
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

    /**
     * first create ArrayList of vertices and edges
     * @param graph graph needs to be calculated
     */
    public Dijkstra(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.vertices = new ArrayList<>(graph.getVertices());
        this.edges = new ArrayList<>(graph.getEdges());
    }

    /**
     * initialize the start vertex
     * the end vertex will be defined in other function
     * see them in JavaDoc
     *
     * create 2 HashSet of visited and unvisited vertices
     * create 2 HashMap of distance and predecessors
     * create HashMap of multiplePredecessors
     *
     * add the distance of the source, which is 0 and add it into the unvisited vertex set
     * also add the distance of the source into the multiplePredecessors array
     *
     * keep add vertex that has been visited and removed them
     * until the unvisited vertices become 0
     *
     * at each step, find the shortest distance to reach that vertex
     * @param source the start vertex
     */
    public void executeDijkstra(Vertex source) {
        visitedVertices = new HashSet<>();
        unVisitedVertices = new HashSet<>();

        distance = new HashMap<>();
        predecessors = new HashMap<>();

        multiplePredecessors =  new HashMap<>();

        distance.put(source, 0);
        unVisitedVertices.add(source);

        for (Vertex target: vertices){
            multiplePredecessors.put(target, new ArrayList<>());
        }

        multiplePredecessors.get(source).add(source);
        while (unVisitedVertices.size() > 0) {
            Vertex vertex = getMinimum(unVisitedVertices);
            LOG.log(Level.FINE, "vertex: " + vertex);
            visitedVertices.add(vertex);
            unVisitedVertices.remove(vertex);
            findMinimalDistances(vertex);
        }
        LOG.log(Level.FINE, "multi predecessor: "+ multiplePredecessors );
    }

    /**
     * calculate the total weight from the source to the destination vertex
     * @param destination the end vertex
     * @return the distance of the destination vertex
     */
    public int returnTotalWeight(Vertex destination){
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
        }
        // Put it into the correct order
        Collections.reverse(path);
        LOG.log(Level.FINE, "number of shortest path: " + path.getLast() +" --- "+ calculateTotalShortestPath(path.getLast()));

        return path;
    }

    /**
     * check if all vertices are visited by comparing the visitedVertices size
     * @return true is the size of visitedVertices are the same as the number of vertex
     */
    public boolean isConnected(){
        return visitedVertices.size() == vertexNum;
    }

    /**
     * this function will first iterate all vertex in the graph
     * adding the size of vertices in the multiplePredecessors then put it into a realMultiplePre
     * then starting adding the new size by using a while loop
     * calculating until all size of the vertices remained unchanged by comparing with tempMultiplePre
     * all value are stored in the realMultiplePre HashMap
     *
     * @param target the end vertex needs
     * @return number shortest path from the start ( which in executedDijkstra function) to the end vertex
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    public float calculateTotalShortestPath( Vertex target){

        HashMap<Vertex, Integer> tempMultiplePredecessors = new HashMap<>();
        HashMap<Vertex, Integer> realMultiplePredecessors = new HashMap<>();

        for (Vertex vertex : vertices) {
            int firstNumberOfPath = 0;
            for (int indexOfPredecessor = 0; indexOfPredecessor < multiplePredecessors.get(vertex).size(); indexOfPredecessor++) {
                firstNumberOfPath += multiplePredecessors.get(multiplePredecessors.get(vertex).get(indexOfPredecessor)).size();
            }
            realMultiplePredecessors.put(vertex, firstNumberOfPath);
        }
        while ( !tempMultiplePredecessors.equals(realMultiplePredecessors)){
            tempMultiplePredecessors.putAll(realMultiplePredecessors);
            for (Vertex vertex : vertices){
                int finalNumberOfPath = 0;
                for (int indexOfPredecessor = 0; indexOfPredecessor < multiplePredecessors.get(vertex).size(); indexOfPredecessor++) {
                    finalNumberOfPath += realMultiplePredecessors.get(multiplePredecessors.get(vertex).get(indexOfPredecessor));
                }
                realMultiplePredecessors.put(vertex, finalNumberOfPath);
            }
        }
        LOG.log(Level.FINE, tempMultiplePredecessors + " all " + realMultiplePredecessors);

        return realMultiplePredecessors.get(target);
    }

}

