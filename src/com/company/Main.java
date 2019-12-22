package com.company;

import static com.company.Graph.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class Main extends Thread{

    private static List<Vertex> Nodes = new ArrayList<>();
    private static List<Edge> Edges = new ArrayList<>();

    // create 4 character for each edges, node , weight in the file to compare
    final static String edgeId = "e_id";
    final static String nodeID = "v_id";
    final static String edgeWeight = "e_we";
    final static String sourceOfEdge = "sour";

    // calculate number of edge, vertex and edge weight
    static int edgeNum = 0, vertexNum = 0, edgeWeightNum = 0;

    // create ArrayList to contain the value that is read from the input file
    static ArrayList<MakePair<String,String>> edges = new ArrayList<>();
    static ArrayList<String> nodeIDs = new ArrayList<>();
    static ArrayList<String> edgeIDs = new ArrayList<>();
    static ArrayList<String> edgeWeights = new ArrayList<>();

    // store the weight of all edges
    static int[][] storeWeigh = new int[10000][10000];
    // charArray to contain all char in a file
    static char[] allChar = new char[1000000];
    // change option from graph properties to calculate dijkstra
    static boolean connect_or_dijkstra = false;

    // calculate all paths that passed through a node
    static float numberPathsPass = 0;
    // calculate all weight of paths between 2 nodes
    static int weightOfPath = 0;
    // calculate all shortest path from all paths that passed through a node
    static float numberOfShortestPathPass = 0;
    // calculate all shortest path from all paths between 2 nodes
    static float numberOfShortestPath = 0;
    // calculate all paths between 2 nodes
    static float numberOfPaths = 0;
    // use to find the smallest weight of a path
    static int minn = 2000000; // careful with large weight

    // use to store all paths between 2 nodes that passed through 1 node using String
    static String[] allPathsPass = new String[10000];
    // use to store all paths between 2 node
    static String[] allPaths = new String[10000];

    static int temp_weight;
    static int temp_weight_2;

    public static void main(String[] args) throws IOException {
        Graph graph_first = new Graph(Nodes, Edges);
        Charset encoding = Charset.defaultCharset();
        LOG.info("Reading the .graphml file");
        // open and read a new file
        File file = new File(args[0]);
        handleFile(file, encoding);
        LOG.info("Building graph begins ");
        buildGraph();

        // checking how many arguments are pasted in order to choose the right operation
        if (args.length == 1) {
            connect_or_dijkstra =  true; // to choose whether to print out properties or just the Dijkstra of 2 nodes

            // true for the properties
            LOG.info("The properties of the graph are: ");

            // print out the nodeIDs
            printNodeIDs(nodeIDs);
            // print out the edgeIDS
            printEdgeIDs(edgeIDs);
            // print out the edges source and target
            printMapOut(edges);

            // use Dijkstra to check the connectivity and the diameter of the graph
            DijkstraCall(Nodes.get(1), Nodes.get(2));

        } else if (args.length == 3){
            if (args[1].equals("-b")) {
                int passVertex = Integer.parseInt(args[2]);
                // calculate betweenness centrality measure for a specific node
                CalculateBetweennessCentrality(Nodes.get(passVertex));
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

                LOG.info("Deletion old file successful.");
                LOG.info("Output file has been created");
                writeFile(args[2]);
            }
        }
        else {
            // false to print out the dijkstra of 2 nodes
            if (args[1].equals("-s")) {
                int sourceNode,targetNode;

                sourceNode = Integer.parseInt(args[2]);
                targetNode = Integer.parseInt(args[3]);
                // call the Dijkstra algorithms
                DijkstraCall(Nodes.get(sourceNode), Nodes.get(targetNode));
            }
        }
//        printCharOutArray(allChar);
    }

    private static void buildGraph() {
        Nodes = new ArrayList<>();
        Edges = new ArrayList<>();

//        System.out.println(vertexNum + "*");
        for (int i = 0; i < vertexNum; i++) {
            // add the vertex ID and name, since it is the same so we just add 2 identical nodeIDs
            Vertex location = new Vertex(nodeIDs.get(i), nodeIDs.get(i));
            Nodes.add(location);
//            System.out.print(Nodes.get(i) + " ");
        }
//        System.out.println(edgeNum + "*");
        for(int i = 0; i < edgeNum;i++) {
            // add a direction between 2 nodes
            addEdge(edgeIDs.get(i), edges.get(i).getL(), edges.get(i).getR(), edgeWeights.get(i));
            // add this to make an undirected graph
            addEdge(edgeIDs.get(i), edges.get(i).getR(), edges.get(i).getL(), edgeWeights.get(i));
//            System.out.println(edgeIDs.get(i) + ": " + edges.get(i).getL() + "->" + edges.get(i).getR() + " " + edgeWeights.get(i));
            storeWeigh[Integer.parseInt(edges.get(i).getL())][Integer.parseInt(edges.get(i).getR())] = Integer.parseInt(edgeWeights.get(i).trim());
            storeWeigh[Integer.parseInt(edges.get(i).getR())][Integer.parseInt(edges.get(i).getL())] = Integer.parseInt(edgeWeights.get(i).trim());
        }
    }

    private static void CalculateBetweennessCentrality(Vertex pass){
        Graph graph = new Graph(Nodes, Edges);
        // calculate betweenness centrality
        float betweenness = 0;

        // call Dijkstra from all pairs of vertices
        for (Vertex start : Nodes) {
            for (Vertex destination : Nodes) {
                // find paths from node that is not the same and also not start or end with the pass node
                if (start != destination && start != pass && destination != pass) {
                    // these variable are reset after calculate a pair of start and destination node
                    numberPathsPass = 0;
                    numberOfPaths = 0;

                    numberOfShortestPathPass = 0;
                    numberOfShortestPath = 0;
                    minn = 1000000;

                    LOG.info("Find and counting all shortest paths between node " + start.toString().trim() + " and " + destination.toString().trim());
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

    public static void print(Graph graph, Vertex start, Vertex end, String path, boolean[] visited, Vertex pass){
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
    public static void checkingIfAPathPassIsMinimal(int weightOfPath){
        if (weightOfPath == minn){
            numberOfShortestPathPass++;
        }
    }

    public static void checkingIfAPathIsMinimal(int weightOfPath){
        if (weightOfPath == minn){
            numberOfShortestPath++;
        }
    }

    public static void findingMinimalLength(int weightOfPath){
        if (weightOfPath <= minn){
            // finding the minimal length from start to node
            minn = weightOfPath;
        }
    }

    public static void storingAllPathsPassThroughNode(String complete){
        allPathsPass[(int) numberPathsPass] = complete;
        numberPathsPass++;
    }

    public static void storingAllPaths(String complete){
        allPaths[(int)numberOfPaths] = complete;
        numberOfPaths++;
    }

    public static boolean checkIfTwoCharAreDashes(char first, char second){
        return first != '-' && second != '-';
    }
    public static void countShortestPath(){
        for (int j = 0; j < numberOfPaths;j++) { //number of shortest paths between 2 nodes
            temp_weight_2 = 0;
            for (int a  = 0; a < allPaths[j].length()-2;a++){
                char first = allPaths[j].charAt(a);
                char second = allPaths[j].charAt(a+2);

                if (checkIfTwoCharAreDashes(first,second)) {
                    temp_weight_2 += storeWeigh[Integer.parseInt(String.valueOf(first))][Integer.parseInt(String.valueOf(second))];
                }
            }
//            System.out.println(temp_weight + " temp");
            checkingIfAPathIsMinimal(temp_weight_2);
        }
//        System.out.println(minn + " minn");
    }

    public static void countShortestPathPassThroughNodes(){
        for (int i = 0; i < numberPathsPass;i++) { //number of shortestes paths that passed through 1 vertex
            temp_weight = 0;
            for (int a  = 0; a < allPathsPass[i].length()-2;a++){
                char first = allPathsPass[i].charAt(a);
                char second = allPathsPass[i].charAt(a+2);

                if (checkIfTwoCharAreDashes(first,second)) {
                    temp_weight += storeWeigh[Integer.parseInt(String.valueOf(first))][Integer.parseInt(String.valueOf(second))];
                }
            }
//            System.out.println(temp_weight + " temp");
            checkingIfAPathPassIsMinimal(temp_weight);
        }
    }

    public static void printAllPaths(Graph graph, Vertex start, Vertex end, Vertex pass){
        boolean[] visited = new boolean[100000];
        visited[Integer.parseInt(start.toString().trim())] = true;
        // starting calculate all possible path from start to end
        print(graph, start, end, " ", visited, pass);
    }

    private static void DijkstraCall(Vertex sourceNode, Vertex targetNode){
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

        if (!connect_or_dijkstra) {
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

    public static int findingTheLongestShortestPath(Graph graph){
        int maxPath = -1;
        Dijkstra findShortestPath = new Dijkstra(graph);
        // call Dijkstra from all pairs of vertices
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

    public static void outputAShortestPath(LinkedList<Vertex> path){
        for (Vertex vertex : path) {
            LOG.fine(vertex + " -> ");
        }
        LOG.fine("Done");
    }

    private static void addEdge(String laneId, String sourceLocNo, String destLocNo, String duration) {
        // add a new edge into Edges list
        Edge lane = new Edge(laneId,Nodes.get(Integer.parseInt(sourceLocNo)), Nodes.get(Integer.parseInt(destLocNo)), duration );
        Edges.add(lane);
        try {
            adjacencyList[Integer.parseInt(sourceLocNo)].add(lane);
        } catch (NullPointerException e){
            LOG.warning("Null value");
        }
    }

    static void handleFile(File file, Charset encoding) throws IOException {
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

    private static void handleCharacters(Reader reader) throws IOException {
        // error handling if file does not have any character
        if (reader == null){
            LOG.warning("File does not have any characters. Type something");
        }
        int count = 0;
        assert reader != null;
        int r = reader.read();
        while (r  != -1) {
            char ch = (char)r;
            allChar[count] = ch;

            count++;
            r = reader.read();
        }

        // change charArray to String for comparison
        String allCharString = new String(allChar);

        // create the startCheck because in the file there are some e_id, v_id that does not contain any value
        boolean startCheck = false;

        LOG.info("Start reading the file");
        for(int i = 0; i < count-30;i++) {
            // take 4 numbers at a time to compare
            String fourInATime = allCharString.substring(i, i + 4);

            // find a number between "> and "<" by adding more space from 4 to 30
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

    private static void writeFile(String fileName) throws IOException{
        String str = "world";
        PrintWriter writer =  new PrintWriter(new FileWriter(fileName, true));
        writer.print(str);
        writer.close();
    }

    static void printEdgeIDs(ArrayList<String> as){
        LOG.info("There are " + edgeNum + " edges. ");
        LOG.fine("The edge IDS are: ");

        printAll(as);
    }

    static void printNodeIDs(ArrayList<String> as){
        LOG.info("There are " + vertexNum + " nodes. ");
        LOG.fine("The vertex IDS are: ");

        printAll(as);
    }

    static void printAll(ArrayList<String> as){
        for (String a : as) {
            LOG.fine(a + " ");
        }
    }

    static void printMapOut(List<MakePair<String, String>> hs){
        LOG.fine("The source and target node of every edges: ");

        for (MakePair<String, String> a : hs) {
            LOG.fine(a.getL() + "->" + a.getR() + " ");
        }
    }

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
}
