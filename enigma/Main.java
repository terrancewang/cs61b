package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Terrance Wang
 */
public final class Main {
    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

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

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
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
    public void process() {
        Machine m = readConfig();
        boolean isSetUp = false;
        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            if (line.length() == 0) {
                printMessageLine("");
            } else if (line.charAt(0) == '*') {
                setUp(m, line);
                isSetUp = true;
            } else if (!isSetUp) {
                throw error("input file incorrectly formatted");
            } else {
                String res = m.convert(line.toUpperCase());
                printMessageLine(res);
            }
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        Scanner s = new Scanner(settings);
        String start = s.next();
        String[] names = new String[M.numRotors()];
        int count = M.numPawls();
        for (int i = 0; i < M.numRotors(); i++) {
            String rotorName = s.next().toUpperCase();
            boolean matches = false;
            for (Rotor a: M.getAllRotor()) {
                if (rotorName.equals(a.name().toUpperCase())) {
                    if (a.rotates()) {
                        count -= 1;
                    }
                    matches = true;
                }
            }
            if (matches) {
                names[i] = rotorName;
            } else {
                throw error("Rotor not in machine");
            }
        }
        M.insertRotors(names);
        String set = s.next();
        if (set.length() != M.numRotors() - 1 || count < 0) {
            throw error("Wrong number of arguments");
        }
        M.setRotors(set);
        String plugboard = "";
        while (s.hasNext()) {
            plugboard += s.next();
        }
        M.setPlugboard(new Permutation(plugboard, _alphabet));
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphaParser = _config.next();
            if (alphaParser.length() == 3 && alphaParser.charAt(1) == '-') {
                _alphabet = new CharacterRange(alphaParser.charAt(0),
                        alphaParser.charAt(2));
            } else if (alphaParser.length() > 1
                    && alphaParser.charAt(1) != '-') {
                _alphabet = new ExtendedCharacterRange(alphaParser);
            } else {
                throw error("incorrectly formatted config file");
            }
            _config.useDelimiter("\\s+");
            int numRotors = Integer.parseInt(_config.next());
            int numPawls = Integer.parseInt(_config.next());
            ArrayList<Rotor> allRotors = new ArrayList<Rotor>();
            String rotorName = _config.next().toUpperCase();
            while (_config.hasNext()) {
                String rotorSetting = _config.next();
                String cycle = "";
                String temp = _config.next();
                while (temp.charAt(0) == '(' && _config.hasNext()) {
                    if (temp.charAt(temp.length() - 1) != ')') {
                        throw error("incorrect config setting, "
                                + "no closing parenthesis");
                    }
                    cycle += temp;
                    temp = _config.next();
                }
                if (!_config.hasNext()) {
                    cycle += temp;
                }
                Permutation p = new Permutation(cycle, _alphabet);
                if (rotorSetting.charAt(0) == 'M') {
                    Rotor r = new MovingRotor(rotorName, p,
                            rotorSetting.substring(1));
                    allRotors.add(r);
                } else if (rotorSetting.charAt(0) == 'N') {
                    Rotor r = new FixedRotor(rotorName, p);
                    allRotors.add(r);
                } else if (rotorSetting.charAt(0) == 'R') {
                    Rotor r = new Reflector(rotorName, p);
                    allRotors.add(r);
                } else {
                    throw error("misnamed rotor");
                }
                rotorName = temp;
            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        } catch (NumberFormatException excp) {
            throw error("missing number of rotors and pawls");
        }
    }

    /** Return a rotor, reading its description from _config. */
    /** IMPLEMENT THIS IF STUFF DOESNT WORK */
    /**private Rotor readRotor() {
        try {

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    } */


    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        for (int i = 0; i < msg.length(); i++) {
            if (i != 0 && (i % 5) == 0) {
                _output.append(' ');

            }
            _output.append(msg.charAt(i));
        }
        _output.append("\r\n");
    }
}
