import java.io.*;
import java.sql.SQLOutput;
import java.util.*;

public class fsminterpreter {

    private LinkedList<String> setOfStates = new LinkedList<>();
    private LinkedList<String> setOfInputs = new LinkedList<>();
    private Hashtable<String,String>[][] states;

    /**
     * The second index in the hashtables array corresponds to whether the hashtable is for mapping input to output, or
     * input to state. I made this static ints so that the code is easier to understand.
     */
    static int OUTPUT = 0;
    static int NEXT_STATE = 1;



    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java fsminterpreter <fsm> <<Input file>");
            System.exit(-1);
        }
        fsminterpreter FSM = new fsminterpreter();
        FSM.getStatesAndInputs(args[0]);
        FSM.makeFSM(args[0]);
        FSM.runFSM();
    }


    private void getStatesAndInputs(String stateTransitionTable) {
        int position = 0;
        try {
            FileReader fr = new FileReader(stateTransitionTable);
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
            System.exit(-1);
        }
    }
    /**
     * This method goes through the fsm provided and populates the hashtables with all the state transition information.
     * First it instantiates the array of hashtables. Then it goes through the fsm file. Position tells the scanner which
     * column it is looking at - 0 = state, 1 = input, 2 = output symbol, 3 = next state. At each position I complete a
     * different process.
     *
     * 0 - I set the current state to the current token.
     * 1 - set the current input to the current token
     * 2 - put the current token in the output hashtable as the value and the current input as the key
     * 3 - put the current token in the next state hashtable as the value and the current input as the key
     *
     * Because indices start from 0, if a state transition table says state 1, it will correspond to the index 1 less
     * i.e. the hashtable for input -> output of state 3 would be stored in index [2][0].
     * @param stateTransitionTable the filename of the fsm being used. Should be passed in from the commandline.
     */
    @SuppressWarnings("unchecked")
    private void makeFSM(String stateTransitionTable) {
        states = new Hashtable[setOfStates.size()][2];
        for (int i = 0; i < setOfStates.size(); i++) {
            states[i][0] = new Hashtable<>();
            states[i][1] = new Hashtable<>();
        }
        try {
            FileReader fr = new FileReader(stateTransitionTable);
            Scanner column = new Scanner(fr);
            String currentToken; //represents the current word taken by the scanner, it will usually be single character.
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
                    isDeterminative(currentState,currentInput);
                    states[currentState][NEXT_STATE].put(currentInput, currentToken);
                }
                position = (position + 1) % 4;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        checkMissingInput();
    }

    /**
     * This function goes through the hashtables currently made and checks that every state has an output for every input
     * from the set of states value. If there isn't a corresponding value for a particular allowed input, it informs the
     * user and the program exits.
     *
     * */
    private void checkMissingInput() {
        for (int i = 0; i < setOfStates.size();i++) {
            for (String s:setOfInputs) {
                if (states[i][OUTPUT].get(s) == null) {
                    System.out.println("Bad description");
                    System.exit(0);
                }
            }
        }
    }

    /**
     * This helper functions checks that the next state in the row of the fsm provided is in the set of valid states. If
     * it isn't it informs the user and exits the program.
     * @param state the state being examined.
     */
    private void validState(String state) {
        if (!setOfStates.contains(state)) {
            System.out.println("Bad description");
            System.exit(0);
        }
    }

    /**
     * This checks that there isn't already a next state mapping for a given input.
     * @param currentState the current state of the FSM
     * @param currentInput the current input being checked
     */
    private void isDeterminative (int currentState, String currentInput) {
        if (states[currentState][NEXT_STATE].get(currentInput) != null) {
            System.out.println("Bad description: non-determinative");
            System.exit(0);
        }
    }

    /**
     * This helper function checks that a particular character is in the list set of allowed inputs. It informs the user
     * and exits the program if it is not.
     * @param currentInput the input to be checked.
     */
    private void validInput(char currentInput) {
        String character = Character.toString(currentInput);
        if (!setOfInputs.contains(character)) {
            System.out.println("Bad input");
            System.exit(1);
        }
    }

    /**
     * This method gets the output corresponding to the current state and input.
     * @param currentState the current state of the FSM
     * @param currentInput the input given to the FSM
     * @return the output string
     */
    private String getOutput(int currentState, char currentInput) {
        String inputKey = Character.toString( currentInput);
        return states[currentState][OUTPUT].get(inputKey);
    }

    /**
     * This method gets the next state corresponding to the current state and input
     * @param currentState the current state of the FSM
     * @param currentInput the input given to the FSM
     * @return the next state.
     */
    private int getNextState(int currentState, char currentInput) {
        String nextStateString = states[currentState][NEXT_STATE].get(Character.toString(currentInput));
        return Integer.parseInt(nextStateString);
    }

    /**
     * This method takes in a input string from stdin(as provided in the commandline arguments) and iterates through
     * each character of the string, using each character as input for the FSM defined earlier.
     */
    private void runFSM() {
            Scanner reader = new Scanner(System.in);
            String input = reader.nextLine();
            int currentState = 0;
            for (int i = 0; i<input.length(); i++) {
                validInput(input.charAt(i));
                System.out.print(getOutput(currentState, input.charAt(i)));
                currentState = (getNextState(currentState, input.charAt(i))-1);
            }
            System.out.println();
    }

}
