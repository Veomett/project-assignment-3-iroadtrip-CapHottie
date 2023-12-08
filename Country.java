import java.util.*;

public class Country implements Comparable<Country>{
    private String code; //i.e. USA, CAN
    private int countryNumber; //i.e. 2, 20
    private List<String> name; //head will always be main name that appears on tsv file
    private List<Country> neighbors;
    private boolean visited; //only used by Path class
    private Country path; //country needed to cross to get here
    public int cost; //cumulative distance based on path

    public Country(int num, String ID, String mainName) {
        this.countryNumber = num;
        this.code = ID;
        this.name = new ArrayList<>();
        name.add(mainName);

        //don't need to add until border.txt is parsed. constructor is called at state_name
        this.neighbors = new ArrayList<>();

        //dual use as vertex node for Path class
        this.visited = false;
        this.path = null;
        this.cost = Integer.MAX_VALUE;
    }

    public void add_alias(String alias) {
        if (!name.contains(alias)){
            name.add(alias);
        }
    }
    public String get_mainName() {
        return name.get(0);
    }
    public boolean get_visited() {
        return visited;
    }
    public void set_visited(boolean newStatus) {
        visited = newStatus;
    }
    public Country get_last_visit() {
        return path;
    }
    public void set_path(Country lastCountry) {
        path = lastCountry;
    }
    public void set_cost(int newCost) {
        cost = newCost;
    }
    public int get_cost() {
        return cost;
    }
    public void add_neighbor(Country neighbor) {
        neighbors.add(neighbor);
    }
    public String get_stateID() {
        return code;
    }
    public int get_CountryNumber() {
        return countryNumber;
    }
    public List<Country> get_neighborList() {
        return neighbors;
    }

    @Override
    public int compareTo(Country o) {
        return Integer.compare(this.cost, o.cost);
    }
}
