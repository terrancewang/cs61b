package enigma;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Terrance Wang
 */
class Machine {
    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    /** Number of available rotors.*/
    private int _numRotors;
    /** Number of moving rotors.*/
    private int _pawls;
    /** Collection that stores all available rotors.*/
    private Collection<Rotor> _allRotors;
    /** Collection of all used slots of an instance.*/
    private ArrayList<Rotor> _slots = new ArrayList<Rotor>();
    /** Stores plugboard cycles.*/
    private Permutation _plugboard;

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }
    /** Return the used rotors in machine. */
    Collection<Rotor> slots() {
        return _slots;
    }

    /** Return all rotors used in the machine.*/
    Collection<Rotor> getAllRotor() {
        return _allRotors;
    }
    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _slots = new ArrayList<>();
        for (String r: rotors) {
            for (Rotor a: _allRotors) {
                if (r.toUpperCase().equals(a.name().toUpperCase())) {
                    _slots.add(a);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        Iterator<Rotor> a = _slots.iterator();
        for (int i = 0; i < setting.length(); i++) {
            Rotor b = a.next();
            while (b.reflecting()) {
                b = a.next();
            }
            b.set(setting.charAt(i));
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
        if (_plugboard != null) {
            c = _plugboard.permute(c);
        }
        boolean nextMoving = true;
        for (int i = _slots.size() - _pawls; i < _slots.size(); i++) {
            if (i != (_slots.size() - 1)) {
                if (_slots.get(i + 1).atNotch()) {
                    _slots.get(i).advance();
                } else if (_slots.get(i).atNotch()
                        && i != _slots.size() - _pawls) {
                    _slots.get(i).advance();
                }
            } else {
                _slots.get(i).advance();
            }
        }
        Collections.reverse(_slots);
        for (Rotor a: _slots) {
            c = a.convertForward(c);
        }
        Collections.reverse(_slots);
        for (Rotor a: _slots) {
            if (!a.reflecting()) {
                c = a.convertBackward(c);
            }
        }
        if (_plugboard != null) {
            c = _plugboard.invert(c);
        }
        return c;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String a = "";
        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) != ' ') {
                int newChar = convert(_alphabet.toInt(msg.charAt(i)));
                a = a + Character.toString(_alphabet.toChar(newChar));
            }
        }
        return a;
    }
}
