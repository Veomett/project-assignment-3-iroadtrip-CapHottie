import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class IRoadTrip {

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

    }

    /*
    * return -1 if invalid pair
    */
    public int getDistance (String country1, String country2) {
        // Replace with your code
        return -1;
    }

    /*
    * return list of countries through which to travel to get to country 2
    * return EMPTY list if impossible, not NULL
    */
    public List<String> findPath (String country1, String country2) {
        // Replace with your code
        return null;
    }


    public void acceptUserInput() {
        // Replace with your code
        System.out.println("IRoadTrip - skeleton");
    }

    //in order, main receives borders.txt, capdist.csv, and state_names.tsv
    public static void main(String[] args) {
        IRoadTrip a3 = new IRoadTrip(args);

        a3.acceptUserInput();
    }

}

