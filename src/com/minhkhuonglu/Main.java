package com.minhkhuonglu;

import static org.junit.Assert.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

/**
 * Class for running the whole OOPJava Project in Java Course WS19/20 FRA_UAS
 *
 * @author minhkhuonglu
 * @since 13
 * @version 2.0
 */

public class Main{

    /**
     * The integerValues here are used to store number of Edges, Vertices and Weight of Edges
     * all set to 0 at first
     */
    static int edgeNum = 0, vertexNum = 0, edgeWeightNum = 0;

    /**
     * The two-dimension integer array stores all possible weight of all edges
     * Two-dimension because we need to have the start vertex and the destination vertex
     */
    static int[][] storeWeigh = new int[10000][10000];
    /**
     * The char array is used to store all characters that appear in the .graphml file
     * Large number of index just to ensure there won't be any character that is left unread
     * Following the index of each character
     */
    static char[] allChar = new char[1000000];
    static int indexOfChar = 0;

    /**
     * This boolean is for changing the logging option
     * whether to use Dijkstra to calculate shortest path between 2 Vertices
     * or to check the connectivity of the graph
     * false is for calculate Dijkstra
     */
    static boolean onlyConnected = false;

    /**
     * The float value calculates all shortest path from all paths between 2 Vertices that passed through a vertex
     * this value will be also be reset after calculating a pair of vertices
     * @see com.minhkhuonglu.Main#calculateBetweennessCentrality(Vertex)
     */
    static float numberOfShortestPathPass = 0;

    /**
     * This value is used to calculated the diameter of the graph
     * @see com.minhkhuonglu.Main#calculateAllDijkstra(int, int)
     */
    static int diameterOfGraph = -1;

    /**
     * Replaces some string in the .graphml file
     * The String edgeID replaces for the "e_id"
     * The String vertexID replaces for the "v_id"
     * The String edgeWeight replaces for the "e_we"
     * The String sourceOfEdge replaces for the "sour"
     * Reason for doing this is checking 4 characters at the same time when reading a file
     * @see com.minhkhuonglu.Main#handleCharacters
     */
    final static String edgeId = "e_id";
    final static String vertexID = "v_id";
    final static String edgeWeight = "e_we";
    final static String sourceOfEdge = "sour";

    /**
     * The ArrayLists edges stores the start and the destination vertex of an edge
     * The ArrayLists vertexIDs stores all the ID of Vertices
     * The ArrayLists edgeIDs stores all the ID of edges
     * The ArrayLists edgeWeights stores only the weight of edges
     */
    private static ArrayList<MakePair<String,String>> edges = new ArrayList<>();
    private static ArrayList<String> vertexIDs = new ArrayList<>();
    private static ArrayList<String> edgeIDs = new ArrayList<>();
    private static ArrayList<String> edgeWeights = new ArrayList<>();

    /**
     * The "Vertices" List is used to contain all vertices in the Graph
     * The "Edges" List is used to contain all edges in the Graph
     * @see com.minhkhuonglu.Graph
     */
    public static List<Vertex> Vertices = new ArrayList<>();
    public static List<Edge> Edges = new ArrayList<>();

    /**
     * This 2D-array is used to stored all Dijkstra between 2 Vertices
     * @see com.minhkhuonglu.Main#calculateAllDijkstra(int, int)
     */
    public static int [][] allDijkstra;

    /**
     * This 2D-array is used to storing the number of shortest paths between 2 Vertices
     * @see com.minhkhuonglu.Main#calculateAllDijkstra(int, int)
     */
    public static float [][] numberOfShortestPath;

    /**
     * This 2D-array is used to storing 1 path between 2 vertices
     */
    public static String [][] APathBetweenTwoVertices;

