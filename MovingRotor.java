package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Kevin Li
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }
    /** Getter method for _notches.
     *  @return _notches */
    private String notches() {
        return _notches;
    }
    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        char posNow = alphabet().toChar(setting());
        if (notches().isEmpty()) {
            return false;
        } else if (notches().contains(String.valueOf(posNow))) {
            return true;
        }
        return false;
    }

    @Override
    void advance() {
        super.set(permutation().wrap(this.setting() + 1));
    }
    /** Instance variable. */
    private String _notches;

}
