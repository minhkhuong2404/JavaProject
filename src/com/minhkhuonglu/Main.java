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
 * @version 1.0
 */

public class Main{
    /**
     * The integerValues here are used to store number of Edges, Vertices and Weight of Edges
     * all set to 0 at first
     */
    static int edgeNum = 0, vertexNum = 0, edgeWeightNum = 0;

    /**
     * The ArrayLists edges stores the start and the destination node of an edge
     * The ArrayLists nodeIDs stores all the ID of nodes
     * The ArrayLists edgeIDs stores all the ID of edges
     * The ArrayLists edgeWeights stores only the weight of edges
     */
    private static ArrayList<MakePair<String,String>> edges = new ArrayList<>();
    private static ArrayList<String> nodeIDs = new ArrayList<>();
    private static ArrayList<String> edgeIDs = new ArrayList<>();
    private static ArrayList<String> edgeWeights = new ArrayList<>();

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
     * whether to use Dijkstra to calculate shortest path between 2 nodes
     * or to check the connectivity of the graph
     * false is for calculate Dijkstra
     */
    static boolean onlyConnected = false;

    /**
     * The float value numberPathsPass for counting number of paths
     * between 2 nodes that pass through a vertex (node)
     */
    static float numberPathsPass = 0;
    /**
     * The float value numberOfPaths for counting number of paths
     * between 2 nodes ONLY
     */
    static float numberOfPaths = 0;
    /**
     * The integer value weightOfPath for calculating the weight of a path between 2 nodes
     * this value will be reset after calculating 1 path
     * @see com.minhkhuonglu.Main#print
     */
    static int weightOfPath = 0;
    /**
     * The float value calculates all shortest path from all paths between 2 nodes that passed through a node
     * this value will be also be reset after calculating a pair of node
     */
    static float numberOfShortestPathPass = 0;
    /**
     * The float value calculates all shortest path from all paths between 2 nodes ONLY
     * this value will be also be reset after calculating a pair of node
     */
    static float numberOfShortestPath = 0;
    /**
     * This minn is used to calculate the smallest weight of the path between 2 nodes
     * this is also used to find the shortest paths for the 2 above float value
     * @see com.minhkhuonglu.Main#checkingIfAPathIsMinimal
     * @see com.minhkhuonglu.Main#checkingIfAPathPassIsMinimal
     */
    static int minn = 2000000; // careful with large weight

    /**
     * This String array allPathsPass stores all paths between 2 nodes that passed through 1 node
     * which may not be the shortest one
     */
    static String[] allPathsPass = new String[10000];
    /**
     * This String array allPathsPass stores all paths between 2 nodes ONLY
     * which may not be the shortest one
     */
    static String[] allPaths = new String[10000];

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
     * The "Nodes" List is used to contain all vertices in the Graph
     * The "Edges" List is used to contain all edges in the Graph
     * @see com.minhkhuonglu.Graph
     */
    public static List<Vertex> Nodes = new ArrayList<>();
    public static List<Edge> Edges = new ArrayList<>();