    /**
     * playing the two parallel threads, one will count up and one will count down and its value
     * will be divided by three. Then it will count up normally then start doing its task.
     * first check the extension of file whether it is .graphml or not
     * then starting reading the input .graphml file to take out the vertex and edge
     * initializing the allDijkstra and numberOfShortestPath 2D-array for further calculation
     * open 2 Thread to calculating all necessary information which is
     * the shortest path between 2 vertices and the number of shortest path between them
     *
     * The main method used to distinguish the input argument that the user types in
     * to choose which needs to be print out or doing
     * divide the number of arguments are used when calling .jar file 1,3 or 4
     * if length is 1, it will print out properties of the graph
     * if length is 3, it will calculate the between centrality measure of a vertex if the second args is -b
     * otherwise, it will output the whole result into a new .graphml file
     * if length is 4, it will find the shortest path between 2 Vertices
     * @param args which are all arguments when calling the .jar file
     * @throws IOException if stream to file cannot be written
     */
    public static void main(String[] args) throws IOException, IncorrectFileExtensionException, InterruptedException {

        long startTime = System.currentTimeMillis();

        Main graph = new Main();
        // play with thread
        ParallelThread countUp = new ParallelThread(1,3,true);
        ParallelThread countDown = new ParallelThread(10,8,true);
        ParallelThread countAgain = new ParallelThread(1,5,true);
        ParallelThread startDoCalculation = new ParallelThread(1,1, false);

        Thread countUpThread = new Thread(countUp);
        Thread countDownThread = new Thread(countDown);
        Thread countAgainThread = new Thread(countAgain);
        Thread startCalculationThread = new Thread(startDoCalculation);

        LOG.log(Level.FINE, "Preparing to build and display the properties of the graph: ");
        countUpThread.start();
        countDownThread.start();
        countUpThread.join();
        countDownThread.join();
        LOG.log(Level.FINE, "System crashed, trying to prepare again.");

        countAgainThread.start();
        countAgainThread.join();
        LOG.log(Level.FINE, "Done preparing! ");

        // check whether it is .graphml file
        try {
            graph.checkCorrectFileExtension(args[0]);
        } catch (IllegalArgumentException err){
            LOG.log(Level.WARNING, "Wrong file extension " + err);
        }

        graph.readFileAndBuildGraph(args[0]);

        // initialize two 2D-array
        allDijkstra = new int[vertexNum][vertexNum];
        numberOfShortestPath = new float[vertexNum][vertexNum];
        APathBetweenTwoVertices = new String[vertexNum][vertexNum];

        startCalculationThread.start();

        // this line is used without thread
//        calculateAllDijkstra(0,vertexNum);
        if (vertexNum > 150){
            LOG.log(Level.INFO, "This file is quite large. So it may take a little bit longer. Please be patient");
        }
        MultiThreading RunningThread1 = new MultiThreading(0,vertexNum/2);
        RunningThread1.start();
        MultiThreading RunningThread2 = new MultiThreading(vertexNum/2, vertexNum);
        RunningThread2.start();

        // checking how many arguments are pasted in order to choose the right operation
        if (args.length == 1) {
            onlyConnected =  true; // to choose whether to print out properties or just the Dijkstra of 2 Vertices

            // true for the properties
            LOG.log(Level.INFO, "The properties of the graph are: ");

            // print out the vertexIDs
            graph.printvertexIDs(vertexIDs);
            // print out the edgeIDS
            graph.printEdgeIDs(edgeIDs);
            // print out the edges source and target
            graph.printMapOut(edges);

            // use Dijkstra to check the connectivity and the diameter of the graph
            graph.DijkstraCall(Vertices.get(1), Vertices.get(2));

        } else if (args.length == 3){
            if (args[1].equals("-b")) {
                int passVertex = Integer.parseInt(args[2]);
                // calculate betweenness centrality measure for a specific vertex
                double betweennessCentrality;
                betweennessCentrality = graph.calculateBetweennessCentrality(Vertices.get(passVertex));
                LOG.log(Level.INFO, "Betweenness centrality of " + passVertex + " is: " + betweennessCentrality);
            }
            else if (args[1].equals("-a")){
                try{
                    Files.deleteIfExists(Paths.get("/" + args[2]));
                }
                catch(IOException e) {
                    LOG.log(Level.WARNING, "Invalid permissions.");
                }

                LOG.log(Level.INFO, "Delete old file successful.");

                try{
                    // output into a file
                    graph.outputingFile(args[2]);
                } catch (Exception ignored){}
                LOG.log(Level.INFO, "File creation is finished!");
            }
        }
        else {
            if (args[1].equals("-s")) {

                int sourceVertex,targetVertex;

                sourceVertex = Integer.parseInt(args[2]);
                targetVertex = Integer.parseInt(args[3]);
                // call the Dijkstra algorithms
                try {
                    graph.DijkstraCall(Vertices.get(sourceVertex), Vertices.get(targetVertex));
                } catch (AssertionError e){
                    LOG.log(Level.INFO, "Oops!! Please choose 2 different vertices.");
                }
            }
        }

        long endTime = System.currentTimeMillis();
        long elapsed_time = endTime - startTime;
        LOG.log(Level.WARNING, "Running time: " + elapsed_time + " ms");
    }

