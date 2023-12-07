import java.util.*;

public class Country {
    private String code; //i.e. USA, CAN
    private int countryNumber; //i.e. 2, 20
    private List<String> name; //head will always be main name that appears on tsv file
    private HashMap<String, Country> neighbors;

    public Country(int num, String ID, String mainName) {
        this.countryNumber = num;
        this.code = ID;
        this.name = new ArrayList<>();
        name.add(mainName);

        this.neighbors = new HashMap<>();
        //don't need to add until border.txt is parsed. constructor is called at state_name
    }

    public boolean contains_alias(String possibleName) {
        for (int i = 0; i < name.size(); i++) {
            if (name.get(i).compareTo(possibleName) == 0) {
                return true;
            }
        }
        return false;
    }

    public void add_alias(String alias) {
        if (!name.contains(alias)){
            name.add(alias);
        }
    }
    public void add_neighbor(Country neighbor) {
        neighbors.put(neighbor.get_stateID(), neighbor);
    }
    public String get_stateID() {
        return code;
    }
    public int get_CountryNumber() {
        return countryNumber;
    }
}
