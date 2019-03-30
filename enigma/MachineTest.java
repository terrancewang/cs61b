package enigma;
import org.junit.Test;
import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;
import static enigma.TestUtils.*;


public class MachineTest {
    private Collection<Rotor> _allRotors = new ArrayList<Rotor>() { };

    private void setRotor(String[] name, HashMap<String, String> rotors,
                          String notches) {
        for (String a: name) {
            Rotor rotor = new MovingRotor(a,
                    new Permutation(rotors.get(a), UPPER),
                    notches);
            _allRotors.add(rotor);
        }
    }

    @Test
    public void numRotorsTest() {
        String[] a = {"I", "II", "III", "IV", "V"};
        setRotor(a, NAVALA, "");
        Machine m = new Machine(UPPER, 3, 2, _allRotors);
        assertEquals(3, m.numRotors());
    }

    @Test
    public void insertRotorsTest() {
        String[] a = {"I", "II", "III", "IV", "V"};
        setRotor(a, NAVALA, "");
        Machine m = new Machine(UPPER, 3, 2, _allRotors);
        String[] b = {"II", "III", "IV"};
        m.insertRotors(b);
        Object[] col = m.slots().toArray();
        assertEquals(NAVALA.get("II"), ((Rotor) col[0]).permutation().cycle());
        assertEquals(NAVALA.get("III"), ((Rotor) col[1]).permutation().cycle());
        assertEquals(NAVALA.get("IV"), ((Rotor) col[2]).permutation().cycle());

    }

    @Test
    public void convertTest() {
        String[] a = {"I", "II", "III", "IV", "V"};
        setRotor(a, NAVALA, "");
        Rotor rotor = new Reflector("B",
                new Permutation(NAVALA.get("B"), UPPER));
        _allRotors.add(rotor);
        Machine m1 = new Machine(UPPER, 1, 1, _allRotors);
        String[] b1 = {"I"};
        m1.insertRotors(b1);
        int c = m1.convert(0);
        assertEquals(0, c);

        Machine m2 = new Machine(UPPER, 2, 1, _allRotors);
        String[] b2 = {"B", "I"};
        m2.insertRotors(b2);
        String str = "A";
        str = m2.convert(str);
        assertEquals("Y", str);

        for (Rotor aa: m2.slots()) {
            aa.set(0);
        }
        String str2 = "AA";
        str2 = m2.convert(str2);
        assertEquals("YY", str2);


        Machine m3 = new Machine(UPPER, 3, 2, _allRotors);
        String[] b3 = {"B", "II", "I"};
        m3.insertRotors(b3);
        String str3 = "A";
        for (Rotor aaa: m3.slots()) {
            aaa.set(0);
        }
        str3 = m3.convert(str3);
        assertEquals("Q", str3);
    }

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);

        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AABC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AACD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABDA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABDB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABDC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABAD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABAB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABAC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABBD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABBA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABBB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABBC", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ABCD", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ACDA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("ACDB", getSetting(ac, machineRotors));
        mach.convert('a');
    }

    /** Helper method to get the String representation
     *  of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }

    @Test
    public void slotsTester() {
        String[] a = {"I", "II", "III", "IV", "V"};
        setRotor(a, NAVALA, "");
        Rotor rotor = new Reflector("B",
                new Permutation(NAVALA.get("B"), UPPER));
        _allRotors.add(rotor);
        Machine m1 = new Machine(UPPER, 1, 1, _allRotors);
        String[] b3 = {"I", "II", "B"};
        m1.insertRotors(b3);
        for (Rotor r: m1.slots()) {
            System.out.println(r.name());
        }
    }
}