    /**
     * this function will iterating through all pair of Vertices
     * calculate and save the dijsktra value in the array allDijkstra
     * calculate and save the number of shortest path in the array numberOfShortestPath
     * calculate and comparing to find the diameter, which is the longest shortest path
     * @param from first half vertices
     * @param to second half vertices
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    public static void calculateAllDijkstra(int from,int to) {
        Graph graphFirst = new Graph(Vertices, Edges);
        Dijkstra dijkstraFirst = new Dijkstra(graphFirst);
        int maxPath = -1;
        for (int source = from; source < to; source++) {
            Vertex start = Vertices.get(source);
            dijkstraFirst.executeDijkstra(start);
            for (int target = 0; target < vertexNum; target++) {

                Vertex destination = Vertices.get(target);
                LinkedList<Vertex> paths = dijkstraFirst.getPath(destination);
                numberOfShortestPath[source][target] = dijkstraFirst.calculateTotalShortestPath(destination);

                if (source < target) {
                    allDijkstra[source][target] = dijkstraFirst.returnTotalWeight(destination);

                } else if (source == target) {
                    allDijkstra[source][target] = 0;
                } else {
                    allDijkstra[source][target] = allDijkstra[target][source];
                }

                // take from the findingTheLongestShortestPath
                if (start != destination) {
                    APathBetweenTwoVertices[source][target] = String.valueOf(dijkstraFirst.getPath(Vertices.get(target)));

                    // find a longer path size
                    if (paths.size() > maxPath) {
                        maxPath = paths.size();
                        diameterOfGraph = dijkstraFirst.returnTotalWeight(destination);
                    }
                } else{
                    APathBetweenTwoVertices[source][target] = String.valueOf(source);
                }
            }
        }
    }

    /**
     * check if the extension of the file is correct
     * @param fileExtension a *.graphml file
     */
    private void checkCorrectFileExtension(String fileExtension){
        if (!fileExtension.contains(".graphml")){
            throw new IncorrectFileExtensionException(
                    "Enter a valid *.graphml file. " + fileExtension + " is detected");
        }else{
            LOG.log(Level.FINE, "Correct file extension");
        }
    }

    /**
     * The function readFileAndBuildGraph will read the file
     * @param nameOfFile which is the args[0]
     * @exception java.io.IOException if stream to file cannot be written to or closed.
     */
    private void readFileAndBuildGraph(String nameOfFile) throws IOException {
        Charset encoding = Charset.defaultCharset();
        // open and read a new file
        File file = new File(nameOfFile);
        handleFile(file, encoding);
        LOG.log(Level.FINE, "Building graph begins ");
        buildGraph();
    }

    /**
     * This function used to build the graph for further calculation
     * which contains vertices and edges
     * this also creates new linked list to store the weight of the start vertex
     * to calculate all paths between 2 Vertices
     */
    private void buildGraph() {
        Graph graph_first = new Graph(Vertices, Edges); // don't DELETE this line. Error
        Vertices = new ArrayList<>();
        Edges = new ArrayList<>();

        graph_first.creatingNewLinkedList();

        buildVertex();
        buildEdge();
    }

