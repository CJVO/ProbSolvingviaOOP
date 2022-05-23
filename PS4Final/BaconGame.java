//import org.codehaus.groovy.tools.shell.IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * @author Chukwuka, CS 10, Winter 2022, for the purpose of completing problem set 4
 * @author Anish, CS 10, Winter 2022, for the purpose of completing problem set 4
 */


public class BaconGame {
    // initializing buffered readers for the respective files
    BufferedReader actorReader;
    BufferedReader movieReader;
    BufferedReader movieActorReader;

    // initializing the graphs
    Graph<String, Set<String>> BaconGraph;
    Graph<String, Set<String>> shortestPathTreeGraph;

    // maps that map ID to actor name and ID to movie name and a helper map for the movies-actors file
    Map<Integer, String> IDtoActorName;
    Map<Integer, String> IDtoMovieName;

    // helper map for the movies-actors file
    Map<Integer, Set<Integer>> movieIDtoactorID;

    // the center of the universe at the start of the game is Kevin Bacon
    String center;

    // the scanner for reading inputs
    Scanner input;

    /**
     * Constructor of Bacon Game
     * Still working on this lol
     *
     * @param center            The center of the universe
     * @param input             The scanner for reading inputs
     * @param actorFileName     The filename for the actors
     * @param movieFileName     The filename for the movies
     * @param actorToMovieFile  The filename of the actor to movie file
     */
    public BaconGame (String center, Scanner input, String actorFileName, String movieFileName, String actorToMovieFile) throws IOException {

        // initializes & instantiates a new Graph and puts it into the Bacon Graph instance variable
        this.BaconGraph = new AdjacencyMapGraph<>();

        // passes the center parameter into the center instance variable
        this.center = center;

        // passes the input parameter into the input instance variable
        this.input = input;


        try {

            // builds the relevant graphs using the files
            buildRelevantGraphs(actorFileName, movieFileName, actorToMovieFile);

            // builds the shortest path tree graph, running bfs on BaconGraph using center
            shortestPathTreeGraph = GraphLib.bfs(BaconGraph, center);

//            System.out.println(shortestPathTreeGraph);

        }
        catch (IOException e) {

            // if there's an exception, print the error message instead
            System.out.println("Error message: " + e);
        }

    }


