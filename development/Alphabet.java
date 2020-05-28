package enigma;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Kevin Li
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        int charLength = chars.length();
        this._store = chars;
        this._storeArr = new char[charLength];
        for (int i = 0; i < charLength; i++) {
            this._storeArr[i] = _store.charAt(i);
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _store.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _store.contains(String.valueOf(ch));
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (0 <= index && index < this.size()) {
            return this._storeArr[index];
        } else {
            throw new EnigmaException("toChar index out of bound");
        }
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!this.contains(ch)) {
            throw new EnigmaException("Character doesn't exist");
        } else {
            return this._store.indexOf(ch);
        }
    }
    /** String store variable. */
    private String _store;
    /** Char[] store variable. */
    private char[] _storeArr;
}