    /**
     * adding Vertices by adding its ID and name,
     * which are the same according to the .graphml file, from the vertexIDS ArrayList.
     * ID and name are the same so we just add 2 identical vertexIDs
     */
    private void buildVertex(){
        for (int i = 0; i < vertexNum; i++) {
            Vertex vertex = new Vertex(i, vertexIDs.get(i));
            Vertices.add(vertex);
        }
    }

    /**
     * adding edges which use a specific function by adding a left(start) and a right(target) vertex
     * and also its weight, from 3 ArrayList edgeIDS, edges and edgeWeights.
     *
     * The adding edges function has to be called twice, as we are calculating directed graph
     * weight from start to target vertex and vice versa are the same.
     *
     * Below is 2 lines which do almost same things.
     * But it will store in the storeWeigh 2-dimension String
     * to calculate the shortestPath
     */
    private void buildEdge(){
        for(int i = 0; i < edgeNum;i++) {
            addEdge(i, edges.get(i).getL(), edges.get(i).getR(), edgeWeights.get(i));
            addEdge(i, edges.get(i).getR(), edges.get(i).getL(), edgeWeights.get(i));
            storeWeigh[Integer.parseInt(edges.get(i).getL())][Integer.parseInt(edges.get(i).getR())] = Integer.parseInt(edgeWeights.get(i).trim());
            storeWeigh[Integer.parseInt(edges.get(i).getR())][Integer.parseInt(edges.get(i).getL())] = Integer.parseInt(edgeWeights.get(i).trim());
        }
    }

