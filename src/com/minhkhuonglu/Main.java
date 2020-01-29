package com.minhkhuonglu;

import static com.minhkhuonglu.Graph.*;
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
     * The ArrayLists edges stores the start and the destination node of an edge
     * The ArrayLists nodeIDs stores all the ID of Vertices
     * The ArrayLists edgeIDs stores all the ID of edges
     * The ArrayLists edgeWeights stores only the weight of edges
     */
    private static ArrayList<MakePair<String,String>> edges = new ArrayList<>();
    private static ArrayList<String> nodeIDs = new ArrayList<>();
    private static ArrayList<String> edgeIDs = new ArrayList<>();
    private static ArrayList<String> edgeWeights = new ArrayList<>();

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
     * The float value calculates all shortest path from all paths between 2 Vertices that passed through a node
     * this value will be also be reset after calculating a pair of node
     * @see com.minhkhuonglu.Main#CalculateBetweennessCentrality(Vertex)
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
     * The String nodeID replaces for the "v_id"
     * The String edgeWeight replaces for the "e_we"
     * The String sourceOfEdge replaces for the "sour"
     * Reason for doing this is checking 4 characters at the same time when reading a file
     * @see com.minhkhuonglu.Main#handleCharacters
     */
    final static String edgeId = "e_id";
    final static String nodeID = "v_id";
    final static String edgeWeight = "e_we";
    final static String sourceOfEdge = "sour";

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
     * if length is 3, it will calculate the between centrality measure of a node if the second args is -b
     * otherwise, it will output the whole result into a new .graphml file
     * if length is 4, it will find the shortest path between 2 Vertices
     * @param args which are all arguments when calling the .jar file
     * @throws IOException if stream to file cannot be written
     */
    public static void main(String[] args) throws IOException, IncorrectFileExtensionException, InterruptedException {
        long start = System.currentTimeMillis();
        
        Main graph = new Main();
        ParallelThread countUp = new ParallelThread(1,3,true);
        ParallelThread countDown = new ParallelThread(10,8,true);
        ParallelThread countAgain = new ParallelThread(1,5,true);
        ParallelThread startDoCalculation = new ParallelThread(1,1, false);

        Thread cUp = new Thread(countUp);
        Thread cDown = new Thread(countDown);
        Thread cAgain = new Thread(countAgain);
        Thread startCal = new Thread(startDoCalculation);

        LOG.log(Level.FINE, "Preparing to build and display the properties of the graph: ");
        cUp.start();
        cDown.start();
        cUp.join();
        cDown.join();
        LOG.log(Level.FINE, "System crashed, trying to prepare again.");

        cAgain.start();
        cAgain.join();
        LOG.log(Level.FINE, "Done preparing! ");

        // check whether it is .graphml file
        try {
            graph.isCorrectFileExtension(args[0]);
        } catch (IllegalArgumentException err){
            LOG.log(Level.FINE, "Wrong file extension " + err);
        }

        graph.readFileAndBuildGraph(args[0]);

        // initialize two 2D-array
        allDijkstra = new int[vertexNum][vertexNum];
        numberOfShortestPath = new float[vertexNum][vertexNum];

        startCal.start();

        // this line is used without thread
//        calculateAllDijkstra(0,vertexNum);

        MultiThreading R1 = new MultiThreading(0,vertexNum/2);
        R1.start();
        MultiThreading R2 = new MultiThreading(vertexNum/2, vertexNum);
        R2.start();

        LOG.log(Level.FINE, "Done building");
        // checking how many arguments are pasted in order to choose the right operation
        if (args.length == 1) {
            onlyConnected =  true; // to choose whether to print out properties or just the Dijkstra of 2 Vertices

            // true for the properties
            LOG.log(Level.FINE, "The properties of the graph are: ");

            // print out the nodeIDs
            graph.printNodeIDs(nodeIDs);
            // print out the edgeIDS
            graph.printEdgeIDs(edgeIDs);
            // print out the edges source and target
            graph.printMapOut(edges);

            // use Dijkstra to check the connectivity and the diameter of the graph
            graph.DijkstraCall(Vertices.get(1), Vertices.get(2));

        } else if (args.length == 3){
            if (args[1].equals("-b")) {
                int passVertex = Integer.parseInt(args[2]);
                // calculate betweenness centrality measure for a specific node
                graph.CalculateBetweennessCentrality(Vertices.get(passVertex));

            }
            else if (args[1].equals("-a")){
                try{
                    Files.deleteIfExists(Paths.get("/" + args[2]));
                }
                catch(NoSuchFileException e) {
                    LOG.warning("No such file/directory exists");
                }
                catch(DirectoryNotEmptyException e) {
                    LOG.warning("Directory is not empty.");
                }
                catch(IOException e) {
                    LOG.warning("Invalid permissions.");
                }

                LOG.log(Level.FINE, "Delete old file successful.");
                LOG.log(Level.FINE, "Output file are being created");

                try{
                    // output into a file
                    graph.outputingFile(args[2]);
                } catch (Exception ignored){}
                LOG.log(Level.FINE, "File creation is finished!");
            }
        }
        else {
            if (args[1].equals("-s")) {
                int sourceNode,targetNode;

                sourceNode = Integer.parseInt(args[2]);
                targetNode = Integer.parseInt(args[3]);
                // call the Dijkstra algorithms
                graph.DijkstraCall(Vertices.get(sourceNode), Vertices.get(targetNode));
            }
        }

        long end = System.currentTimeMillis();
        long elapsed_time = end - start;
        LOG.log(Level.WARNING, "Running time: " + elapsed_time + " ms");
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
        System.out.print("  <key id=\"v_id\" for=\"node\" attr.name=\"id\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"e_id\" for=\"edge\" attr.name=\"id\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"e_weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"total_node\" for=\"node\" attr.name=\"weight\" attr.type=\"double\"/>\n");                    System.out.print("  <key id=\"total\" for=\"node\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"total_edge\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <key id=\"connected\" for=\"edge\" attr.name=\"weight\" attr.type=\"boolean\"/>\n");
        System.out.print("  <key id=\"diameter\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
        System.out.print("  <graph id=\"G\" edgedefault=\"undirected\">\n");
        System.out.print("    <node id=\"total\">\n");
        System.out.print("      <data key=\"total_node\">" + vertexNum + "</data>\n");
        System.out.print("    </node>\n");

        for(int i = 0; i < vertexNum; i++){
            System.out.print("    <node id=\"n" + i + "\">\n");
            System.out.print("      <data key=\"v_id\">" + i + "</data>\n");

            double betweenness = CalculateBetweennessCentrality(Vertices.get(i));

            for (int j = 0; j < vertexNum;j++){
                System.out.print("      <dijkstra to=\"n" + j + "\">" + allDijkstra[i][j] + "</dijkstra>\n");
            }
            if (i == vertexNum/2) {
                LOG.log(Level.FINE, "Please be patient!");
            }
            System.out.print("      <betweenness of=\"n" + i + "\">" + betweenness + "</betweenness>\n");
            System.out.print("    </node>\n");
        }
        System.out.print("    <edge source=\"all\" target=\"all\">\n");
        System.out.print("      <data key=\"total_edge\">" + edgeNum +"</data>\n");
        System.out.print("    </edge>\n");

        for (int i = 0; i < edgeNum;i++) {
            System.out.print("    <edge source=\"n" + edges.get(i).getL().trim() + "\" target=\"n" + edges.get(i).getR().trim() + "\">\n");
            System.out.print("      <data key=\"e_id\">"+ edgeIDs.get(i).trim() + "</data>\n");
            System.out.print("      <data key=\"e_weight\">" + edgeWeights.get(i).trim() + "</data>\n");
            System.out.print("    </edge>\n");
        }
        System.out.print("    <edge source=\"all\" target=\"all\">\n");
        System.out.print("      <data key=\"connected\">" + onlyConnectedGraph(Vertices.get(1),Vertices.get(2)) + "</data>\n");
        System.out.print("    </edge>\n");
        System.out.print("    <edge source=\"all\" target=\"all\">\n");
        System.out.print("      <data key=\"diameter\">" + diameterOfGraph +"</data>\n");
        System.out.print("    </edge>\n");
        System.out.print("  </graph>\n");
        System.out.print("</graphml>\n");
    }

    /**
     * check if the extension of the file is correct
     * @param fileExtension a *.graphml file
     */
    private void isCorrectFileExtension(String fileExtension){
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
     * which are the same according to the .graphml file, from the nodeIDS ArrayList.
     * ID and name are the same so we just add 2 identical nodeIDs
     */
    private void buildVertex(){
        for (int i = 0; i < vertexNum; i++) {
            Vertex location = new Vertex(i, nodeIDs.get(i));
            Vertices.add(location);
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
        int r = reader.read();

        while (r  != -1) {
            char ch = (char)r;
            allChar[indexOfChar] = ch;
            indexOfChar++;
            r = reader.read();
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
        for(int i = 0; i < indexOfChar-30;i++) {
            String fourInATime = allCharString.substring(i, i + 4);

            String expandToBrackets = allCharString.substring(i, i + 30);
            expandToBrackets = expandToBrackets.replaceAll("[^-?0-9]+", " ");

            // check 4 number at a time
            // compare for edgeID and nodeID
            // if found the word "defa" in the word "default" start checking
            if (fourInATime.equals("defa")) {
                startCheck = true;
            }
            if (startCheck) {
                switch (fourInATime) {
                    // check for the word "e_id"
                    case edgeId: {
                        edgeIDs.add(expandToBrackets);
                        edgeNum++;
                        break;
                    }
                    // check for the word "v_id
                    case nodeID: {
                        nodeIDs.add(expandToBrackets);
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
                        List<String> a = Arrays.asList(expandToBrackets.trim().split(" "));
                        // add 2 value in the new List to the edges ArrayList
                        edges.add(new MakePair<>(a.get(0), a.get(1)));
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
     * First, it will call 2 Dijkstra from start and the pass node ( node needed to be pass)
     * Then executes it
     * as we already saving the value of Dijkstra and number of shortest path
     * we just need to call it when calculating to reduce number of tasks
     * 
     * In order to compute the number of shortest path passes throught a node
     * we use the formula:  
     * if ( d(start,pass) + d(pass,end) != d(start,end) ) numPass = 0
     * else numPass = numOfPath(start,pass) + numOfPath(pass,end)
     * where d(a,b) = shortest path from a to b
     *       numPass = number of shortest path that pass through a node
     *       numOfPath(a,b) = number of shortest path from a to b
     * 
     * It will calculate number of shortest paths between 2 Vertices
     * also shortest paths between 2 Vertices that go through the node needs to be passed
     *
     * @param pass which is the node needs to be passed through
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    private double CalculateBetweennessCentrality(Vertex pass){
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);
        Dijkstra dijkstra1 = new Dijkstra(graph);

        // calculate betweenness centrality
        float betweenness = 0;
        int passNum = pass.getID();

        for(int i = 0 ;i < vertexNum; i++) {
            Vertex start = Vertices.get(i);
            dijkstra.executeDijkstra(start);
            dijkstra1.executeDijkstra(pass);

            for( int j  = 0; j < vertexNum; j++) {
                Vertex destination = Vertices.get(j);

                // find paths from node that is not the same and also not start or end with the pass node
                if (start != destination && start != pass && destination != pass && i < j ) {

                    // these variable are reset after calculate a pair of start and destination node
                    LOG.log(Level.FINE, "Find and counting all shortest paths between node " + start.toString().trim() + " and " + destination.toString().trim());
                    numberOfShortestPathPass = 0;

                    if (allDijkstra[i][passNum] + allDijkstra[passNum][j] != allDijkstra[i][j] ){
                        numberOfShortestPathPass = 0;
                    } else{
                        numberOfShortestPathPass = dijkstra.calculateTotalShortestPath(pass) * dijkstra1.calculateTotalShortestPath(destination);
                    }

                    LOG.log(Level.FINE, "Total weight of the path is: " + dijkstra.returnTotal_Weight(destination));
                    LOG.log(Level.FINE, numberOfShortestPathPass + " shortest path(s) that passed through " + pass.toString().trim() );
                    LOG.log(Level.FINE, numberOfShortestPath[i][j] + " shortest path(s) between 2 Vertices");
                    LOG.log(Level.FINE, numberOfShortestPathPass + " / " + numberOfShortestPath[i][j] + " = " + numberOfShortestPathPass/numberOfShortestPath[i][j] +'\n');

                    betweenness += numberOfShortestPathPass/numberOfShortestPath[i][j];
                }
            }
        }

        LOG.log(Level.FINE, "Betweenness is: " + Math.round(betweenness * 100.00) / 100.00);
        return Math.round(betweenness * 100.00) / 100.00;
    }

    /**
     * This function creates a linked list in order to store all Vertices that created the shortest path
     *
     * as dijkstra is used to check connectivity and calculate the diameter also
     * we need to have a boolean value onlyConnected to choose which action should be performed
     * if !onlyConnected we just calculate the shortest path using Dijkstra as normal
     * otherwise, we will use it to calculate the connectivity and the diameter
     *
     * @param sourceNode the start vertex
     * @param targetNode the end vertex
     * @throws java.lang.NullPointerException if the graph is unconnected
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    private void DijkstraCall(Vertex sourceNode, Vertex targetNode){
        // Create a graph from the Vertices and Edges list above
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);

        LOG.log(Level.FINE, "Calling dijkstra");
        dijkstra.executeDijkstra(sourceNode);
        LinkedList<Vertex> path = dijkstra.getPath(targetNode);

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0

        if (!onlyConnected) {
            LOG.log(Level.FINE, "Total weight of the path is: " + dijkstra.returnTotal_Weight(targetNode));
            LOG.log(Level.FINE, "Shortest path from node " + sourceNode + " to node " + targetNode + " is: ");
            outputAShortestPath(path);
        }
        else {
            if (dijkstra.isConnected()) {
                LOG.log(Level.FINE, "The graph is connected");
            }
            try {
                LOG.log(Level.FINE, "The diameter of the graph is: " + findingTheLongestShortestPath(graph));
            }
            catch (java.lang.NullPointerException ex ){
                LOG.log(Level.FINE, "The graph is not connected");
                LOG.log(Level.FINE, "The diameter of the graph is: oo (infinity)");
            }
        }
    }

    /**
     * this function only use for checking the connectivity of the graph
     * @param sourceNode any node
     * @param targetNode any node
     * @return is the graph connected?
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    private boolean onlyConnectedGraph(Vertex sourceNode, Vertex targetNode){
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.executeDijkstra(sourceNode);
        LinkedList<Vertex> path = dijkstra.getPath(targetNode);

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0
        return dijkstra.isConnected();
    }
    /**
     * This function calculates all shortest paths between a pair of vertices
     * to find the shortest path that has the longest path
     * which means -1-2-3-4 < -1-2-3-4-5-6-7
     *
     * @param graph the graph that needs to be calculate
     * @return the maximum paths, which is the diameter
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    private int findingTheLongestShortestPath(Graph graph){
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
                        diameterOfGraph = findShortestPath.returnTotal_Weight(destination);
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
            LOG.log(Level.FINE, vertex + " -> ");
        }
        LOG.log(Level.FINE, "Done");
    }

    /**
     * adding an edge into the Edges List
     * also for the adjacencyList[] which stores the edge according to its start vertex
     * @param laneId ID of the edge
     * @param sourceLocNo start vertex
     * @param destLocNo end vertex
     * @param weight the weight of the edge
     * @throws java.lang.NullPointerException if the adjacencyList hasn't been created yet
     */
    private void addEdge(int laneId, String sourceLocNo, String destLocNo, String weight) {
        Edge lane = new Edge(laneId, Vertices.get(Integer.parseInt(sourceLocNo)), Vertices.get(Integer.parseInt(destLocNo)), weight );
        Edges.add(lane);
        try {
            adjacencyList[Integer.parseInt(sourceLocNo)].add(lane);
        } catch (NullPointerException e){
            LOG.warning("Null value");
        }
    }

    /**
     * print out all edges'ID
     * @param listEdgeIDs store all edgeIDs
     */
    private void printEdgeIDs(ArrayList<String> listEdgeIDs){
        LOG.log(Level.FINE, "There are " + edgeNum + " edges. ");
        LOG.log(Level.FINE, "The edge IDS are: ");

        printAll(listEdgeIDs);
    }

    /**
     * print out all Vertices'ID
     * @param listNodeIDs store all nodeIDs
     */
    private void printNodeIDs(ArrayList<String> listNodeIDs){
        LOG.log(Level.FINE, "There are " + vertexNum + " Vertices. ");
        LOG.log(Level.FINE, "The vertex IDS are: ");

        printAll(listNodeIDs);
    }

    /**
     * print all things that others need as an ArrayList
     * @param list list of anything
     */
    private void printAll(ArrayList<String> list){
        for (String a : list) {
            LOG.log(Level.FINE, a + " ");
        }
    }

    /**
     * print out all pair of source and target vertices in an edge
     * @param listSourceTarget list of all edges
     */
    private void printMapOut(List<MakePair<String, String>> listSourceTarget){
        LOG.log(Level.FINE, "The source and target node of every edges: ");

        for (MakePair<String, String> a : listSourceTarget) {
            LOG.log(Level.FINE, a.getL() + "->" + a.getR() + " ");
        }
    }

    /**
     * This function is to create a Logger for further use
     */
    public static final Logger LOG = Logger.getLogger(Main.class.getName());

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
        for (int i = from; i < to; i++) {
            Vertex start = Vertices.get(i);
            dijkstraFirst.executeDijkstra(start);
            for (int j = 0; j < vertexNum; j++) {

                Vertex destination = Vertices.get(j);
                LinkedList<Vertex> paths = dijkstraFirst.getPath(destination);
                numberOfShortestPath[i][j] = dijkstraFirst.calculateTotalShortestPath(destination);

                if (i < j) {
                    allDijkstra[i][j] = dijkstraFirst.returnTotal_Weight(destination);
                } else if (i == j) {
                    allDijkstra[i][j] = 0;
                } else {
                    allDijkstra[i][j] = allDijkstra[j][i];
                }

                // take from the findingTheLongestShortestPath
                if (start != destination) {
                    // find a longer path size
                    if (paths.size() > maxPath) {
                        maxPath = paths.size();
                        diameterOfGraph = dijkstraFirst.returnTotal_Weight(destination);
                    }
                }
            }
        }
    }
}

/**
 * this multi-threading is used for dividing the task require for
 * calculating all pair shortest path into half
 * first thread calculate from 0 to number of vertex / 2
 * second thread calculate the other half
 */
class MultiThreading implements Runnable {
    private Thread t1;
    private Thread t2;
    private String thread1;
    private String thread2;

    MultiThreading(int start, int end){
        Main.calculateAllDijkstra(start,end);
    }
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
    public void start(){
        if (t1 == null){
            t1 = new Thread(this, String.valueOf(thread1));
            t2 = new Thread(this, String.valueOf(thread2));
            t1.start();
            t2.start();
        }
    }
}
