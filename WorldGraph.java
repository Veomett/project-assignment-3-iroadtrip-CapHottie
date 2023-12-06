import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldGraph {
    private class Country {
        private int code;
        private HashMap<Integer, Integer> edges;
        public Country(int code) {
            this.code = code;
            this.edges = new HashMap<>();
        }
        public void draw_edge(int target, int distance) {
            edges.put(target, distance);
        }
        public int get_distance_to(int target) {
            return edges.get(target);
        }
        public boolean is_neighbor(int target) {
            return edges.containsKey(target);
        }
    }
    private HashMap<Integer, Country> Map; //this implies the graph will be undirected because edges can go either way
    public WorldGraph() {
        this.Map = new HashMap<>();
    }

    public void add_countryNode(int code) {
        Country newCountry = new Country(code);

    }
}