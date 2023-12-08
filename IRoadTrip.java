import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IRoadTrip {
    private final int INFINITY = Integer.MAX_VALUE; //can't get much higher than infinity...
    private HashMap<String, String> stateDictionary;

    private HashMap<String, Country> WorldGraph;
    private HashMap<String, HashMap<String, Integer>> Edges;
    private PriorityQueue<Country> nextVisit;

    //constructor. passes name of files given to main. assumes order is correct:
    //borders.txt capdist.csv state_name.tsv
    public IRoadTrip (String [] args) {
        switch(args.length) {//I just like switch case, heard it's faster
            case 3:
                break;
            default:
                System.out.println("One or more files are missing from main()");
                System.exit(1);
        }

        List<String> bordersRows = new ArrayList<>();
        List<String> capdistRows = new ArrayList<>();
        List<String> nameRows = new ArrayList<>();

        String row;

        for (String filePath : args) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                if (filePath.compareTo("borders.txt") == 0){
                    while ((row = reader.readLine()) != null) {
                        bordersRows.add(row);
                    }
                }
                else if (filePath.compareTo("capdist.csv") == 0){
                    while ((row = reader.readLine()) != null) {
                        capdistRows.add(row);
                    }

                }
                else if (filePath.compareTo("state_name.tsv") == 0){
                    while ((row = reader.readLine()) != null) {
                        nameRows.add(row);
                    }

                }
            }
            catch (IOException error){
                error.printStackTrace();
            }
        }
        Handler handler = new Handler(bordersRows, capdistRows, nameRows);
        this.stateDictionary = new HashMap<>(handler.get_nameList());
        this.WorldGraph = new HashMap<>(handler.get_Map());
        this.Edges = new HashMap<>(handler.get_GraphEdges());
        nextVisit = new PriorityQueue<>();
    }

    /*
    * return -1 if invalid pair
    * country1 will typically be source node
    * params are full names of country w possible alias
    */
    public int getDistance (String country1, String country2) {
        Country source = get_Country(country1);
        Country target = get_Country(country2);
        if (source == null || target == null || !Edges.containsKey(source.get_stateID()) || !Edges.containsKey(target.get_stateID())) {
            return -1;
        }
        //check if they share border
        if (!source.get_neighborList().contains(target)) {
            return -1;
        }

        return Edges.get(source.get_stateID()).get(target.get_stateID()); //in km. value of the value of the Edges hashmap
    }

    private void resetGraph() {
        WorldGraph.forEach(
                (key, value) -> {
                    value.set_path(null);
                    value.set_visited(false);
                    value.set_cost(INFINITY);
                }
        );
    }

    private Country next_visit(Country currentVertex) {
        for (Country neighbor : currentVertex.get_neighborList()) {
            if (!neighbor.get_visited() && !nextVisit.contains(neighbor)){
                nextVisit.add(neighbor);
            }
        }
        if (nextVisit.isEmpty()) {
            currentVertex.set_visited(true);
            return next_visit(currentVertex.get_last_visit());
        }

        try {
            while (nextVisit.peek().get_CountryNumber() == 626 || nextVisit.peek().get_CountryNumber() == 347) {
                nextVisit.poll();
            }
        }
        catch (NullPointerException nullCountry) {
            return next_visit(currentVertex.get_last_visit());
        }
        return nextVisit.poll();
    }

    /*
    * return list of countries through which to travel to get to country 2
    * return EMPTY list if impossible, not NULL
    * Use queue to populate List return value by enqueueing path from each country node
    */
    public List<String> findPath (String country1, String country2) {
        resetGraph();
        Country source = get_Country(country1);
        Country target = get_Country(country2);
        source.set_cost(0);
        List<String> path = new ArrayList<>();

        if (!DFSearch_target(source, target)) {
            //return empty list if impossible path
            return path;
        }
        //source and target are in the same set "tree" of paths
        Country currentNode = source;
        Country adjacent;
        while (currentNode != null) {
            currentNode.set_visited(true);
            for (int i = 0; i < currentNode.get_neighborList().size(); i++) {
                adjacent = currentNode.get_neighborList().get(i);
                int proposedCost = currentNode.get_cost() + getDistance(currentNode.get_mainName(), adjacent.get_mainName()); //cost(v) + edge_weight(v, n), respectively
                if (adjacent.get_cost() > proposedCost) {
                    adjacent.set_cost(proposedCost);
                    adjacent.set_path(currentNode);
                }
            }
            if (target.get_visited()) {
                break;
            }
            currentNode = next_visit(currentNode);
        }

        currentNode = target;
        while (currentNode != null) {
            path.addFirst(currentNode.get_mainName());
            currentNode = currentNode.get_last_visit();
        }
        return path;
    }

    private String formatStep(Country currNode, Country nextNode) {
        String step = new String(currNode.get_mainName());
        String nextName = nextNode.get_mainName();
        int edge = getDistance(step, nextName);

        step = step.concat(" --> ");
        step = step.concat(nextName);
        step = step.concat(" (");
        step = step.concat(Integer.toString(edge));
        return step.concat(" km.)");
    }

    /*
    * Parameters are respective stateID (3-letter codes)
    * returns true if there is a possible path regardless of cost
    * Start by pushing source country, then put in visited map marked as true
    * return true if at any point target can be found in the stack or the map
    * while loop stops when target is in either data obj
    */
    private boolean DFSearch_target(Country source, Country target) {
        HashMap<Country, Boolean> visitedMap = new HashMap<>();
        Stack<Country> searchList = new Stack<>();
        Country visitedCountry = source;

        while (visitedCountry != null) {
            //add visited country to visitedMap to keep track and avoid infinite looping
            visitedMap.put(visitedCountry, true);
            //end if target has a viable path from source
            if (visitedMap.containsKey(target) || searchList.contains(target)) {
                return true;
            }
            //push neighboring countries to stack so they can be visited
            for (Country neighbor : visitedCountry.get_neighborList()) {
                if (!visitedMap.containsKey(neighbor)) {
                    searchList.push(neighbor);
                }
            }
            //will be null if no more countries to check
            try {
                visitedCountry = searchList.pop();
            }
            catch (EmptyStackException emptyStack) {
                return false;
            }
        }
        return false;
    }

    private void print_pathList(List<String> steps) {
        List<String> formattedPath = new ArrayList<>();
        Country curr;
        Country next;
        for (int i = 0; i < steps.size() - 1; i++) {
            curr = get_Country(steps.get(i));
            next = get_Country(steps.get(i + 1));
            formattedPath.add(formatStep(curr, next));
        }
        for (int i = 0; i < formattedPath.size(); i++) {
            System.out.println("* " + formattedPath.get(i));
        }
    }

    public void acceptUserInput() {
        Scanner receiver = new Scanner(System.in);
        List<String> countryPair = new LinkedList<>();
        String userInput = "";
        while(true) {
            if (countryPair.isEmpty()){
                System.out.print("Enter the name of the first country (type EXIT to quit):");
            }
            else {
                System.out.print("Enter the name of the second country (type EXIT to quit):");
            }
            userInput = receiver.nextLine();
            if (userInput.compareTo("EXIT") == 0) {
                break;
            }
            if (get_Country(userInput) == null || userInput.compareTo("Kosovo") == 0 || userInput.compareTo("South Sudan") == 0) {
                System.out.println("Invalid country name. Please enter a valid country name.");
                continue;
            }
            countryPair.add(userInput);
            if (countryPair.size() == 2) {
                System.out.println("Route from " + countryPair.get(0) + " to " + countryPair.get(1) + ":");
                print_pathList(findPath(countryPair.remove(0), countryPair.remove(0)));
            }
        }
        receiver.close();
    }

    private String get_countryID(String alias) {
        //nan is default in case it's invalid
        return stateDictionary.getOrDefault(alias, "nan");
    }

    //expects name spelled out and returns the respective country it refers to
    //returns null if name can't be validated or doesn't exist
    private Country get_Country(String fullname) {
        return  WorldGraph.getOrDefault(get_countryID(fullname), null);
    }

    //in order, main receives borders.txt, capdist.csv, and state_names.tsv
    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

}

