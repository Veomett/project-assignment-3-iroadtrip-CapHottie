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
    }
    private HashMap<Integer, Country> Map;
    public WorldGraph() {
        this.Map = new HashMap<>();
    }
}