    /**
     * Takes in the file names as parameters and builds the relevant maps for them
     *
     * @param actorsFile            The filename for the actors
     * @param moviesFile            The filename for the movies
     * @param moviesActorsFile      The filename of the actor to movie file
     * @throws IOException
     */
    public void buildRelevantGraphs (String actorsFile, String moviesFile, String moviesActorsFile) throws IOException {
        String fileLines;

        // Building the map for ID to Actor names
        actorReader = new BufferedReader(new FileReader(actorsFile));
        IDtoActorName = new HashMap<>();
        // read the first line
        String actorLines = actorReader.readLine();
        while (actorLines != null) {
            // make a list of strings, split by \\|
            String [] actorFileLinePieces = actorLines.split("\\|");
            // puts the ID as a key, and an actor name as a value
            IDtoActorName.put(Integer.valueOf(actorFileLinePieces[0]), actorFileLinePieces[1]);
            // read the next line
            actorLines = actorReader.readLine();
        }
        // close the reader
        actorReader.close();

        // Building the map for ID to Movie names
        movieReader = new BufferedReader(new FileReader(moviesFile));
        IDtoMovieName = new HashMap<>();
        // read the first line
        String movieLines = movieReader.readLine();
        while (movieLines != null) {
            // make a list of strings, split by \\|
            String [] movieFileLinePieces = movieLines.split("\\|");
            // puts the ID as a key, and a movie name as a value
            IDtoMovieName.put(Integer.valueOf(movieFileLinePieces[0]), movieFileLinePieces[1]);
            // read the next line
            movieLines = movieReader.readLine();
        }
        // close the reader
        movieReader.close();

        // Building the map for Movie ID to Actor ID
        // Storing all the movie actor pairs in a map whose key is a movie and whose value is the set of actors in the movie
        movieActorReader = new BufferedReader(new FileReader(moviesActorsFile));
        movieIDtoactorID = new HashMap<>();
        // read the first line
        fileLines = movieActorReader.readLine();
        while (fileLines != null) {
            // make a list of strings, split by \\|
            String [] pieces = fileLines.split("\\|");
            // if the length of the list of strings is greater than one
            if (pieces.length > 1) {
                // if the movie ID is not already in the hashMap as a key
                if (!movieIDtoactorID.containsKey(Integer.valueOf(pieces[0]))) {
                    // put the movie ID in the hashMap as a key, with a new initialized hashSet as a value
                    movieIDtoactorID.put(Integer.valueOf(pieces[0]), new HashSet<>());
                }
                // add the actor ID to the hashSet for this movie
                movieIDtoactorID.get(Integer.valueOf(pieces[0])).add(Integer.valueOf(pieces[1]));
            }
            // read the next line
            fileLines = movieActorReader.readLine();
        }
        // close the reader
        movieActorReader.close();


        // Time to build the Bacon graph!
        // Vertices of the graph are actor names

        // for each actorID in the set of keys given from the map IDtoActorName
        for (Integer actorID: IDtoActorName.keySet()) {

            // insert a new Vertex in the Bacon graph with the name of the actor (no connections)
            BaconGraph.insertVertex(IDtoActorName.get(actorID));
        }

        /*
        I want to label the edges
        This is a little tricky. First, I have to iterate through the map of movieID to actor ID, get the set of actors
        for each movie, iterate through the set of actors with a double loop, and draw edges between actors who co-starred
        The label of the edges is the name of the Movie
        */

        // for each movieID in the set of keys given from the map movieIDtoactorID
        for (Integer movieID: movieIDtoactorID.keySet()) {

            // get the corresponding movieName
            String movieName = IDtoMovieName.get(movieID);

            // for each actorID in the set of actors corresponding to the movie
            for (Integer actor1: movieIDtoactorID.get(movieID)) {
                // for each actorID in the set of actors corresponding to the movie
                for (Integer actor2: movieIDtoactorID.get(movieID)) {

                    // get the corresponding actorNames
                    String newActor1 = IDtoActorName.get(actor1);
                    String newActor2 = IDtoActorName.get(actor2);

                    // if the actors aren't the same people, and there is no edge between actor1 and actor 2
                    if (actor1 != actor2 && !BaconGraph.hasEdge(newActor1, newActor2)) {
                        // create a new set and add the movie name that they appeared in to this set
                        Set<String> movieSet = new HashSet<>();
                        movieSet.add(movieName);
                        // insert an undirected edge between the actors, with the movieSet as the label
                        BaconGraph.insertUndirected(newActor1, newActor2, movieSet);
                    }
                    // if the actors already have an edge between each other
                    else if (actor1 != actor2 && BaconGraph.hasEdge(newActor1, newActor2)) {
                        // add this movie name, which the actors have in common, to the set located in the label
                        BaconGraph.getLabel(newActor1, newActor2).add(movieName);
                    }
                }
            }
        }
    }


    /**
     * Change the center of the universe to a new actor
     * @param newActorName      Actor that we want to change the center of the universe to
     */
    public void changeUniverseCenter (String newActorName) {

        // if the Bacon Graph has this actor as one of its vertices
        if (BaconGraph.hasVertex(newActorName)) {

            // update the center
            center = newActorName;

            // make a new shortestPathTreeGraph, by rerunning bfs
            shortestPathTreeGraph = GraphLib.bfs(BaconGraph, center);

            // print the update in console
            System.out.println(center + " is now the center of the universe, connected to " +
                    shortestPathTreeGraph.numVertices() + "/" + BaconGraph.numVertices() + " actors with average separation " +
                    GraphLib.averageSeparation(shortestPathTreeGraph, center));
        }

        // if the Bacon Graph doesn't have this actor as one of its vertices
        else {
            // print an error message
            System.err.println("No such actor exists in the dataset.");
        }
    }

    /**
     * Finds the shortest path from a requested actor to the desired center of the graph
     * @param requestedActor
     * @return
     */
    public void findShortestPathToActor (String requestedActor) {
        if (shortestPathTreeGraph.hasVertex(requestedActor)) {
            List<String> shortestPath = GraphLib.getPath(shortestPathTreeGraph, requestedActor);

            // decrements the shortestPath.size() by one, in order to neglect the first connection
            // (always a null connection with itself)
            int actualShortestPath = shortestPath.size() - 1;
            System.out.println(requestedActor + "'s number is " + actualShortestPath);
            String currentActor = requestedActor;

            // prevents from printing the first connection, which is always a null connection with itself
            Boolean preventionSwitch = false;

            for (String eachActor: shortestPath) {
                if (preventionSwitch) {
                    System.out.println(currentActor + " appeared in " + BaconGraph.getLabel(currentActor, eachActor) + " with " + eachActor);
                    currentActor = eachActor;
                }
                preventionSwitch = true;
            }
        }

        else {
            System.err.println(requestedActor + " is not connected to " + center);
        }
    }


