package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Terrance Wang
 */
class Permutation {
    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String _cycles;

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
    }

    /** Return all the cycles in permutation. */
    public String cycle() {
        return _cycles;
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    public void addCycle(String cycle) {
        _cycles = _cycles + " " + cycle;
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

    /** Given a char C, return the FORWARD/backward char in cycles. */
    char wrapCycle(char c, int forward) {
        int ind = _cycles.indexOf(c);
        if (ind == -1) {
            return c;
        } else {
            if (_cycles.charAt(ind + forward) == ')'
                    || _cycles.charAt(ind + forward) == '(') {
                int a = ind;
                while (_cycles.charAt(a) != '(' && _cycles.charAt(a) != ')') {
                    a -= forward;
                }
                return _cycles.charAt(a + forward);
            } else {
                return _cycles.charAt(ind + forward);
            }
        }
    }


    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char a = _alphabet.toChar(p);
        char newA = wrapCycle(a, 1);
        return _alphabet.toInt(newA);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char a = _alphabet.toChar(c);
        char newA = wrapCycle(a, -1);
        return _alphabet.toInt(newA);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return wrapCycle(p, 1);
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        char newC = wrapCycle(c, -1);
        return (int) newC;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        int ind = _cycles.indexOf('(') + 1;
        if (ind == 0) {
            return false;
        } else {
            int len = size();
            while (_cycles.charAt(ind) != ')') {
                len -= 1;
                ind += 1;
            }
            return len == 0;
        }
    }
}
