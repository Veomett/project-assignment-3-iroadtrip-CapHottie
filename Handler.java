import java.util.*;

public class Handler {
    /*
    take state_name.tsv: put in hashmap<3 letter country code, LL of country names> only recent entries
    borders.txt: new hash<country name (could be alias), ArrayL of bordering countries> parse out border length
    capdist: hash <3 letter code, Country>

    INSTEAD:
    make a single hashmap <3 letter code, Country object>
        Country contains name, alias, number ID, and bordering countries

    1.-iterate thru statename and instantiate countries
    2.-on borders, add new alias/names in () and format comma'd names, add neighbors
    */

    private final String PRESENT = "2020-12-31";
    private HashMap<String, String> matchList; //key: country name, value: 3 letter code
    private HashMap<String, Country> Atlas; //key: 3 letter code
    private HashMap<String, HashMap<String, Integer>> borderDistances;

    public HashMap<String, String> get_nameList() {
        return matchList;
    }
    public HashMap<String, Country> get_Map() {
        return Atlas;
    }public HashMap<String, HashMap<String, Integer>> get_GraphEdges() {
        return borderDistances;
    }

    public Handler(List<String> borderRows, List<String> capdistRows, List<String> stateNameRows) {
        this.Atlas = new HashMap<>();
        this.matchList = new HashMap<>();
        this.borderDistances = new HashMap<>();
        special_matches();
        write_country_list(stateNameRows);
        handle_borders(borderRows);
        get_edges(capdistRows);
    }

    private void get_edges(List<String> capdistrows) {
        String[] values;
        HashMap<String, Integer> currentEdge;
        for (int i = 1; i < capdistrows.size(); i++) {
            values = capdistrows.get(i).split(",");
            //[1]ida [3]idb [4]kmdist
            String sourceID = values[1];
            String targetID = values[3];
            int distance = Integer.parseInt(values[4]);
            if (borderDistances.containsKey(sourceID)) {
                //already instantiated a hashmap for source country
                currentEdge = borderDistances.get(sourceID);
                currentEdge.put(targetID, distance);
            }
            else {
                //first time adding an edge to this country "node"
                currentEdge = new HashMap<>();
                currentEdge.put(targetID, distance);
                borderDistances.put(sourceID, currentEdge);
            }
        }
    }

    /*
    need to implement info from borderstxt after instantiating all country objs
    neighbor hashmap values should be ptrs to already existing objs
    1.format each row from borderstxt into a list of country names
    2.search for country name in Atlas to match with existing country. if not found, ignore
    2a. if matched, get neighbor country and add it to head of list's neighbor map
    */
    private void handle_borders(List<String> borderstxt) {
        String currentRow;
        String[] rowValues;

        for (int i = 0; i < borderstxt.size(); i++) { //loop to get new aliases
            currentRow = borderstxt.get(i);
            rowValues = currentRow.split("=");
            //rowValues[0] has name and possible aliases. values [1] has country X km; ...
            List<String> names = format_borderRow_name(rowValues[0]);
            Country match;
            String alias;
            for (int j = 0; j < names.size(); j++) {
                alias = names.get(j);
                //if one or more aliases identify a country in Atlas, assign to match, add more aliases, add borders
                if (matchList.containsKey(alias)) {
                    match = Atlas.getOrDefault(matchList.get(alias), null);
                    //MATCH FOUND. add borders and new aliases if applicable
                    while (!names.isEmpty()){
                        match.add_alias(names.remove(0));
                    }
                    //populate neighbors
                    add_neighbors(match, rowValues[1]);
                }

            }
        }
    }

    //only concerned with string prior to =. formattign will be different with bordering countries