    public void printCommands () {
        System.out.println("Commands:\n" +
                "c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation\n" +
                "d <low> <high>: list actors sorted by degree, with degree between low and high\n" +
                "i: list actors with infinite separation from the current center\n" +
                "p <name>: find path from <name> to current center of the universe\n" +
                "s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high\n" +
                "u <name>: make <name> the center of the universe\n" +
                "q: quit game\n" +
                "\n" +
                center + " is now the center of the universe, connected to " +
                shortestPathTreeGraph.numVertices() + "/" + BaconGraph.numVertices() + " actors with average separation " +
                GraphLib.averageSeparation(shortestPathTreeGraph, center));
    }


    /**
     * list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation
     * @param number
     */
    public void listTopOrBottomSortedByAverageSeparation (int number) {
        Set<String> verticesConnectedToSource = new HashSet<>();
        for (String eachVertex: shortestPathTreeGraph.vertices()) verticesConnectedToSource.add(eachVertex);

        // Helper map and list for sorting
        ArrayList<String> universeCenter = new ArrayList<>();
        Map<String, Double> universeMap = new HashMap<>();

        for (String eachActor: BaconGraph.vertices()) {
            // Stores average path lengths over all the actors (vertices) currently connected to the center
            if (verticesConnectedToSource.contains(eachActor)) {
                double separation = GraphLib.averageSeparation(GraphLib.bfs(BaconGraph, eachActor), eachActor);
                universeMap.put(eachActor, separation);
                universeCenter.add(eachActor);
            }
        }

        universeCenter.sort(Comparator.comparingDouble(universeMap::get));

        List<String> sortedActorsList = new ArrayList<>();

        if (number > 0) {
            for (int i = 0; i < number; i++) {
                sortedActorsList.add(universeCenter.get(i));
            }
        }

        else {
            for (int i = universeCenter.size() - 1; i > universeCenter.size() + number - 1; i--) {
                sortedActorsList.add(universeCenter.get(i));
            }
        }

        System.out.println("People sorted by average separation are: " + "\n");
        for (String actor: sortedActorsList) System.out.println(actor);
    }


    /**
     * list actors sorted by degree, with degree between low and high
     * @param low
     * @param high
     */
    public void listActorsSortedByDegreeOfSeparation (int low, int high) {
        ArrayList<String> actorsByDegree = new ArrayList<>();
        for (String eachActor: BaconGraph.vertices()) {
            if (BaconGraph.inDegree(eachActor) >= low && BaconGraph.inDegree(eachActor) <= high) {
                actorsByDegree.add(eachActor);
            }
        }

        actorsByDegree.sort(Comparator.comparingInt((String s) -> BaconGraph.inDegree(s)));

        if (!actorsByDegree.isEmpty()) {
            System.out.println("Sorted actors with number of connections from " + low + " to " + high + " are: ");
            for (String actor: actorsByDegree) System.out.println(actor);
        }

        else {
            System.out.println("Range of " + low + " to " + high + " is empty.");
        }
    }


    /**
     * list actors with infinite separation from the current center
     */
    public void actorsWithInfiniteSeparation () {
        System.out.println("People with infinite separation from the center, i.e who are not connected are: " + "\n");
        System.out.println(GraphLib.missingVertices(BaconGraph, shortestPathTreeGraph));
    }


