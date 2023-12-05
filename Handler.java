import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

public class Handler {
    private class Edge {
        private HashMap<Integer, Integer> targetList;
        public Edge() {
            this.targetList = new HashMap<>();
        }

        public void add_edge(int target, int weight) {
            targetList.put(target, weight);
        }
    }

    /*
    1. Return list of countries and who they border
    2. Return list of edges
    3. Return list of most recent data on countries, their code, and other aliases
    */
    private String separator;
    private final String PRESENT = "2020-12-31";
    private HashMap<String, String[]> borders;
    private HashMap<Integer, Edge> edges;
    private HashMap<Integer, List<String>> aliasDictionary;

    private HashMap<Integer, String> stateDictionary;
    //each parameter is an arraylist of text rows containing data

    public Handler(List<String> borderFile, List<String> distFile, List<String> nameFile) {
        this.separator = "\t";
        this.stateDictionary = write_state_dictionary(nameFile);
        set_separator("; ");
        this.borders = write_borders_list(borderFile);
        set_separator(",");
        this.edges = write_edges(distFile);
    }

    private void join_file_alias() {
        /*
        borders contains a String[] of bordering countries, key is the given country
        edges is a 2d hashmap, first key is source country, key 2 is target country, value is distance
        stateDictionary contains country code, key is ID
        */
        aliasDictionary = new HashMap<>();

    }
    /*
    only put country and code pairs if the value in the end column (index 4) is 2020-12-31
    returns hashmap of country id and country abbreviation pair
    */

    private HashMap<Integer, String> write_state_dictionary(List<String> nameFileRows) {
        HashMap<Integer, String> dictionary = new HashMap<>();

        String[] values;
        ListIterator<String> iterator = nameFileRows.listIterator(0);
        String currentRow;

        while (iterator.nextIndex() < nameFileRows.size()) {
            currentRow = iterator.next();//skip column labels
            values = currentRow.split(separator);
            if (values[4].compareTo(PRESENT) == 0) {
                //up-to-date entry
                dictionary.put(Integer.parseInt(values[0]), values[2]);
            }
        }

        return dictionary;
    }

    private HashMap<String, String[]> write_borders_list(List<String> bordersRows) {
        HashMap<String, String[]> bordersList = new HashMap<>();

        ListIterator<String> iterator = bordersRows.listIterator(0);
        String row = bordersRows.get(0);
        String key;
        String[] borderingCountries;

        while (iterator.nextIndex() < bordersRows.size()) {
            key = row.substring(0, row.indexOf(" ="));
            borderingCountries = row.substring(row.indexOf("=") + 2).split(separator);
            //only care about what country it borders. border length will be parsed out for worldgraph obj
            bordersList.put(key, borderingCountries);
            row = iterator.next();
        }

        return bordersList;
    }

    private HashMap<Integer, Edge> write_edges(List<String> distRows) {
        HashMap<Integer, Edge> edgeTable = new HashMap<>();

        ListIterator<String> iterator = distRows.listIterator(0);
        String row;
        String[] values;
        Edge currentEdge;
        Integer source;
        Integer target;
        Integer weight;

        while (iterator.nextIndex() < distRows.size()) {
            row = iterator.next(); //skip labels
            values = row.split(separator);
            source = Integer.parseInt(values[0]);
            target = Integer.parseInt(values[2]);
            weight = Integer.parseInt(values[4]);
            //[0]source country ID, [2]target country ID, [4]kmdist
            if (edgeTable.containsKey(source)) {
                edgeTable.get(source).add_edge(target, weight);
            }
            else {
                currentEdge = edgeTable.put(source, new Edge());
                currentEdge.add_edge(target, weight);
            }
        }

        return edgeTable;
    }

    public void set_separator(String newSeparator) {
        separator = newSeparator;
    }

    public String get_separator() {
        return separator;
    }
}
