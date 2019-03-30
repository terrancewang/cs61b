package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Terrance Wang
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation permu;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, permu.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, permu.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, permu.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, permu.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, permu.invert(ei));
        }
    }

    /* ***** TESTS ***** */
    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(PNH) (ABDFIKLZYXW) (JC)",
                new CharacterRange('A', 'Z'));
        assertEquals(p.invert('B'), 'A');
        assertEquals(p.invert('G'), 'G');
    }

    @Test
    public void checkIdTransform() {
        permu = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkPermute() {
        Permutation perm = new Permutation("(ABCD) (EFGH)", UPPER);
        char permed =  perm.permute('A');
        assertEquals('B', permed);
        permed = perm.permute(permed);
        assertEquals('C', permed);
        permed = perm.permute(permed);
        permed = perm.permute(permed);
        assertEquals('A', permed);
        permed = (char) perm.invert(permed);
        assertEquals('D', permed);
    }
    @Test
    public void checkDerangement() {
        Permutation perm =
                new Permutation("(ABDCEFGHIJKLMNOPQRSXTUVWYZ)", UPPER);
        Permutation perm1 = new Permutation("()", UPPER);
        assertEquals(true, perm.derangement());
        assertEquals(false, perm1.derangement());
    }

}
