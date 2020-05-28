package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Kevin Li
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME.
     * @param name vs _name instance variable. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME.
     * @param name vs _name instance variable. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine enig = readConfig();
        String intake;
        while (_input.hasNext()) {
            String nextLine = _input.nextLine();
            checkFirstLine(nextLine);
            if (!nextLine.isEmpty()) {
                intake = nextLine;
            } else {
                _output.println();
                intake = "";
            }
            while (_input.hasNext("\\*")) {
                intake += _input.nextLine();
            }
            setUp(enig, intake);
            while (!_input.hasNext("\\*") && _input.hasNextLine()) {
                String msg = _input.nextLine().replaceAll("\\s", "");
                if (msg.equals("")) {
                    _output.print("\n");
                } else {
                    printMessageLine(enig.convert(msg));
                }
            }
        }
    }
    /** Helper. Check if the first line is a valid setting with an asterick.
     *  isFirst is an instance variable initially assigned true.
     *  @param nextLine is the first line of input
     *  */
    private void checkFirstLine(String nextLine) {
        if (isFirst) {
            isFirst = false;
            if (!nextLine.contains("*")) {
                throw new EnigmaException("Need an * at the first line");
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = readAlphabet(_config.next());
            for (int i = 0; i < 2 && !_config.hasNextInt(); i += 1) {
                throw new EnigmaException("Wrong number of rotors or pawls");
            }
            int rotorNum = _config.nextInt();
            int pawlNum = _config.nextInt();
            _name = (_config.next()).toUpperCase();
            while (_config.hasNext()) {
                _name = (_placeHolder == null) ? _name : _placeHolder;
                _notches = (_config.next()).toUpperCase();
                rotorBuffer.add(readRotor());
            }
            return new Machine(_alphabet, rotorNum, pawlNum, rotorBuffer);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Helper verifyClause. */
    private void verifyClause() {
        if (_placeHolder.contains("(") && !_placeHolder.contains(")")) {
            throw new EnigmaException("Incomplete ()");
        }
    }
    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String cycles = "";
            _placeHolder = (_config.next()).toUpperCase();
            verifyClause();
            while (_placeHolder.contains("(") && _config.hasNext()) {
                cycles = cycles.concat(_placeHolder + " ");
                _placeHolder = (_config.next()).toUpperCase();
                verifyClause();
            }
            if (!_config.hasNext()) {
                cycles += cycles.concat(_placeHolder + " ");
            }
            char identifier = _notches.charAt(0);
            if (identifier == 'M') {
                return new MovingRotor(_name,
                        new Permutation(cycles, _alphabet),
                        _notches.substring(1));
            } else if (identifier == 'R') {
                return new Reflector(_name, new Permutation(cycles, _alphabet));
            } else if (identifier == 'N') {
                return new FixedRotor(_name,
                        new Permutation(cycles, _alphabet));
            } else {
                throw new EnigmaException("Invalid rotor identifier");
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] set = settings.split(" ");
        if (set.length - 1 < M.numRotors()) {
            throw new EnigmaException("Not enough arguments in setting");
        }
        if (countMovingRotor(M, set) > M.numPawls()) {
            throw new EnigmaException("Too many MovingRotors");
        }
        String[] rotors = new String[M.numRotors()];
        if (M.numRotors() >= 0) {
            System.arraycopy(set, 1, rotors, 0, M.numRotors());
        }
        checkRepeatRotors(rotors);
        if (1 + M.numRotors() >= set.length) {
            throw new EnigmaException("Setting message length does not match");
        }
        String verifySetting = set[1 + M.numRotors()];
        if (verifySetting.length() != M.numRotors() - 1) {
            throw new EnigmaException("Verify Setting length doesnt match");
        }
        String stecker = "";
        for (int i = 7; i < set.length; i++) {
            stecker = stecker.concat(set[i] + " ");
        }
        for (int i = 0; i < M.numRotors(); i++) {
            if (!M.rotorStore().containsKey((rotors[i]).toUpperCase())) {
                throw new EnigmaException("No such rotor exist");
            }
        }
        M.insertRotors(rotors);
        if (set[M.numRotors() + 1].length() != M.numRotors() - 1) {
            throw new EnigmaException("Wrong setting length");
        }
        char[] testSet = set[M.numRotors() + 1].toCharArray();
        for (char c : testSet) {
            if (!_alphabet.contains(c)) {
                throw new EnigmaException("Setting out of bounds");
            }
        }
        M.setRotors(set[M.numRotors() + 1]);
        M.setPlugboard(new Permutation(stecker, _alphabet));
    }
    /** Helper function to determine the # of moving rotors.
     * @param setting as a String array.
     * @param M as a Machine.
     * @return counter */
    private int countMovingRotor(Machine M, String[] setting) {
        int counter = 0;
        for (String i : setting) {
            Rotor a = M.rotorStore().get(i);
            if (a instanceof MovingRotor) {
                counter += 1;
            }
        }
        return counter;
    }
    /** Helper function.
     * @param rotors is for record rotor.
     * */
    private void checkRepeatRotors(String[] rotors) {
        for (int i = 0; i < rotors.length - 1; i++) {
            for (int j = i + 1; j < rotors.length; j++) {
                if (rotors[i].equals(rotors[j])) {
                    throw new EnigmaException("Rotors repeated");
                }
            }
        }
    }
    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg = msg.replaceAll(" ", "").toUpperCase();
        for (int i = 0; i < msg.length(); i += 5) {
            int rem = msg.length() - i;
            if (rem > 5) {
                _output.print(msg.substring(i, i + 5) + " ");
            } else {
                _output.println(msg.substring(i, i + rem));
            }
        }
    }
    /** Helper
     * 1. read Alphabet and nextValid
     *  @param alpha is short of alphabets
     *  @return new Alphabet(alpha)
     * */
    private Alphabet readAlphabet(String alpha) {
        if (alpha.contains("(") || alpha.contains(")") || alpha.contains("*")) {
            throw new EnigmaException("Wrong config in readAlphabet");
        }
        return new Alphabet(alpha);
    }

    /** Temp checker for isFirst. */
    private boolean isFirst = true;
    /** Process the name of rotors. */
    private String _name;
    /** Process the notches. */
    private String _notches;
    /** Placeholder to record the name of rotors. */
    private String _placeHolder;

    /** ArrayList to hold rotors. */
    private ArrayList<Rotor> rotorBuffer = new ArrayList<>();

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
