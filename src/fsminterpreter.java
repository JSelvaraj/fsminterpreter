import java.io.*;
import java.lang.reflect.Array;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

public class fsminterpreter {

    static LinkedList<String> setOfStates = new LinkedList<>();
    static LinkedList<String> setOfInputs = new LinkedList<>();
    static Hashtable<String,String>[][] states;

    public static void main(String args[]) {
        getStatesAndInputs();
        makeStates();
        makeHashtables();





        printSetOfStates();
        printSetOfInputs();

    }

    private static void getStatesAndInputs() {
        int position = 0;
        try {
            FileReader fr = new FileReader("fsm.txt");
            Scanner column = new Scanner(fr);
            String currentToken;
            while (column.hasNext()) {
                currentToken = column.next();
                if (position == 0) {
                    if (!setOfStates.contains(currentToken)) {
                        setOfStates.add(currentToken);
                    }
                } else if (position == 1) {
                    if (!setOfInputs.contains(currentToken)) {
                        setOfInputs.add(currentToken);
                    }
                }
                position = (position + 1) % 4;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void makeStates() {
        states = new Hashtable[setOfStates.size()][2];
        for (int i = 0; i< setOfStates.size();i++) {
            states[i][0] = new Hashtable<>();
            states[i][1] = new Hashtable<>();
        }
    }
    private static void makeHashtables() {
        try {
            FileReader fr = new FileReader("fsm.txt");
            Scanner column = new Scanner(fr);
            String currentToken;
            int currentState = 0;
            int position = 0;
            String currentInput = "";
            while (column.hasNext()) {
                currentToken = column.next();
                if (position == 0) {
                    currentState = Integer.parseInt(currentToken) - 1;
                } else if (position == 1) {
                    currentInput = currentToken;
                } else if (position == 2) {
                    states[currentState][0].put(currentInput,currentToken);
                } else if (position == 3) {
                    states[currentState][1].put(currentInput,currentToken);
                }
                position = (position + 1) % 4;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }




    private static void printSetOfStates() {
        for (String s : setOfStates) {
            System.out.print(s + " ");
        }
        System.out.println();
    }
    private static void printSetOfInputs() {
        for (String s : setOfInputs) {
            System.out.print(s + " ");
        }
        System.out.println();
    }


}
