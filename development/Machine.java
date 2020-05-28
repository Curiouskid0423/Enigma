package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Kevin Li
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (numRotors <= 1) {
            throw new EnigmaException("Rotor number has to be > 1");
        } else if (pawls <= 0 || pawls > numRotors) {
            throw new EnigmaException("Invalid number of pawls");
        }
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _activeRotors = new Rotor[numRotors];
        _rotorStore = new HashMap<String, Rotor>();
        for (Rotor i : _allRotors) {
            _rotorStore.put(i.name(), i);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }
    /** Helper function containsKey.
     * @param name is the name of the rotor
     *  @return boolean _rotorStore.containsKey(name).
     * */
    boolean containsKey(String name) {
        return _rotorStore.containsKey(name);
    }
    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int len = rotors.length;
        for (int i = 0; i < len; i++) {
            String key = rotors[i].toUpperCase();
            if (_rotorStore.containsKey(key)) {
                if (i == 0 && !_rotorStore.get(key).reflecting()) {
                    throw new EnigmaException("Leftmost should be Reflector");
                }
                _activeRotors[i] = _rotorStore.get(key);
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        int len = _activeRotors.length;
        if (setting.length() != len - 1) {
            throw new EnigmaException("Fewer number of settings than expected");
        }
        for (int i = 1; i < len; i++) {
            _activeRotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        boolean[] advanceable = new boolean[_activeRotors.length];
        advanceable[_activeRotors.length - 1] = true;
        for (int i = advanceable.length - 1; i > 1; i--) {
            if (_activeRotors[i].atNotch() && _activeRotors[i - 1].rotates()) {
                advanceable[i] = true;
                advanceable[i - 1] = true;
            }
        }
        for (int j = 0; j < _activeRotors.length; j++) {
            if (advanceable[j]) {
                _activeRotors[j].advance();
            }
        }
        int input = _plugboard.permute(c);
        for (int i = _numRotors - 1; i > 0; i -= 1) {
            input = _activeRotors[i].convertForward(input);
        }
        int output = _activeRotors[0].convertForward(input);
        for (int j = 1; j < _numRotors; j += 1) {
            output = _activeRotors[j].convertBackward(output);
        }
        return _plugboard.permute(output);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly.
     *  1. Forward prop: Char in -> plugboard -> enter motor (advance setting
     *  if the motor is movable -> char + setti@Rule
     *     public Timeout globalTimeout = Timeout.seconds(5);ng -> permute
     *  -> char - setting -> final output
     *  2. Reflector: reflects
     *  3. Back prop: Same processes as forward prop
     *  4. Plugboard
     *
     *  @param msg stands for message
     *  @return buffer
     *  */
    String convert(String msg) {
        msg = msg.toUpperCase();
        String buffer = "";
        int len = msg.length();
        for (int i = 0; i < len; i += 1) {
            int index = _alphabet.toInt(msg.charAt(i));
            char convertedChar = _alphabet.toChar(convert(index));
            buffer += String.valueOf(convertedChar);
        }
        return buffer;
    }

    /** Helper for Hashmap Getter.
     * @return _rotorStore */
    public HashMap<String, Rotor> rotorStore() {
        return _rotorStore;
    }
    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** The number of rotors. */
    private int _numRotors;
    /** The number of pawls. */
    private int _pawls;
    /** Instance variable for plugboards. */
    private Permutation _plugboard;
    /** Collection of all rotors. */
    private Collection<Rotor> _allRotors;
    /** Currently active running rotors. */
    private Rotor[] _activeRotors;
    /** Hashmap for rotors. */
    private HashMap<String, Rotor> _rotorStore;
}