    /**
     * This handleFile is used to properly read a file using InputStream and Buffer 4 characters at the time
     * warning if there is no input file
     * @param file which is the args[0]
     * @param encoding using the defauft charset encoding
     * @exception  IOException if the stream of file cannot be written
     */
    private void handleFile(File file, Charset encoding) throws IOException {
        if (file == null){
            LOG.log(Level.WARNING, "File does not exist. Please try again");
        }
        // error handling if file does not exist
        assert file != null;

        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             Reader buffer = new BufferedReader(reader)) {
            handleCharacters(buffer);
        }
    }

    /**
     * This function use Reader, only when reader reaches the end of file, will it stop
     * storing each character in the allChar array for further processing
     * integer value count use to index all characters
     *
     * @param reader uses to read character by character
     * @throws IOException if the stream of file cannot be written
     * @see com.minhkhuonglu.Main@compareFourCharacterToFindItsCharacteristics()
     */
    private void handleCharacters(Reader reader) throws IOException {
        // error handling if file does not have any character
        if (reader == null){
            LOG.log(Level.WARNING,"File does not have any characters. Type something");
        }
        assert reader != null;
        int endOfFile = reader.read();

        while (endOfFile  != -1) {
            char ch = (char)endOfFile;
            allChar[indexOfChar] = ch;
            indexOfChar++;
            endOfFile = reader.read();
        }
        compareFourCharacterToFindItsCharacteristics();
    }

    /**
     * This function check 4 characters at a time. startCheck boolean tells whether
     * it should start counting due to some extra e_id, v_id that don't have value before <graph> </graph>
     * using the keyword "defa" (default)
     * String expandToBrackets will find a number between "> and "<" by adding more space from 4 to 30
     *
     * using switch to compare whether it should add an edgeID, a vertexID, a weight or an edge
     */
    private void compareFourCharacterToFindItsCharacteristics(){
        // change charArray to String for comparison
        String allCharString = new String(allChar);

        boolean startCheck = false;

        LOG.log(Level.FINE, "Start reading the .graphml file");
        for(int character = 0; character < indexOfChar-30;character++) {
            String fourCharAtATime = allCharString.substring(character, character + 4);

            String expandToBrackets = allCharString.substring(character, character + 30);
            expandToBrackets = expandToBrackets.replaceAll("[^-?0-9]+", " ");

            // check 4 number at a time
            // compare for edgeID and vertexID
            // if found the word "defa" in the word "default" start checking
            if (fourCharAtATime.equals("defa")) {
                startCheck = true;
            }
            if (startCheck) {
                switch (fourCharAtATime) {
                    // check for the word "e_id"
                    case edgeId: {
                        edgeIDs.add(expandToBrackets);
                        edgeNum++;
                        break;
                    }
                    // check for the word "v_id
                    case vertexID: {
                        vertexIDs.add(expandToBrackets);
                        vertexNum++;
                        break;
                    }
                    // check for the word "e_wei"
                    case edgeWeight: {
                        edgeWeights.add(expandToBrackets);
                        edgeWeightNum++;
                        break;
                    }
                    case sourceOfEdge: {
                        // remove space in the expandToBrackets String
                        // take out 2 number from the expandToBrackets and put them into a new List
                        List<String> lineThatContainsSourceAndTargetOfAnEdge = Arrays.asList(expandToBrackets.trim().split(" "));
                        // add 2 value in the new List to the edges ArrayList
                        edges.add(new MakePair<>(lineThatContainsSourceAndTargetOfAnEdge.get(0), lineThatContainsSourceAndTargetOfAnEdge.get(1)));
                    }
                }
            }
        }
    }

    /**
     * This function will be call Dijkstra from all pairs of vertices by printing out
     * the float value betweenness which are adding for every pair for vertices
     * except for the vertex being pass and if the start and destination vertex are the same
     *
     * First, it will call 2 Dijkstra from start and the pass vertex ( vertex needed to be pass)
     * Then executes it
     * as we already saving the value of Dijkstra and number of shortest path
     * we just need to call it when calculating to reduce number of tasks
     * 
     * In order to compute the number of shortest path passes throught a vertex
     * we use the formula:  
     * if ( d(start,pass) + d(pass,end) != d(start,end) ) numPass = 0
     * else numPass = numOfPath(start,pass) + numOfPath(pass,end)
     * where d(a,b) = shortest path from a to b
     *       numPass = number of shortest path that pass through a vertex
     *       numOfPath(a,b) = number of shortest path from a to b
     * 
     * It will calculate number of shortest paths between 2 Vertices
     * also shortest paths between 2 Vertices that go through the vertex needs to be passed
     *
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     * @param pass which is the vertex needs to be passed through
     */
    private double calculateBetweennessCentrality(Vertex pass){
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra dijkstra1 = new Dijkstra(graph);

        // calculate betweenness centrality
        float betweennessCentrality = 0;
        int numberOfPassingVertex = pass.getID();

        for(int from = 0 ;from < vertexNum; from++) {
            Vertex start = Vertices.get(from);
            dijkstra.executeDijkstra(start);
            dijkstra1.executeDijkstra(pass);

            for( int to  = 0; to < vertexNum; to++) {
                Vertex destination = Vertices.get(to);

                // find paths from vertex that is not the same and also not equal to the pass vertex
                // and also the value of its has to be smaller than the destination node because 0 to 6 are the sam as 6 to 0
                if (start != destination && start != pass && destination != pass && from < to ) {

                    // these variable are reset after calculate a pair of start and destination vertex
                    LOG.log(Level.FINE, "Find and counting all shortest paths between vertex " + start.toString().trim() + " and " + destination.toString().trim());
                    numberOfShortestPathPass = 0;

                    if (allDijkstra[from][numberOfPassingVertex] + allDijkstra[numberOfPassingVertex][to] != allDijkstra[from][to] ){
                        numberOfShortestPathPass = 0;
                    } else{
                        numberOfShortestPathPass = dijkstra.calculateTotalShortestPath(pass) * dijkstra1.calculateTotalShortestPath(destination);
                    }

                    LOG.log(Level.FINE, "Total weight of the path is: " + dijkstra.returnTotalWeight(destination));
                    LOG.log(Level.FINE, numberOfShortestPathPass + " shortest path(s) that passed through " + pass.toString().trim() );
                    LOG.log(Level.FINE, numberOfShortestPath[from][to] + " shortest path(s) between 2 Vertices");
                    LOG.log(Level.FINE, numberOfShortestPathPass + " / " + numberOfShortestPath[from][to] + " = " + numberOfShortestPathPass/numberOfShortestPath[from][to] +'\n');

                    betweennessCentrality += numberOfShortestPathPass/numberOfShortestPath[from][to];
                }
            }
        }

        LOG.log(Level.FINE, "Betweenness is: " + Math.round(betweennessCentrality * 100.00) / 100.00);
        return Math.round(betweennessCentrality * 100.00) / 100.00;
    }

    /**
     * This function creates a linked list in order to store all Vertices that created the shortest path
     *
     * as dijkstra is used to check connectivity and calculate the diameter also
     * we need to have a boolean value onlyConnected to choose which action should be performed
     * if !onlyConnected we just calculate the shortest path using Dijkstra as normal
     * otherwise, we will use it to calculate the connectivity and the diameter
     *
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     * @param sourceVertex the start vertex
     * @param targetVertex the end vertex
     * @throws java.lang.NullPointerException if the graph is unconnected
     */
    private void DijkstraCall(Vertex sourceVertex, Vertex targetVertex){
        // Create a graph from the Vertices and Edges list above
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);

        LOG.log(Level.FINE, "Calling dijkstra");
        dijkstra.executeDijkstra(sourceVertex);
        LinkedList<Vertex> path = dijkstra.getPath(targetVertex);

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0

        if (!onlyConnected) {
            LOG.log(Level.INFO, "Total weight of the path is: " + dijkstra.returnTotalWeight(targetVertex));
            LOG.log(Level.INFO, "Shortest path from vertex " + sourceVertex + " to vertex " + targetVertex + " is: ");
            outputAShortestPath(path);
        }
        else {
            if (dijkstra.isConnected()) {
                LOG.log(Level.INFO, "The graph is connected");
            }
            try {
                LOG.log(Level.INFO, "The diameter of the graph is: " + findTheLongestShortestPath(graph));
            }
            catch (java.lang.NullPointerException ex ){
                LOG.log(Level.INFO, "The graph is not connected");
                LOG.log(Level.INFO, "The diameter of the graph is: oo (infinity)");
            }
        }
    }

    /**
     * this function only use for checking the connectivity of the graph
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     * @param sourceVertex any vertex
     * @param targetVertex any vertex
     * @return is the graph connected?
     */
    private boolean isGraphConnected(Vertex sourceVertex, Vertex targetVertex){
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.executeDijkstra(sourceVertex);
        LinkedList<Vertex> path = dijkstra.getPath(targetVertex);

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0
        return dijkstra.isConnected();
    }
    /**
     * This function calculates all shortest paths between a pair of vertices
     * to find the shortest path that has the longest path
     * which means if ( d(-1-2-3-4) == d(-1-2-3-4-5-6-7) )
     * =>  -1-2-3-4-5-6-7 is the diameter because it contains more vertices
     * where d(-1-2-3-4): the weight of shortest path from 1 to 4
     *
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     * @param graph the graph that needs to be calculate
     * @return the maximum paths, which is the diameter
     */
    private int findTheLongestShortestPath(Graph graph){
        Dijkstra findShortestPath = new Dijkstra(graph);
        // call Dijkstra from all pairs of vertices
        int maxPath = -1;
        int diameterOfGraph = -1;
        for (Vertex start : Vertices) {
            findShortestPath.executeDijkstra(start);
            for (Vertex destination : Vertices) {
                if (start != destination) {
                    LinkedList<Vertex> paths = findShortestPath.getPath(destination);
                    // find a longer path size
                    if (paths.size() > maxPath) {
                        maxPath = paths.size();
                        diameterOfGraph = findShortestPath.returnTotalWeight(destination);
                    }
                }
            }
        }
        return diameterOfGraph;
    }

    /**
     * print out the path more readable
     * @param path linked list to store all vertices goes through
     */
    private void outputAShortestPath(LinkedList<Vertex> path){
        for (Vertex vertex : path) {
            LOG.log(Level.INFO, vertex + " -> ");
        }
        LOG.log(Level.INFO, "Done");
    }

    /**
     * adding an edge into the Edges List
     * also for the adjacencyList[] which stores the edge according to its start vertex
     * @param laneId ID of the edge
     * @param sourceLocationNumber start vertex
     * @param destinationLocationNumber end vertex
     * @param weight the weight of the edge
     * @throws java.lang.NullPointerException if the adjacencyList hasn't been created yet
     */
    private void addEdge(int laneId, String sourceLocationNumber, String destinationLocationNumber, String weight) {
        Edge lane = new Edge(laneId, Vertices.get(Integer.parseInt(sourceLocationNumber)), Vertices.get(Integer.parseInt(destinationLocationNumber)), weight );
        Edges.add(lane);
    }

    /**
     * print out all edges'ID
     * @param listEdgeIDs store all edgeIDs
     */
    private void printEdgeIDs(ArrayList<String> listEdgeIDs){
        LOG.log(Level.INFO, "There are " + edgeNum + " edges. ");
        LOG.log(Level.INFO, "The edge IDS are: ");

        printAll(listEdgeIDs);
    }

    /**
     * print out all Vertices'ID
     * @param listvertexIDs store all vertexIDs
     */
    private void printvertexIDs(ArrayList<String> listvertexIDs){
        LOG.log(Level.INFO, "There are " + vertexNum + " Vertices. ");
        LOG.log(Level.INFO, "The vertex IDS are: ");

        printAll(listvertexIDs);
    }

    /**
     * print all things that others need as an ArrayList
     * @param list list of anything
     */
    private void printAll(ArrayList<String> list){
        LOG.log(Level.INFO, " : " + list );
    }

    /**
     * print out all pair of source and target vertices in an edge
     * @param listSourceTarget list of all edges
     */
    private void printMapOut(List<MakePair<String, String>> listSourceTarget){
        LOG.log(Level.FINE, "The source and target vertex of every edges: ");

        for (MakePair<String, String> element : listSourceTarget) {
            LOG.log(Level.FINE, element.getL() + "->" + element.getR() + " ");
        }
    }

    /**
     * This function will output all the properties of the graph into a new file
     * @param fileName file .graphml to output
     */
    private void outputingFile(String fileName) {
        try {
            System.setOut(new PrintStream(new FileOutputStream((fileName))));
        } catch (FileNotFoundException e){
            LOG.log(Level.WARNING,"File not found");
        }

        System.out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        System.out.print("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n");
        System.out.print("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        System.out.print("         xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n");
        System.out.print("         http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
        System.out.print("<!-- Created by igraph -->\n");
        System.out.print("  <key id=\"v_id\" for=\"vertex\" attr.name=\"id\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"e_id\" for=\"edge\" attr.name=\"id\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"e_weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"total_vertex\" for=\"vertex\" attr.name=\"weight\" attr.type=\"double\"/>\n");                    System.out.print("  <key id=\"total\" for=\"vertex\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"total_edge\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"connected\" for=\"edge\" attr.name=\"weight\" attr.type=\"boolean\"/>\n");
        System.out.print("  <key id=\"diameter\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <graph id=\"G\" edgedefault=\"undirected\">\n");
        System.out.print("    <vertex id=\"total\">\n");
        System.out.print("      <data key=\"total_vertex\">" + vertexNum + "</data>\n");
        System.out.print("      <data key=\"total_vertex\">" + vertexIDs + "</data>\n");
        System.out.print("    </vertex>\n");

        for(int startVertex = 0; startVertex < vertexNum; startVertex++){
            System.out.print("    <vertex id=\"n" + startVertex + "\">\n");
            System.out.print("      <data key=\"v_id\">" + startVertex + "</data>\n");

            double betweenness = calculateBetweennessCentrality(Vertices.get(startVertex));

            for (int endVertex = 0; endVertex < vertexNum;endVertex++){
                if (startVertex != endVertex) {
                    System.out.print("      <dijkstra to=\"n" + endVertex + "\">" + APathBetweenTwoVertices[startVertex][endVertex] + " = " + allDijkstra[startVertex][endVertex] + "</dijkstra>\n");
                }else{
                    System.out.print("      <dijkstra to=\"n" + endVertex + "\">[ " + APathBetweenTwoVertices[startVertex][endVertex] + " ] = " + allDijkstra[startVertex][endVertex] + "</dijkstra>\n");
                }
            }
            if (startVertex == vertexNum/2) {
                LOG.log(Level.INFO, "Half way now!");
            }
            System.out.print("      <betweenness of=\"n" + startVertex + "\">" + betweenness + "</betweenness>\n");
            System.out.print("    </vertex>\n");
        }
        System.out.print("    <edge source=\"all\" target=\"all\">\n");
        System.out.print("      <data key=\"total_edge\">" + edgeNum + "</data>\n");
        System.out.print("      <data key=\"total_edge\">" + edgeIDs + "</data>\n");
        System.out.print("    </edge>\n");

        for (int edgeNumber = 0; edgeNumber < edgeNum;edgeNumber++) {
            System.out.print("    <edge source=\"n" + edges.get(edgeNumber).getL().trim() + "\" target=\"n" + edges.get(edgeNumber).getR().trim() + "\">\n");
            System.out.print("      <data key=\"e_id\">"+ edgeIDs.get(edgeNumber).trim() + "</data>\n");
            System.out.print("      <data key=\"e_weight\">" + edgeWeights.get(edgeNumber).trim() + "</data>\n");
            System.out.print("    </edge>\n");
        }
        System.out.print("    <edge source=\"all\" target=\"all\">\n");
        System.out.print("      <data key=\"connected\">" + isGraphConnected(Vertices.get(1),Vertices.get(2)) + "</data>\n");
        System.out.print("    </edge>\n");
        System.out.print("    <edge source=\"all\" target=\"all\">\n");
        System.out.print("      <data key=\"diameter\">" + diameterOfGraph +"</data>\n");
        System.out.print("    </edge>\n");
        System.out.print("  </graph>\n");
        System.out.print("</graphml>\n");
    }

    /**
     * This function is to create a Logger for further use
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

}

/**
 * this multi-threading is used for dividing the task require for
 * calculating all pair shortest path into half
 * first thread calculate from 0 to number of vertex / 2
 * second thread calculate the other half
 */
class MultiThreading implements Runnable {
    /**
     * first thread
     */
    private Thread t1;

    /**
     * second thread
     */
    private Thread t2;
    private String thread1;
    private String thread2;

    /**
     * Constructor for open a thread to calculate from start vertex to end vertex
     * this will be used to create 2 instances to calculate from start to vertex/2 and vertex/2 to end
     * @param start vertex
     * @param end vertex
     */
    MultiThreading(int start, int end){
        Main.calculateAllDijkstra(start,end);
    }

    /**
     * this function is used to run a thread
     */
    @Override
    public void run() {
        Logger.getLogger("Running "+ thread1);
        Logger.getLogger("Running "+ thread2);

        try{
            Logger.getLogger("Starting multi-threading");
            Thread.sleep(1);
        }catch (InterruptedException e){
            Logger.getLogger("Thread " + thread1 + " interrupted");
            Logger.getLogger("Thread " + thread2 + " interrupted");
        }
    }

    /**
     * this function will start a new thread
     * in this case, we create 2 new threads
     */
    public void start(){
        if (t1 == null){
            t1 = new Thread(this, String.valueOf(thread1));
            t2 = new Thread(this, String.valueOf(thread2));
            t1.start();
            t2.start();
        }
    }
}