    /**
     * <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high
     * @param low
     * @param high
     */
    public void actorsSortedByNonInfiniteSeparation (int low, int high) {

        // Helper map and list for sorting
        ArrayList<String> currentCenterList = new ArrayList<>();
        Map<String, Integer> currentMap = new HashMap<>();

        // The center is separated 0 times from themselves
        if (low == 0 && high == 0) {
            System.out.println("People sorted by non infinite separation from the current center between " + low + " and " + high + " are: ");
            System.out.println(center);
        }

        else {
            System.out.println("People sorted by non infinite separation from the current center between " + low + " and " + high + " are: ");

            // if the low is zero, include the center
            if (low == 0) {
                System.out.println(center);
            }

            // for each actor within the shortest path tree graph
            for (String eachActor : shortestPathTreeGraph.vertices()) {
                // if the actor is not the center
                if (!eachActor.equals(center)) {
                    // get the path size between the actor and the center, subtract it by one, and set it equal to integer
                    int avgNonInfiniteSep = GraphLib.getPath(shortestPathTreeGraph, eachActor).size() - 1;

                    // add each actor to the center list
                    currentCenterList.add(eachActor);

                    // add each actor to the map, with its path size
                    currentMap.put(eachActor, avgNonInfiniteSep);
                }
            }

            // sort the list
            currentCenterList.sort(Comparator.comparingInt(currentMap::get));

            int i = 0;
            for (String eachEntry : currentMap.keySet()) {
                if (currentMap.get(eachEntry) >= low && currentMap.get(eachEntry) <= high) {
                    System.out.println(currentCenterList.get(i));
                    i = i + 1;
                }
            }
        }

        System.out.println("End of this \n");
    }


    public String getInput (String prompt) {
        System.out.println(prompt);
        return input.nextLine();
    }

    /**
     * returns a boolean
     * useful if q is pressed thereby helping to end the game
     * @param input
     * @return
     */
    public boolean handleInput (String input) {
        // first step is to handle single inputs like i and q
        if (input == null) {
            System.out.println("Enter a valid input.");
        }

        assert input != null;
        String[] readInput = input.split(" ");

        if (readInput[0].equals("q")) {
            return false;
        }

        else if (readInput[0].equals("i")) {
            actorsWithInfiniteSeparation();
        }

        // next step is to handle inputs with two entries, e.g c, p and u commands
        if (readInput[0].equals("c")) {
            try {
                Integer number = Integer.valueOf(readInput[1]);
                listTopOrBottomSortedByAverageSeparation(number);
            }
            catch (Exception e) {
                System.out.println("Error message: " + e);
            }
        }

        else if (readInput[0].equals("p")) {
            try {

                // instantiate a new string called name
                String name = "";

                // loop over every word in readInput, except the first and last one
                for (int i = 1; i < readInput.length - 1; i++) {
                    // append it to a string with a space at the end of it
                    name = name + readInput[i] + " ";
                }

                // append the last word to the string, without the space
                name = name + readInput[readInput.length - 1];


                if (BaconGraph.hasVertex(name)) findShortestPathToActor(name);
                else System.out.println("Name does not exist in dataset");
            }
            catch (Exception e) {
                System.out.println("Error message: " + e);
            }
        }

        else if (readInput[0].equals("u")) {
            try {

                // instantiate a new string called name
                String name = "";

                // loop over every word in readInput, except the first and last one
                for (int i = 1; i < readInput.length - 1; i++) {
                    // append it to a string with a space at the end of it
                    name = name + readInput[i] + " ";
                }

                // append the last word to the string, without the space
                name = name + readInput[readInput.length - 1];

                if (BaconGraph.hasVertex(name)) changeUniverseCenter(name);
                else System.out.println("Name does not exist in dataset");
            }
            catch (Exception e) {
                System.out.println("Error message: " + e);
            }
        }

        // final step is to handle inputs with three entries, i.e the d and s commands
        else if (readInput[0].equals("d")) {
            assert readInput.length == 3;
            try {
                Integer low = Integer.parseInt(readInput[1]);
                Integer high = Integer.parseInt(readInput[2]);
                listActorsSortedByDegreeOfSeparation(low, high);
            }
            catch (Exception e) {
                System.out.println("Error message: " + e);
            }
        }

        else if (readInput[0].equals("s")) {
            assert readInput.length == 3;
            try {
                Integer low = Integer.parseInt(readInput[1]);
                Integer high = Integer.parseInt(readInput[2]);
                actorsSortedByNonInfiniteSeparation(low, high);
            }
            catch (Exception e) {
                System.out.println("Error message: " + e);
            }
        }

        return true;
    }



    public void playBaconGame () {
        printCommands();
        String newInput = getInput("\n" + "Kevin Bacon game >");
        boolean isValid = handleInput(newInput);
        while (isValid) {
            newInput = getInput("\n" + "Kevin Bacon game >");
            isValid = handleInput(newInput);
        }
    }
}