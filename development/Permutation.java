package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Kevin Li
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles += cycle;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int x = wrap(p);
        char convertChar = alphabet().toChar(x);
        return alphabet().toInt(permute(convertChar));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        int x = wrap(c);
        char convertChar = alphabet().toChar(x);
        return alphabet().toInt(invert(convertChar));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        String[] convert = processCycle(_cycles);
        String buffer = "";
        for (String i : convert) {
            if (i.contains(String.valueOf(p))) {
                buffer = i;
            }
        }
        if (buffer.isEmpty()) {
            return p;
        } else if (buffer.charAt(buffer.length() - 1) == p) {
            return buffer.charAt(0);
        } else {
            return buffer.charAt(buffer.indexOf(p) + 1);
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        String[] convert = processCycle(_cycles);
        String buffer = "";
        for (String i : convert) {
            if (i.contains(String.valueOf(c))) {
                buffer = i;
            }
        }
        if (buffer.isEmpty()) {
            return c;
        } else if (buffer.charAt(0) == c) {
            return buffer.charAt(buffer.length() - 1);
        } else {
            return buffer.charAt(buffer.indexOf(c) - 1);
        }
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }
    /** Getter method for cycles.
     * @return _cycles */
    String cycles() {
        return _cycles;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        String[] storeCycles = processCycle(_cycles);
        int cyclesLen = 0;
        for (String i : storeCycles) {
            if (i.length() == 1) {
                return false;
            } else {
                cyclesLen += i.length();
            }
        }
        return cyclesLen == alphabet().size();
    }

    /** Helper function: processCycle.
     * @param cycles which is the cycles of a Permutation.
     * @return result
     * */
    public static String[] processCycle(String cycles) {
        if (cycles.contains(" ")) {
            cycles = cycles.replaceAll(" ", "");
            String[] result = cycles.split("\\)\\(");
            result[0] = result[0].substring(1);
            int last = result.length - 1;
            result[last] = result[last].substring(0, result[last].length() - 1);
            return result;
        } else {
            cycles = cycles.replaceAll("\\(", "");
            cycles = cycles.replaceAll("\\)", "");
            String[] result = {cycles};
            return result;
        }
    }
    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /** Cycles of this permutation. */
    private String _cycles;
}
