import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class StateDictionary {
    /*
    Stores "entry" type objects
    Entries have the ID (key), country code, and country names, and possible aliases
    */
    private class Entry {
        private String code; // 3 letter abbreviation
        private String name; // name that appears on state_name.tsv
        private List<String> aliases;
        public Entry(String code, String mainName) {
            this.code = code;
            this.name = mainName;
            this.aliases = new ArrayList<>();
        }

        public void add_alias(String newAlias) {
            if (newAlias.compareTo(name) == 0) {
                return;
            }
            for (int i = 0; i < aliases.size(); i++) {
                if (newAlias.compareTo(aliases.get(i)) == 0) {
                    return;
                }
            }
            aliases.add(newAlias);
        }

        public String get_code() {
            return code;
        }
        public List<String> get_aliases() {
            return aliases;
        }
    }

    private HashMap<Integer, Entry> dictionary;

    public StateDictionary() {
        this.dictionary = new HashMap<>();
    }
    public void add_Entry(int id, String code, String tsvName) {
        Entry newEntry = new Entry(code, tsvName);
        dictionary.put(id, newEntry);
    }
}