    /**
     * first check the extension of file whether it is .graphml or not
     * The main method used to distinguish the input argument that the user types in
     * to choose which needs to be print out or doing
     * divide the number of arguments are used when calling .jar file 1,3 or 4
     * if length is 1, it will print out properties of the graph
     * if length is 3, it will calculate the between centrality measure of a node if the second args is -b
     * otherwise, it will output the whole result into a new .graphml file
     * if length is 4, it will find the shortest path between 2 nodes
     * @param args which are all arguments when calling the .jar file
     * @throws IOException if stream to file cannot be written
     */
    public static void main(String[] args) throws IOException, IncorrectFileExtensionException, InterruptedException {
        Main graph = new Main();
        ParallelThread countUp = new ParallelThread(1,3,true);
        ParallelThread countDown = new ParallelThread(10,8,true);
        ParallelThread countAgain = new ParallelThread(1,5,true);
        ParallelThread startDoCalculation = new ParallelThread(1,1, false);

        Thread cUp = new Thread(countUp);
        Thread cDown = new Thread(countDown);
        Thread cAgain = new Thread(countAgain);
        Thread startCal = new Thread(startDoCalculation);

        LOG.info("Preparing to build and display the properties of the graph: ");
        cUp.start();
        cDown.start();
        cUp.join();
        cDown.join();
        LOG.info("System crashed, trying to prepare again.");

        cAgain.start();
        cAgain.join();
        LOG.info("Done preparing! ");
        /*
        ==================================================================
         */
        try {
            graph.isCorrectFileExtension(args[0]);
        } catch (IllegalArgumentException err){
            LOG.info("Wrong file extension " + err);
        }

        graph.readFileAndBuildGraph(args[0]);

        startCal.start();
        // checking how many arguments are pasted in order to choose the right operation
        if (args.length == 1) {
            onlyConnected =  true; // to choose whether to print out properties or just the Dijkstra of 2 nodes

            // true for the properties
            LOG.info("The properties of the graph are: ");

            // print out the nodeIDs
            graph.printNodeIDs(nodeIDs);
            // print out the edgeIDS
            graph.printEdgeIDs(edgeIDs);
            // print out the edges source and target
            graph.printMapOut(edges);

            // use Dijkstra to check the connectivity and the diameter of the graph
            graph.DijkstraCall(Nodes.get(1), Nodes.get(2));

        } else if (args.length == 3){
            if (args[1].equals("-b")) {
                int passVertex = Integer.parseInt(args[2]);
                // calculate betweenness centrality measure for a specific node
                graph.CalculateBetweennessCentrality(Nodes.get(passVertex));
            }
            else if (args[1].equals("-a")){
                try{
                    Files.deleteIfExists(Paths.get("/outputfile.graphml"));
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

                LOG.info("Delete old file successful.");
                LOG.info("Output file has been created");
                graph.writeFile(args[2]);
            }
        }
        else {
            if (args[1].equals("-s")) {
                int sourceNode,targetNode;

                sourceNode = Integer.parseInt(args[2]);
                targetNode = Integer.parseInt(args[3]);
                // call the Dijkstra algorithms
                graph.DijkstraCall(Nodes.get(sourceNode), Nodes.get(targetNode));
            }
        }

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
            LOG.info("Correct file extension");
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
        LOG.info("Building graph begins ");
        buildGraph();
    }

    /**
     * This function used to build the graph for further calculation
     * which contains vertices and edges
     * this also creates new linked list to store the weight of the start vertex
     * to calculate all paths between 2 nodes
     */
    private void buildGraph() {
        Graph graph_first = new Graph(Nodes, Edges); // don't DELETE this line. Error
        Nodes = new ArrayList<>();
        Edges = new ArrayList<>();

        graph_first.creatingNewLinkedList();

        buildVertex();
        buildEdge();
    }

    /**
     * adding nodes by adding its ID and name,
     * which are the same according to the .graphml file, from the nodeIDS ArrayList.
     * ID and name are the same so we just add 2 identical nodeIDs
     */
    private void buildVertex(){
        for (int i = 0; i < vertexNum; i++) {
            Vertex location = new Vertex(nodeIDs.get(i), nodeIDs.get(i));
            Nodes.add(location);
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
     * @see com.minhkhuonglu.Main#countShortestPath()
     * @see com.minhkhuonglu.Main#countShortestPathPassThroughNodes()
     */
    private void buildEdge(){
        for(int i = 0; i < edgeNum;i++) {
            addEdge(edgeIDs.get(i), edges.get(i).getL(), edges.get(i).getR(), edgeWeights.get(i));
            addEdge(edgeIDs.get(i), edges.get(i).getR(), edges.get(i).getL(), edgeWeights.get(i));
//            System.out.println(edgeIDs.get(i) + ": " + edges.get(i).getL() + "->" + edges.get(i).getR() + " " + edgeWeights.get(i));
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
            LOG.warning("File does not exist. Please try again");
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
            LOG.warning("File does not have any characters. Type something");
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

        LOG.info("Start reading the .graphml file");
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
     * This function will also use 5 more variable defined above to reset its value to 0
     * in order to calculate other pair of vertices
     *
     * It will calculate number of shortest paths between 2 nodes
     * also shortest paths between 2 nodes that go through the node needs to be passed
     *
     * @param pass which is the node needs to be passed through
     * @see Main#countShortestPathPassThroughNodes()
     * @see Main#countShortestPath()
     */
    private void CalculateBetweennessCentrality(Vertex pass){
        Graph graph = new Graph(Nodes, Edges);
        // calculate betweenness centrality
        float betweenness = 0;
        for(int i = 0 ;i < vertexNum; i++) {
            for( int j  = 0; j < vertexNum; j++) {
                Vertex start = Nodes.get(i);
                Vertex destination = Nodes.get(j);
                // find paths from node that is not the same and also not start or end with the pass node
                if (start != destination && start != pass && destination != pass) {
                    // these variable are reset after calculate a pair of start and destination node
                    numberPathsPass = 0;
                    numberOfPaths = 0;

                    numberOfShortestPathPass = 0;
                    numberOfShortestPath = 0;
                    minn = 1000000;

                    LOG.fine("Find and counting all shortest paths between node " + start.toString().trim() + " and " + destination.toString().trim());
                    printAllPaths(graph,start, destination, pass);

                    countShortestPathPassThroughNodes();
                    countShortestPath();

                    LOG.fine("There are " + (int) numberOfShortestPathPass + " shortest path(s) that passed through " + pass.toString().trim() + " with the length of " + minn);
                    LOG.fine("And there are " + (int) numberOfShortestPath + " shortest path(s) between 2 nodes");
                    LOG.fine(numberOfShortestPathPass + " / " + numberOfShortestPath + " = " + numberOfShortestPathPass/numberOfShortestPath +'\n');
                    betweenness += numberOfShortestPathPass/numberOfShortestPath;
                }
            }
        }

        LOG.info("Betweenness centrality measure for node " + pass + " is: " + betweenness);
    }

    /**
     * This function first creating a newPath separated by a dash between 2 vertices
     * make the first index of the visited array true then start
     *
     * go through all edges and check if its end vertices has been visited also check
     * if that end's vertex is the same as the end vertex in the parameter
     * if no, keeping calling the print() function recursively
     * otherwise, check the complete path and calculate the weight of each edges to find the weight of that path
     * In the meantime, saving all paths between 2 nodes and all paths between 2 nodes that goes through the vertex pass
     * in to 2 different array list.
     *
     * And also finding the minimal weight to find the shortest path later.
     *
     * @param graph passing the graph needs to be calculate
     * @param start the start vertex
     * @param end the end (destination) vertex
     * @param path saving the path from start to end vertex
     * @param visited checked if a vertex has been visited by using a boolean array
     * @param pass the vertex needs to check whether it is passed or not
     * @see Main#storingAllPaths(String)
     * @see Main#storingAllPathsPassThroughNode(String)
     * @see Main#findingMinimalLength(int)
     */
    private void print(Graph graph, Vertex start, Vertex end, String path, boolean[] visited, Vertex pass){
        String newPath = path.trim() + "-" + start.toString().trim();
        int indexOfStart = Integer.parseInt(start.toString().trim());

        visited[indexOfStart] = true;
        LinkedList<Edge> list = Graph.adjacencyList[indexOfStart];

        for (Edge edge : list) {

            if (edge.getDestination() != end && !visited[Integer.parseInt(edge.getDestination().toString().trim())]) {
                visited[Integer.parseInt(edge.getDestination().toString().trim())] = true;
                print(graph, edge.getDestination(), end, newPath, visited, pass);
            } else if (edge.getDestination() == end) {

                String complete = newPath.trim() + "-" + edge.getDestination().toString().trim();
                for (int a  = 0; a < complete.length()-2;a++){
                    char first = complete.charAt(a);
                    char second = complete.charAt(a+2);
                    if (checkIfTwoCharAreDashes(first,second)) {
                        weightOfPath += storeWeigh[Integer.parseInt(String.valueOf(first))][Integer.parseInt(String.valueOf(second))];
                    }
                }
//                System.out.println(newPath.trim() + "-" + edge.getDestination().toString().trim() + " weight " + weightOfPath );
                if (complete.contains(pass.toString().trim())){
                    // store paths that pass through a node
                    storingAllPathsPassThroughNode(complete);
                }
                // find minimal length of all paths between 2 nodes
                findingMinimalLength(weightOfPath);

                // store all paths
                storingAllPaths(complete);
                // reset the calculated weight from start to destination for the next combination
                weightOfPath = 0;
            }
        }
        //remove from path
        visited[indexOfStart] = false;
    }

    /**
     * calling the function print() for the first time to print out all paths
     * from the start vertex to the end vertex
     *
     * @param graph the graph needs to be calculated
     * @param start the start vertex
     * @param end the end vertex
     * @param pass the vertex needs to be passed
     */
    private void printAllPaths(Graph graph, Vertex start, Vertex end, Vertex pass){
        boolean[] visited = new boolean[100000];
        visited[Integer.parseInt(start.toString().trim())] = true;
        // starting calculate all possible path from start to end
        print(graph, start, end, " ", visited, pass);
    }

    /**
     * compare if the weight of the current path between 2 nodes
     * that passes through a node is the shortest path
     * @param weightOfPath is the weight of the current checked path
     */
    private void checkingIfAPathPassIsMinimal(int weightOfPath){
        if (weightOfPath == minn){
            numberOfShortestPathPass++;
        }
    }

    /**
     * compare if the weight of the current path between 2 nodes ONLY
     * is the shortest path
     * @param weightOfPath is the weight of the current checked path
     */
    private void checkingIfAPathIsMinimal(int weightOfPath){
        if (weightOfPath == minn){
            numberOfShortestPath++;
        }
    }

    /**
     * This function is used to check if the weight of the path is the smallest one or not
     * @param weightOfPath is the weight of the current checked path
     */
    private void findingMinimalLength(int weightOfPath){
        if (weightOfPath <= minn){
            // finding the minimal length from start to node
            minn = weightOfPath;
        }
    }

    /**
     * add the path that have a vertex that needs to go pass and store into an array
     * @param complete the String that store the path between 2 nodes
     */
    private void storingAllPathsPassThroughNode(String complete){
        allPathsPass[(int) numberPathsPass] = complete;
        numberPathsPass++;
    }

    /**
     * add the path between 2 nodes and store into an array
     * @param complete the String that store the path between 2 nodes
     */
    private void storingAllPaths(String complete){
        allPaths[(int)numberOfPaths] = complete;
        numberOfPaths++;
    }

    /**
     * checking if 2 char are dashes
     * @param first the first char needs to be checked
     * @param second the second char needs to be checked
     * @return true if 2 char are not dashes
     */
    private boolean checkIfTwoCharAreDashes(char first, char second){
        return first != '-' && second != '-';
    }

    /**
     * Because the path has the type -1-2-3-4
     * we need to choose 2 char at a time in order to calculate its weight
     * then store all of them into the shortestPath integer value to check if that path is the shortest
     *
     * this function is to check for all the paths between 2 nodes
     * the following function has a kind of same function
     * @see com.minhkhuonglu.Main#countShortestPathPassThroughNodes()
     */
    private void countShortestPath(){
        int shortestPath;
        for (int j = 0; j < numberOfPaths;j++) { //number of shortest paths between 2 nodes
            shortestPath = 0;
            for (int a  = 0; a < allPaths[j].length()-2;a++){
                char first = allPaths[j].charAt(a);
                char second = allPaths[j].charAt(a+2);

                if (checkIfTwoCharAreDashes(first,second)) {
                    shortestPath += storeWeigh[Integer.parseInt(String.valueOf(first))][Integer.parseInt(String.valueOf(second))];
                }
            }
            checkingIfAPathIsMinimal(shortestPath);
        }
    }

    /**
     * this function is apply to check for all the paths between 2 nodes that pass through a vertex
     * also check if that path is the smallest one
     */
    private void countShortestPathPassThroughNodes(){
        int shortestPathPassThroughNodes;
        for (int i = 0; i < numberPathsPass;i++) { //number of shortest paths that passed through 1 vertex
            shortestPathPassThroughNodes = 0;
            for (int a  = 0; a < allPathsPass[i].length()-2;a++){
                char first = allPathsPass[i].charAt(a);
                char second = allPathsPass[i].charAt(a+2);

                if (checkIfTwoCharAreDashes(first,second)) {
                    shortestPathPassThroughNodes += storeWeigh[Integer.parseInt(String.valueOf(first))][Integer.parseInt(String.valueOf(second))];
                }
            }
            checkingIfAPathPassIsMinimal(shortestPathPassThroughNodes);
        }
    }

    /**
     * This function creates a linked list in order to store all nodes that created the shortest path
     *
     * as dijkstra is used to check connectivity and calculate the diameter also
     * we need to have a boolean value onlyConnected to choose which action should be performed
     * if !onlyConnected we just calculate the shortest path using Dijkstra as normal
     * otherwise, we will use it to calculate the connectivity and the diameter
     *
     * @param sourceNode the start vertex
     * @param targetNode the end vertex
     * @throws java.lang.NullPointerException if the graph is unconnected
     */
    private void DijkstraCall(Vertex sourceNode, Vertex targetNode){
        // Create a graph from the Nodes and Edges list above
        Graph graph = new Graph(Nodes, Edges);
        Dijkstra dijkstra = new Dijkstra(graph);

        // import the source and target from the user input
        LOG.info("Calling dijkstra");
        dijkstra.executeDijkstra(sourceNode);
        LinkedList<Vertex> path = dijkstra.getPath(targetNode);

        // check if the path doesn't exist
        assertNotNull(path);
        assertTrue(path.size() > 0); //  if the size is large than 0

        if (!onlyConnected) {
            // print out the shortest path
            LOG.info("Total weight of the path is: " + dijkstra.returnTotal_Weight(targetNode));
            LOG.info("Shortest path from node " + sourceNode + " to node " + targetNode + " is: ");
            outputAShortestPath(path);
        }
        else {
            if (dijkstra.isConnected()) {
                LOG.info("The graph is connected");
            }
            try {
                LOG.info("The diameter of the graph is: " + findingTheLongestShortestPath(graph));
            }
            catch (java.lang.NullPointerException ex ){
                LOG.info("The graph is not connected");
                LOG.info("The diameter of the graph is: oo (infinity)");
            }
        }
    }

    /**
     * This function calculates all shortest paths between a pair of vertices
     * to find the shortest path that has the longest path
     * which means -1-2-3-4 < -1-2-3-4-5-6-7
     *
     * @param graph the graph that needs to be calculate
     * @return the maximum paths, which is the diameter
     */
    private int findingTheLongestShortestPath(Graph graph){
        Dijkstra findShortestPath = new Dijkstra(graph);
        // call Dijkstra from all pairs of vertices
        int maxPath = -1;
        for (Vertex start : Nodes) {
            for (Vertex destination : Nodes) {
                if (start != destination) {
                    findShortestPath.executeDijkstra(start);
                    LinkedList<Vertex> paths = findShortestPath.getPath(destination);
                    // find a longer path size
                    if (paths.size() > maxPath) {
                        maxPath = paths.size();
                        outputAShortestPath(paths);
                    }
                }
            }
        }
        return maxPath;
    }

    /**
     * print out the path more readable
     * @param path linked list to store all vertices goes through
     */
    private void outputAShortestPath(LinkedList<Vertex> path){
        for (Vertex vertex : path) {
            LOG.info(vertex + " -> ");
        }
        LOG.info("Done");
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
    private void addEdge(String laneId, String sourceLocNo, String destLocNo, String weight) {
        Edge lane = new Edge(laneId, Nodes.get(Integer.parseInt(sourceLocNo)), Nodes.get(Integer.parseInt(destLocNo)), weight );
        Edges.add(lane);
        try {
            adjacencyList[Integer.parseInt(sourceLocNo)].add(lane);
        } catch (NullPointerException e){
            LOG.warning("Null value");
        }
    }

    /**
     * This function uses to write into a .graphml file
     * all the result and properties of the graph
     *
     * This function hasn't been done yet, because I didn't find a way to write into the .graphml file
     * @param fileName is the file in the args[0]
     * @throws IOException if stream of a file cannot be written
     */
    private void writeFile(String fileName) throws IOException{
        String str = "world";
        PrintWriter writer =  new PrintWriter(new FileWriter(fileName, true));
        writer.print(str);
        writer.close();
    }

    /**
     * print out all edges'ID
     * @param listEdgeIDs store all edgeIDs
     */
    private void printEdgeIDs(ArrayList<String> listEdgeIDs){
        LOG.info("There are " + edgeNum + " edges. ");
        LOG.fine("The edge IDS are: ");

        printAll(listEdgeIDs);
    }

    /**
     * print out all nodes'ID
     * @param listNodeIDs store all nodeIDs
     */
    private void printNodeIDs(ArrayList<String> listNodeIDs){
        LOG.info("There are " + vertexNum + " nodes. ");
        LOG.fine("The vertex IDS are: ");

        printAll(listNodeIDs);
    }

    /**
     * print all things that others need as an ArrayList
     * @param list list of anything
     */
    private void printAll(ArrayList<String> list){
        for (String a : list) {
            LOG.fine(a + " ");
        }
    }

    /**
     * print out all pair of source and target vertices in an edge
     * @param listSourceTarget list of all edges
     */
    private void printMapOut(List<MakePair<String, String>> listSourceTarget){
        LOG.fine("The source and target node of every edges: ");

        for (MakePair<String, String> a : listSourceTarget) {
            LOG.fine(a.getL() + "->" + a.getR() + " ");
        }
    }

    /**
     * This function is to create a Logger for further use
     */
    public static final Logger LOG = Logger.getLogger(Main.class.getName());
}
