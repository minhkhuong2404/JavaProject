package com.minhkhuonglu;

import static java.lang.System.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
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

    /**
     * This integer is used to continuously moving character by character in a file
     */
    static int indexOfChar = 0;

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
     * This array will be used to save the start and end vertex of the diameter
     * to prevent further calculating in the future
     */
    static String[] storeDiameter = new String[2];

    /**
     * Replaces some string in the .graphml file
     * The String edgeID replaces for the "e_id"
     * The String vertexID replaces for the "v_id"
     * The String edgeWeight replaces for the "e_we"
     * The String sourceOfEdge replaces for the "sour"
     * Reason for doing this is checking 4 characters at the same time when reading a file
     * @see com.minhkhuonglu.Main#readEachCharacterInAFile(java.io.Reader)
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
    public static String [][] aPathBetweenTwoVertices;

    /**
     * first check the extension of file whether it is .graphml or not
     * then starting reading the input .graphml file to take out the vertex and edge
     * initializing the allDijkstra and numberOfShortestPath 2D-array for further calculation
     *
     * open 2 Thread to calculating all necessary information which is
     * the shortest path between 2 vertices and the number of shortest path between them
     *
     * The main method used to distinguish the input argument that the user types in
     * to choose which needs to be print out or doing
     * divide the number of arguments are used when calling .jar file 1,3 or 4
     * if length is 1, it will print out properties of the graph
     *
     * if length is 2, it will throw error
     *
     * if length is 3,
     * in case of "-b"
     * it will calculate the between centrality measure of a vertex
     * in case of "-a"
     * it will output the whole result into a new .graphml file
     * in case of "-v"
     * it will print out the properties of a vertex
     * in case of "-e"
     * it will print out the properties of an edge
     *
     * if length is 4, it will find the shortest path between 2 Vertices
     *
     * if length is larger than 4, it will also throw error
     *
     * besides, there are lots of exception when the user type in the wrong syntax are also being handled
     * @param args which are all arguments when calling the .jar file
     * @throws IOException if stream to file cannot be written
     */
    public static void main(String[] args) throws IOException, IncorrectFileExtensionException {
        // start counting execute time
        long startTime = System.currentTimeMillis();

        Main graph = new Main();

        LOG.log(Level.INFO, "Preparing our program. Please hold on ");

        try{
            if (args[0] == null){
                LOG.log(Level.INFO,"Missing file");
            }
        } catch (ArrayIndexOutOfBoundsException e){
            System.out.println("==================================================");
            System.out.println("|   Welcome to our OOP Java Project -  Group 8   |");
            System.out.println("==================================================");
            System.out.println("                                                  ");
            System.out.println("**********  COMPUTER NETWORK ANALYSIS  ***********");
            System.out.println("                                                  ");
            System.out.println("==================================================");
            System.out.println("                                                  ");
            System.out.println("     Please provide a .graphml file to continue   ");
            System.out.println("We will show you step by step on how to use the program");
            System.out.println("                                                  ");
            System.out.println("==================================================");
            System.out.println("|             @Made by MinhKhuongLu              |");
            System.out.println("|            And members in Group 8              |");
            System.out.println("|                 @Github 2020                   |");
            System.out.println("==================================================");

            exit(0);
        }

        // check whether it is .graphml file
        try {
            graph.checkCorrectFileExtension(args[0]);
        } catch (IllegalArgumentException err){
            LOG.log(Level.WARNING, "Oh no! Look like you enter the wrong graph extension. " + err);
            exit(0);
        }

        graph.readFileAndBuildGraph(args[0]);

        // initialize two 2D-array
        allDijkstra = new int[vertexNum][vertexNum];
        numberOfShortestPath = new float[vertexNum][vertexNum];
        aPathBetweenTwoVertices = new String[vertexNum][vertexNum];

        if (vertexNum > 150){
            LOG.log(Level.INFO, "This file is quite large. So it may take a little bit longer. Please be patient");
        }

        // this line is used without thread
//        calculateAllDijkstra(0,vertexNum);

        // start 2 threads for faster calculation
        MultiThreading RunningThread1 = new MultiThreading(0, vertexNum / 2);
        RunningThread1.start();
        MultiThreading RunningThread2 = new MultiThreading(vertexNum / 2, vertexNum);
        RunningThread2.start();

        // checking how many arguments are pasted in order to choose the right operation
        if (args.length == 1) {

            // true for the properties
            System.out.println("The properties of the graph are: ");
            System.out.println("==================================================");
            // print out the vertexIDs
            graph.printVertexIDs(vertexIDs);
            System.out.println("==================================================");

            // print out the edgeIDS
            graph.printEdgeIDs(edgeIDs);
            System.out.println("==================================================");

            // print out the edges source and target
            graph.printEdgeOut(edges);
            System.out.println("==================================================");

            if (graph.isGraphConnected()){
                System.out.println("The graph is connected");
            }

            graph.calculateDiameter();

            System.out.println("==================================================");
            graph.showProgramInstruction();

        }
        else if (args.length == 3){
            if (!args[1].equals("-s") && !args[1].equals("-b") && !args[1].equals("-a") && !args[1].equals("-v") && !args[1].equals("-e")){
                System.out.println("Make sure that you use the right syntax of the program!");
                graph.showProgramInstruction();
                exit(0);
            }

            switch (args[1]) {

                case "-b":
                    // calculate betweenness centrality measure for a specific vertex
                    double betweennessCentrality;
                    if (args[2] == null) {
                        LOG.log(Level.INFO, "Please input a number");
                        exit(0);
                    }

                    try {
                        if (Integer.parseInt(args[2]) < 0){
                            LOG.log(Level.WARNING, "A vertix cannot be negative");
                            exit(0);
                        } else if ( Integer.parseInt(args[2]) >= vertexNum){
                            LOG.log(Level.WARNING,"Please enter a number that is not larger than "+ vertexNum +" as we only have " + vertexNum + " vertices");
                            exit(0);
                        }
                        betweennessCentrality = graph.calculateBetweennessCentrality(Vertices.get(Integer.parseInt(args[2])));
                        System.out.println("Betweenness centrality of " + Integer.parseInt(args[2]) + " is: " + betweennessCentrality);
                    } catch (NumberFormatException e) {
                        LOG.log(Level.WARNING, "Please type in a number: '" + args[2] + "' is not valid");
                        exit(0);
                    }

                    break;
                case "-a":

                    if (!args[2].contains(".graphml") && !args[2].contains(".xml")){
                        LOG.log(Level.WARNING,"Please enter a valid *.graphml or *.xml file. '" + args[2] + "' is detected");
                        exit(0);
                    }

                    try {
                        Files.deleteIfExists(Paths.get("/" + args[2]));
                    } catch (IOException e) {
                        LOG.log(Level.WARNING, "Invalid permissions.");
                    }

                    // output into a file
                    graph.outputFile(args[2]);
                    break;
                case "-s":
                    try {
                        int checkIfItIsANumber = Integer.parseInt(args[2]);
                        LOG.log(Level.INFO, "Please enter 1 more number: only '" + checkIfItIsANumber + "' found");
                        if (Integer.parseInt(args[2]) < 0){
                            LOG.log(Level.WARNING, "A vertix cannot be negative");
                            exit(0);
                        }
                    } catch ( NumberFormatException e){
                        LOG.log(Level.WARNING, "Please change '" + args[2] + "' into a number and add another number");
                    }
                    exit(0);
                    break;
                case "-v":
                    try {
                        Abstract vertexID = new Vertex(Integer.parseInt(args[2]));
                        if (Integer.parseInt(args[2]) < vertexNum && Integer.parseInt(args[2]) >= 0) {
                            System.out.println( "The properties of vertex '" + args[2] + "' are: ");
                            System.out.println( "Vertex ID: " + vertexID.getID());
                            System.out.println( "Number of edges pass through: " + graph.getNeighborsVertex(args[2]).size());
                            System.out.println( "Neighbor vertices are: " + graph.getNeighborsVertex(args[2]));
                        } else if (Integer.parseInt(args[2]) >= vertexNum){
                            LOG.log(Level.INFO, "Please type in a number no larger than " + vertexNum + " as we only have " + vertexNum + " vertices");
                            exit(0);
                        } else if (Integer.parseInt(args[2]) < 0){
                            LOG.log(Level.INFO, "Please type in a positive number " + Integer.parseInt(args[2]) + " found");
                            exit(0);
                        }
                    } catch (NumberFormatException e){
                        LOG.log(Level.WARNING, "Please change '" + args[2] + "' into a number");
                    }
                    exit(0);
                    break;
                case "-e":
                    try {
                        Edge edgeID = new Edge(Integer.parseInt(args[2]));
                        if (Integer.parseInt(args[2]) < edgeNum && Integer.parseInt(args[2]) >= 0 ) {
                            System.out.println( "The properties of edge '" + args[2] + "' are: ");
                            System.out.println( "Edge ID: " + edgeID.getID());
                            System.out.println( "Made from 2 vertices '" + Edges.get(Integer.parseInt(args[2])*2).getSource() + "' and '" + Edges.get(Integer.parseInt(args[2])*2).getDestination() + "'");
                            System.out.println( "Edge weight is: " + Edges.get(Integer.parseInt(args[2])*2).getWeight());
                        } else if (Integer.parseInt(args[2]) >= edgeNum){
                            LOG.log(Level.INFO, "Please type in a number no larger than " + edgeNum + " as we only have " + edgeNum + " edges");
                            exit(0);
                        } else if (Integer.parseInt(args[2]) < 0){
                            LOG.log(Level.INFO, "Please type in a positive number " + Integer.parseInt(args[2]) + " found");
                            exit(0);
                        }
                    } catch (NumberFormatException e) {
                        LOG.log(Level.WARNING, "Please change '" + args[2] + "' into a number");
                    }
                    exit(0);
                    break;
            }
        }
        else if (args.length == 4 ){
            if (!args[1].equals("-s") && !args[1].equals("-b") && !args[1].equals("-a") && !args[1].equals("-v") && !args[1].equals("-e")){
                System.out.println("Make sure that you use the right syntax of the program!");
                graph.showProgramInstruction();
                exit(0);
            }
            switch (args[1]) {
                case "-s":
                    try {
                        int checkIfFirstCharIsANumber = Integer.parseInt(args[2]);
                        int checkIfSecondCharIsANumber = Integer.parseInt(args[3]);
                        if (checkIfFirstCharIsANumber < 0 || checkIfSecondCharIsANumber < 0){
                            LOG.log(Level.WARNING, "A vertix cannot be negative");
                            exit(0);
                        } else if (checkIfFirstCharIsANumber >= vertexNum || checkIfSecondCharIsANumber >= vertexNum){
                            LOG.log(Level.WARNING,"Please enter a number that is not larger than "+ vertexNum +" as we only have " + vertexNum + " vertices");
                            exit(0);
                        }

                    } catch (NumberFormatException e) {
                        LOG.log(Level.WARNING, "Please type in 2 numbers not 1 or 2 character(s): '" + args[2] + "' and '" + args[3] + "' found");
                        exit(0);
                    }

                    int sourceVertex, targetVertex;

                    sourceVertex = Integer.parseInt(args[2]);
                    targetVertex = Integer.parseInt(args[3]);

                    try {
                        // call the Dijkstra algorithms
                        graph.callDijkstra(Vertices.get(sourceVertex), Vertices.get(targetVertex));
                    } catch (AssertionError e) {
                        LOG.log(Level.WARNING, "Oops!! Please choose 2 different vertices.");                        exit(0);
                        exit(0);
                    }
                    break;
                case "-a":
                    LOG.log(Level.INFO, "You only need to type into the name of the file only");
                    LOG.log(Level.INFO, "Please delete '" + args[3] +"'");
                    exit(0);
                    break;
                case "-b":
                    LOG.log(Level.INFO, "You only need to type 1 number to calculate the betweenness");
                    LOG.log(Level.INFO, "Please delete '" + args[3] +"'");
                    exit(0);
                    break;
                case "-v":
                    LOG.log(Level.INFO, "You only need to type 1 number to see the vertex properties");
                    LOG.log(Level.INFO, "Please delete '" + args[3] +"'");
                    exit(0);
                    break;
                case "-e":
                    LOG.log(Level.INFO, "You only need to type 1 number to see the edge properties");
                    LOG.log(Level.INFO, "Please delete '" + args[3] +"'");
                    exit(0);
                    break;
            }
        }
        else if (args.length == 2){
            if (!args[1].equals("-s") && !args[1].equals("-b") && !args[1].equals("-a") && !args[1].equals("-v") && !args[1].equals("-e")){
                System.out.println("Make sure that you use the right syntax of the program!");
                graph.showProgramInstruction();
                exit(0);
            }

            switch (args[1]) {
                case "-s":
                    LOG.log(Level.INFO, "Please enter 2 more numbers to calculate Dijkstra");
                    break;
                case "-a":
                    LOG.log(Level.INFO, "Please enter a *.graphml file to print out the result");
                    break;
                case "-b":
                    LOG.log(Level.INFO, "Please enter 1 number to calculate the betweenness centrality");
                    break;
                case "-v":
                    LOG.log(Level.INFO, "Please enter 1 number to see its vertex properties");
                    break;
                case "-e":
                    LOG.log(Level.INFO, "Please enter 1 number to see its edge properties");
                    break;
                default:
                    LOG.log(Level.INFO, "Maybe you enter the wrong syntax, please check it again");
            }
            exit(0);
        }
        else {
            LOG.log(Level.INFO, "Look like you entered too many values. Please check again the syntax");
            exit(0);
        }

        long endTime = System.currentTimeMillis();
        long elapsed_time = endTime - startTime;
        System.out.println("==================================================");
        System.out.println("You can re-run the project to see other functions as well");
        System.out.println("Exiting the project");
        System.out.println("Hope you enjoy our project in computer network analysis");
        System.out.println("Good bye. Have a nice day! <3");
        LOG.log(Level.WARNING, "Running time: " + elapsed_time + " ms");
    }

    /**
     * this function will iterating through all pair of Vertices
     * calculate and save the dijkstra value in the array allDijkstra
     * calculate and save the number of shortest path in the array numberOfShortestPath
     * calculate and comparing to find the diameter, which is the longest shortest path
     * @param from first half vertices
     * @param to second half vertices
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     */
    public static void calculateAllDijkstra(int from,int to) {
        Graph graphFirst = new Graph(Vertices, Edges);
        Dijkstra dijkstraFirst = new Dijkstra(graphFirst);
        int tempPath;
        for (int source = from; source < to; source++) {
            Vertex start = Vertices.get(source);
            dijkstraFirst.executeDijkstra(start);
            for (int target = 0; target < vertexNum; target++) {
                Vertex destination = Vertices.get(target);

                // store all shortest path value, number of paths between 2 nodes and a path between them
                if (source < target) {
                    allDijkstra[source][target] = dijkstraFirst.returnTotalWeight(destination);
                    numberOfShortestPath[source][target] = dijkstraFirst.calculateTotalShortestPath(destination);
                    aPathBetweenTwoVertices[source][target] = String.valueOf(dijkstraFirst.getPathFromAVertexToAnother(Vertices.get(target)));
                }
                // if the same, then return 0, except for the path, which is itself
                else if (source == target){
                    allDijkstra[source][target] = 0;
                    numberOfShortestPath[source][target] = 0;
                    aPathBetweenTwoVertices[source][target] = String.valueOf(source);
                }
                // shortest path from 0 to 6 are the same for 6 to 0, due to undirected graph
                else {
                    allDijkstra[source][target] = allDijkstra[target][source];
                    numberOfShortestPath[source][target] = numberOfShortestPath[target][source];
                    aPathBetweenTwoVertices[source][target] = aPathBetweenTwoVertices[target][source];

                }
                // find the longest path weight
                tempPath = dijkstraFirst.returnTotalWeight(destination);
                if (diameterOfGraph < tempPath){
                    diameterOfGraph = tempPath;
                }
            }
        }
    }

    /**
     * Show instruction on how to use the program correctly, without having errors
     */
    private void showProgramInstruction(){
        System.out.println("\"-s x y\" to calculate Dijkstra between 2 nodes ");
        System.out.println("\"-b x\" to calculate betweenness centrality of a node");
        System.out.println("\"-a output.graphml\" to print the result into output.graphml file");
        System.out.println("\"-v x\" to see the properties of vertex with ID x");
        System.out.println("\"-e x\" to see the properties of edge with ID x");
        System.out.println("where x, y are numbers");
        System.out.println("      *.graphml is the file you need.");
    }

    /**
     * get all vertices which are connected from a vertex by edges
     * by checking all edges if have the same source vertex or target vertex in an edge.
     * @param vertex start vertex
     * @return destination vertices
     */
    private List<String> getNeighborsVertex(String vertex) {
        List<String> neighbors = new ArrayList<>();
        for (MakePair<String, String> edge : edges) {
            if (edge.getL().equals(vertex)) {
                neighbors.add(edge.getR());
            } else if (edge.getR().equals(vertex)){
                neighbors.add(edge.getL());
            }
        }
        return neighbors;
    }

    /**
     * check if the extension of the file is correct
     * @param fileExtension a *.graphml file
     */
    private void checkCorrectFileExtension(String fileExtension){
        if (!fileExtension.contains(".graphml") && !fileExtension.contains(".xml")){
            throw new IncorrectFileExtensionException(
                    "Enter a valid *.graphml or *.xml file. " + fileExtension + " is detected");
        }else{
            LOG.log(Level.INFO, "Correct file extension");
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
        readInputFromAFile(file, encoding);
        buildGraph();
    }

    /**
     * This function used to build the graph for further calculation
     * which contains vertices and edges
     * this also creates new linked list to store the weight of the start vertex
     * to calculate all paths between 2 Vertices
     */
    private void buildGraph() {
        Vertices = new ArrayList<>();
        Edges = new ArrayList<>();

        addVerticesIntoGraph();
        addEdgesIntoGraph();
    }

    /**
     * adding Vertices by adding its ID and name,
     * which are the same according to the .graphml file, from the vertexIDS ArrayList.
     * ID and name are the same so we just add 2 identical vertexIDs
     */
    private void addVerticesIntoGraph(){
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
    private void addEdgesIntoGraph(){
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
     * @param encoding using the default charset encoding
     * @exception  IOException if the stream of file cannot be written
     */
    private void readInputFromAFile(File file, Charset encoding) throws IOException {
        if (file == null){
            LOG.log(Level.WARNING, "File does not exist. Please try again");
        }
        // error handling if file does not exist
        assert file != null;

        try (InputStream in = new FileInputStream(file);
             Reader reader = new InputStreamReader(in, encoding);
             // buffer for efficiency
             Reader buffer = new BufferedReader(reader)) {
            readEachCharacterInAFile(buffer);
        } catch (FileNotFoundException e){
            LOG.log(Level.WARNING, "File not found. Please check the name again");
            exit(0);
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
    private void readEachCharacterInAFile(Reader reader) throws IOException {
        // error handling if file does not have any character
        if (reader == null){
            LOG.log(Level.WARNING,"File does not have any characters. Type something");
        }
        assert reader != null;
        int endOfFile = reader.read();

        // read until EOF
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
        String allCharacterInAFile = new String(allChar);

        // to avoid some extra e_id or v_id appear in the start of file
        boolean startCheck = false;

        for(int character = 0; character < indexOfChar-30;character++) {
            // take 4 characters from the allChar char array to calculate
            String compareFourCharacterAtATime = allCharacterInAFile.substring(character, character + 4);

            // expand to get the value between 2 tags <>?<>
            String addValueBetweenTwoDataTags = allCharacterInAFile.substring(character, character + 30);
            addValueBetweenTwoDataTags = addValueBetweenTwoDataTags.replaceAll("[^-?0-9]+", " ");

            // check 4 number at a time
            // compare for edgeID and vertexID
            // if found the word "defa" in the word "default" start checking
            if (compareFourCharacterAtATime.equals("defa")) {
                startCheck = true;
            }
            if (startCheck) {
                switch (compareFourCharacterAtATime) {
                    // check for the word "e_id"
                    case edgeId: {
                        edgeIDs.add(addValueBetweenTwoDataTags);
                        edgeNum++;
                        break;
                    }
                    // check for the word "v_id
                    case vertexID: {
                        vertexIDs.add(addValueBetweenTwoDataTags);
                        vertexNum++;
                        break;
                    }
                    // check for the word "e_wei"
                    case edgeWeight: {
                        edgeWeights.add(addValueBetweenTwoDataTags);
                        edgeWeightNum++;
                        break;
                    }
                    case sourceOfEdge: {
                        // remove space in the expandToBrackets String
                        // take out 2 number from the expandToBrackets and put them into a new List
                        List<String> lineThatContainsSourceAndTargetOfAnEdge = Arrays.asList(addValueBetweenTwoDataTags.trim().split(" "));
                        // add 2 value in the new List to the edges ArrayList
                        edges.add(new MakePair<>(lineThatContainsSourceAndTargetOfAnEdge.get(0), lineThatContainsSourceAndTargetOfAnEdge.get(1)));
                        break;
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
     * In order to compute the number of shortest path passes through a vertex
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
            for( int to  = from; to < vertexNum; to++) {
                Vertex destination = Vertices.get(to);

                // find paths from vertex that is not the same and also not equal to the pass vertex
                // and also the value of its has to be smaller than the destination node because 0 to 6 are the sam as 6 to 0
                if (start != destination && start != pass && destination != pass ) {

                    // these variable are reset after calculate a pair of start and destination vertex
                    numberOfShortestPathPass = 0;

                    // applying the above formula
                    if (allDijkstra[from][numberOfPassingVertex] + allDijkstra[numberOfPassingVertex][to] != allDijkstra[from][to] ){
                        numberOfShortestPathPass = 0;
                    } else{
                        numberOfShortestPathPass = numberOfShortestPath[from][numberOfPassingVertex] * numberOfShortestPath[numberOfPassingVertex][to];
                    }

                    betweennessCentrality += numberOfShortestPathPass/numberOfShortestPath[from][to];
                }
            }
        }
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
    private void callDijkstra(Vertex sourceVertex, Vertex targetVertex){
        // Create a graph from the Vertices and Edges list above
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);

        dijkstra.executeDijkstra(sourceVertex);
        ArrayList<Vertex> path = dijkstra.getPathFromAVertexToAnother(targetVertex);

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0

        System.out.println("Total weight of the path is: " + dijkstra.returnTotalWeight(targetVertex));
        System.out.println("Shortest path from vertex " + sourceVertex + " to vertex " + targetVertex + " is: ");
        outputAShortestPath(path);
    }

    /**
     * This function is used to calculate the diameter of a graph
     */
    private void calculateDiameter(){
        // Create a graph from the Vertices and Edges list above
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);

        dijkstra.executeDijkstra(Vertices.get(0));
        ArrayList<Vertex> path = dijkstra.getPathFromAVertexToAnother(Vertices.get(1));

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0

        System.out.println("==================================================");
        System.out.println("The diameter of the graph is: " + findTheLongestShortestPath(graph));
        System.out.println("From node '" + storeDiameter[0] + "' to '" + storeDiameter[1] + "'");

    }

    /**
     * this function only use for checking the connectivity of the graph
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     * @return is the graph connected?
     */
    private boolean isGraphConnected(){
        Graph graph = new Graph(Vertices, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);
        dijkstra.executeDijkstra(Vertices.get(0));
        ArrayList<Vertex> path = dijkstra.getPathFromAVertexToAnother(Vertices.get(1));

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0
        return dijkstra.isConnected();
    }

    /**
     * This function calculates all shortest paths between a pair of vertices
     * to find the shortest path that has the longest path
     * which means if ( d(-1-2-3-4) > any other d(-x-y-z-...) )
     * =>  d(-1-2-3-4) is the diameter because it contains more vertices
     * where d(-1-2-3-4): the weight of shortest path from 1 to 4
     *
     * @see com.minhkhuonglu.Dijkstra#executeDijkstra(Vertex)
     * @param graph the graph that needs to be calculate
     * @return the maximum paths, which is the diameter
     */
    private int findTheLongestShortestPath(Graph graph){
        Dijkstra findShortestPath = new Dijkstra(graph);
        // call Dijkstra from all pairs of vertices
        int tempDiameter;
        int diameterOfGraph = -1;
        for (Vertex start : Vertices) {
            findShortestPath.executeDijkstra(start);
            for (Vertex destination : Vertices) {
                if (start != destination) {
                    ArrayList<Vertex> paths = findShortestPath.getPathFromAVertexToAnother(destination);
                    tempDiameter = findShortestPath.returnTotalWeight(destination);

                    // find a longer path weight and store the start and end vertex of that path
                    if (diameterOfGraph < tempDiameter){
                        diameterOfGraph = tempDiameter;
                        storeDiameter[0] = String.valueOf(paths.get(0));
                        storeDiameter[1] = String.valueOf(paths.get(paths.size()-1));
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
    private void outputAShortestPath(ArrayList<Vertex> path){
        for (Vertex vertex : path) {
            System.out.print(vertex + " -> ");
        }
        System.out.println("Done");
    }

    /**
     * adding an edge into the Edges List
     * @param laneId ID of the edge
     * @param sourceLocationNumber start vertex
     * @param destinationLocationNumber end vertex
     * @param weight the weight of the edge
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
        System.out.println("There are " + edgeNum + " edges. ");
        System.out.println("The edge IDS are: ");

        printAll(listEdgeIDs);
    }

    /**
     * print out all Vertices'ID
     * @param listvertexIDs store all vertexIDs
     */
    private void printVertexIDs(ArrayList<String> listvertexIDs){
        System.out.println("There are " + vertexNum + " vertices. ");
        System.out.println("The vertex IDS are: ");

        printAll(listvertexIDs);
    }

    /**
     * print all things that others need as an ArrayList
     * @param list list of anything
     */
    private void printAll(ArrayList<String> list){
        System.out.println(list);
    }

    /**
     * print out all pair of source and target vertices in an edge
     * @param listSourceTarget list of all edges
     */
    private void printEdgeOut(List<MakePair<String, String>> listSourceTarget){
        System.out.println("The source and target vertex of every edges: ");
        int i = 1;
        for (MakePair<String, String> element : listSourceTarget) {
            System.out.print(element.getL() + "->" + element.getR() + ":" + storeWeigh[Integer.parseInt(element.getL())][Integer.parseInt(element.getR())] + " ; ");
            if (i % 10 == 0 && i != 0) {
                System.out.println();
            }
            i++;
        }
        System.out.println();
    }

    /**
     * This function will output all the properties of the graph into a new file
     * @param fileName file .graphml to output
     */
    private void outputFile(String fileName) throws IOException {
//        long startTime = System.currentTimeMillis();

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new
                    FileOutputStream(fileName), StandardCharsets.US_ASCII), 512);
            out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\"\n");
            out.write("         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            out.write("         xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns\n");
            out.write("         http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd\">\n");
            out.write("<!-- Created by igraph -->\n");
            out.write("  <key id=\"v_id\" for=\"vertex\" attr.name=\"id\" attr.type=\"double\"/>\n");
            out.write("  <key id=\"e_id\" for=\"edge\" attr.name=\"id\" attr.type=\"double\"/>\n");
            out.write("  <key id=\"e_weight\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
            out.write("  <key id=\"total_vertex\" for=\"vertex\" attr.name=\"weight\" attr.type=\"double\"/>\n");                    out.write("  <key id=\"total\" for=\"vertex\" attr.name=\"weight\" attr.type=\"double\"/>\n");
            out.write("  <key id=\"total_edge\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
            out.write("  <key id=\"connected\" for=\"edge\" attr.name=\"weight\" attr.type=\"boolean\"/>\n");
            out.write("  <key id=\"diameter\" for=\"edge\" attr.name=\"weight\" attr.type=\"double\"/>\n");
            out.write("  <graph id=\"G\" edgedefault=\"undirected\">\n");
            out.write("    <vertex id=\"total\">\n");
            out.write("      <data key=\"total_vertex\">" + vertexNum + "</data>\n");
            out.write("      <data key=\"total_vertex\">" + vertexIDs + "</data>\n");
            out.write("    </vertex>\n");

            for(int startVertex = 0; startVertex < vertexNum; startVertex++){
                out.write("    <vertex id=\"n" + startVertex + "\">\n");
                out.write("      <data key=\"v_id\">" + startVertex + "</data>\n");

                double betweenness = calculateBetweennessCentrality(Vertices.get(startVertex));

                for (int endVertex = 0; endVertex < vertexNum;endVertex++){
                    if (startVertex != endVertex) {
                        out.write("      <dijkstra to=\"n" + endVertex + "\">" + aPathBetweenTwoVertices[startVertex][endVertex] + " = " + allDijkstra[startVertex][endVertex] + "</dijkstra>\n");
                    }else{
                        out.write("      <dijkstra to=\"n" + endVertex + "\">[ " + aPathBetweenTwoVertices[startVertex][endVertex] + " ] = " + allDijkstra[startVertex][endVertex] + "</dijkstra>\n");
                    }
                }
                if (startVertex == vertexNum/2) {
                    LOG.log(Level.INFO, "Half way now!");
                }
                out.write("      <betweenness of=\"n" + startVertex + "\">" + betweenness + "</betweenness>\n");
                out.write("    </vertex>\n");
            }
            out.write("    <edge source=\"all\" target=\"all\">\n");
            out.write("      <data key=\"total_edge\">" + edgeNum + "</data>\n");
            out.write("      <data key=\"total_edge\">" + edgeIDs + "</data>\n");
            out.write("    </edge>\n");

            for (int edgeNumber = 0; edgeNumber < edgeNum;edgeNumber++) {
                out.write("    <edge source=\"n" + edges.get(edgeNumber).getL().trim() + "\" target=\"n" + edges.get(edgeNumber).getR().trim() + "\">\n");
                out.write("      <data key=\"e_id\">"+ edgeIDs.get(edgeNumber).trim() + "</data>\n");
                out.write("      <data key=\"e_weight\">" + edgeWeights.get(edgeNumber).trim() + "</data>\n");
                out.write("    </edge>\n");
            }
            out.write("    <edge source=\"all\" target=\"all\">\n");
            out.write("      <data key=\"connected\">" + isGraphConnected() + "</data>\n");
            out.write("    </edge>\n");
            out.write("    <edge source=\"all\" target=\"all\">\n");
            out.write("      <data key=\"diameter\">" + diameterOfGraph +"</data>\n");
            out.write("    </edge>\n");
            out.write("  </graph>\n");
            out.write("</graphml>\n");
//        out.flush();
            out.close();
        } catch (FileNotFoundException e){
            LOG.log(Level.WARNING,"File not found");
        }

        LOG.log(Level.INFO, "File creation is finished, you can now open your file to check the result.");
        // see the actual time of file creation
//        long endTime = System.currentTimeMillis();
//        long elapsed_time = endTime - startTime;
//        LOG.log(Level.WARNING, "Running time: " + elapsed_time + " ms");

    }

    /**
     * This function is to create a Logger for further use
     */
    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}