    private List<String> format_borderRow_name(String values) {
        //[1] name/alias. [2] neighbors
        List<String> names = new ArrayList<>();
        if (values.contains(",")) {
            String[] separated = values.split(", ");
            names.add(separated[1].concat(separated[0]));
        }
        else if (values.contains("(")) {
            int start = values.indexOf("(");
            String alias = values.substring(start + 1, values.indexOf(")"));
            names.add(alias);
            alias = values.substring(0, start).trim();
            names.add(alias);
        }
        else {
            names.add(values.trim());
        }
        return names;
    }
    private void add_neighbors(Country source, String bordersRow) {
        Country neighbor;
        String[] countries = bordersRow.split("km;");

        for (int i = 0; i < countries.length; i++) {
            int index;
            if (countries[i].contains("(")) {
                index = countries[i].indexOf("(");
                countries[i] = countries[i].substring(0, index).trim();
                continue;
            }
            index = 0;
            char[] charArray = countries[i].toCharArray();
            while (index < charArray.length && !Character.isDigit(charArray[index])) {
                index++;
            }
            //index is where there's a number
            countries[i] = countries[i].substring(0, index).trim();
        }
        for (String country : countries) {
            neighbor = Atlas.getOrDefault(matchList.get(country), null);
            if (neighbor != null) {
                source.add_neighbor(neighbor);
            }
        }
    }

    private void write_country_list(List<String> tsvRows) {
        String currentRow;
        String[] cellValues;

        for (int i = 1; i < tsvRows.size(); i++) {
            currentRow = tsvRows.get(i);
            cellValues = currentRow.split("\t");
            // field order: 1 statenumber 2  stateid 3 country name 4 start 5 end
            if (cellValues[4].compareTo(PRESENT) == 0) {
                int rowCode = Integer.parseInt(cellValues[0]);
                Stack<String> names = format_tsv_aliases(cellValues[2]);
                matchList.put(names.peek(), cellValues[1]);
                //matchlist (key, value) = main name, 3 letter code
                Country newCountry = new Country(rowCode, cellValues[1], names.pop());
                while (!names.isEmpty()){
                    matchList.put(names.peek(), cellValues[1]);
                    newCountry.add_alias(names.pop());
                }
                if (!Atlas.containsKey(newCountry.get_stateID())) {
                    //make sure there's only one of each
                    Atlas.put(newCountry.get_stateID(), newCountry);
                }
            }
        }
    }

    /*
    Name could have comma which indicates string up to that comma should go in front of the remainder.
    Names in parentheses are an additional alias
    Same for slashes. Get () first, then /, then format comma'd names
    */
    private Stack<String> format_tsv_aliases(String countryNameColumn) {
        Stack<String> aliasStack = new Stack<>();
        String formattedName;
        int startIndex = countryNameColumn.indexOf("(");
        int endIndex;

        if (startIndex != -1) {
            //alias in ()
            endIndex = countryNameColumn.indexOf(")");
            formattedName = countryNameColumn.substring(startIndex + 1, endIndex);
            //formattedName is "alias" without parantheses
            String[] moreAliases = formattedName.split("/");
            for (String newAlias : moreAliases) {
                aliasStack.push(newAlias);
            }
            formattedName = countryNameColumn.substring(0, startIndex - 1);
            //NOW formattedName is the countryname without aliases in parentheses
        }
        else {
            //no parentheses
            formattedName = new String(countryNameColumn);
        }

        //no more aliases in (). check for /
        if (formattedName.contains("/")){
            String[] slashed = formattedName.split("/");
            for (int i = slashed.length - 1; i >= 0; i--) {
                aliasStack.push(slashed[i]);
            }
        }
        //format names with commas
        else if (formattedName.contains(",")) {
            String[] separated = formattedName.split(",");
            aliasStack.push(separated[1].concat(" ").concat(separated[0]).trim());
        }
        else {
            aliasStack.push(formattedName);
        }

        return aliasStack;
    }

    private void special_matches() {
        matchList.put("The Bahamas", "BHM");
        matchList.put("Burma", "MYA");
        matchList.put("Cabo Verde", "CAP");
        matchList.put("Republic of the Congo", "CON");
        matchList.put("Democratic Republic of the Congo", "DRC");
        matchList.put("Cote d'Ivoire", "CDI");
        matchList.put("Czechia", "CZR");
        matchList.put("Eswatini", "SWA");
        matchList.put("Germany", "GFR");
        matchList.put("North Korea", "PRK");
        matchList.put("South Korea", "ROK");
        matchList.put("North Macedonia", "MAC");
        matchList.put("Romania", "RUM");
        matchList.put("United States", "USA");
        matchList.put("US", "USA");
        matchList.put("USA", "USA");
        matchList.put("UK", "UKG");
        matchList.put("Ivory Coast", "CDI");
    }
}
