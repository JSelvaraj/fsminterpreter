import java.io.*;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

public class fsminterpreter {

    static LinkedList<String> setOfStates = new LinkedList<>();
    static LinkedList<String> setOfInputs = new LinkedList<>();
    static Hashtable<String, String>[][] states;
    static String FILE_NAME = "bigger.fsm";
    static int OUTPUT = 0;
    static int NEXT_STATE = 1;
    static String INPUT_FILE = "input.txt";


    public static void main(String args[]) {
        fsminterpreter FSM = new fsminterpreter();
        FSM.getStatesAndInputs();
        FSM.makeStates();
        FSM.makeHashtables();
        FSM.runFSM();


//        printSetOfStates();
//        printSetOfInputs();

    }

    private void getStatesAndInputs() {
        int position = 0;
        try {
            FileReader fr = new FileReader(INPUT_FILE);
            Scanner column = new Scanner(System.in);
            String currentToken = column.next();
            while (column.hasNext()) {
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
                currentToken = column.next();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void makeStates() {
        states = new Hashtable[setOfStates.size()][2];
        for (int i = 0; i < setOfStates.size(); i++) {
            states[i][OUTPUT] = new Hashtable<>();
            states[i][NEXT_STATE] = new Hashtable<>();
        }
    }

    private void makeHashtables() {
        try {
            FileReader fr = new FileReader(FILE_NAME);
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
                    states[currentState][OUTPUT].put(currentInput, currentToken);
                } else if (position == 3) {
                    validState(currentToken);
                    states[currentState][NEXT_STATE].put(currentInput, currentToken);
                }
                position = (position + 1) % 4;
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private boolean validState(String state) {
        if (!setOfStates.contains(state)) {
            System.out.println("Bad description");
            System.exit(0);
        }
        return true;
    }

    private void runFSM() {
        try {
            FileReader fr = new FileReader(INPUT_FILE);
            BufferedReader reader = new BufferedReader(fr);
            int currentState = 0;
            int currentInput = reader.read();
            while (currentInput != -1 && Character.isLetterOrDigit((char) currentInput)) {
                validInput(currentInput);
                System.out.print(getOutput(currentState, currentInput));
                currentState = getNextState(currentState, currentInput) - 1;
                currentInput = reader.read();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOutput(int currentState, int currentInput) {
        String inputKey = Character.toString((char) currentInput);
        return states[currentState][OUTPUT].get(inputKey);
    }

    private int getNextState(int currentState, int currentInput) {
        String nextStateString = states[currentState][NEXT_STATE].get(Character.toString((char) currentInput));
        return Integer.parseInt(nextStateString);


    }

    private void validInput(int currentinput) {
        String character = Character.toString((char) currentinput);
        if (!setOfInputs.contains(character)) {
            System.out.println();
            System.out.println("Bad input");
            System.exit(1);
        }
    }


    private void printSetOfStates() {
        for (String s : setOfStates) {
            System.out.print(s + " ");
        }
        System.out.println();
    }

    private void printSetOfInputs() {
        for (String s : setOfInputs) {
            System.out.print(s + " ");
        }
        System.out.println();
    }


}
