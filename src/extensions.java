import java.io.*;
import java.sql.SQLOutput;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;

public class extensions {

    private LinkedList<String> setOfStates = new LinkedList<>();
    private LinkedList<String> setOfInputs = new LinkedList<>();
    private Hashtable<String,String>[][] states;






    static int OUTPUT = 0;
    static int NEXT_STATE = 1;



    public static void main(String args[]) {
        extensions FSM = new extensions();
        FSM.getStatesAndInputs(args[0]);
        FSM.makeFSM(args[0]);
        FSM.addMissingInput();
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
                    isDeterminative(currentState,currentInput);
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
    private void validInput(int currentInput) {
        String character = Character.toString((char) currentInput);
        if (!setOfInputs.contains(character)) {
            System.out.println("Bad input");
            System.exit(1);
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
    private void runFSM() {
        Scanner reader = new Scanner(System.in);
        String input = reader.nextLine();
        int currentState = 0;
        for (int i = 0; i<input.length(); i++) {
            validInput(input.charAt(i));
            System.out.print(getOutput(currentState,input.charAt(i)));
            currentState = (getNextState(currentState,input.charAt(i))-1);
        }
        System.out.println();
    }

    private void isDeterminative (int currentState, String currentInput) {
        if (states[currentState][NEXT_STATE].get(currentInput) != null) {
            System.out.println("Bad description: non-determinative");
            System.exit(0);
        }
    }

    private void addMissingInput() {
        for (int i = 0; i < setOfStates.size();i++) {
            for (String s:setOfInputs) {
                if (states[i][OUTPUT].get(s) == null) {
                    states[i][OUTPUT].put(s," ");
                    states[i][NEXT_STATE].put(s, Integer.toString(i+1));
                }
            }
        }
    }
}
