package enigma;

/** Alphabet consisting of values in the string _VALS.*/
/** @author Terrance Wang */
public class ExtendedCharacterRange extends Alphabet {

    /** Stores the values in the alphabet.*/
    private String _vals;

    /** Construct and alphabet with values in VALS.*/
    ExtendedCharacterRange(String vals) {
        _vals = vals;
    }

    /** Returns the size of your alphabet.*/
    int size() {
        return _vals.length();
    }

    /** Returns whether the alphabet has CH.*/
    boolean contains(char ch) {
        return _vals.contains(Character.toString(ch));
    }

    /** Returns the char at index IND.*/
    char toChar(int ind) {
        return _vals.charAt(ind);
    }

    /** Returns the index of the char CH.*/
    int toInt(char ch) {
        return _vals.indexOf(Character.toString(ch));
    }
}
