package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Terrance Wang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */

    private String _notches;

    /** Constructs MovingRotor with NAME, PERM, and NOTCHES.*/
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return true;
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        char current = permutation().alphabet().toChar(setting());
        return _notches.indexOf(current) != -1;
    }

    /** Returns notches.*/
    public String getNotches() {
        return _notches;
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }
}
