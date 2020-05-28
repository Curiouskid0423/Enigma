package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Kevin Li
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    /**
     * Return the result of applying this permutation to P modulo the
     * alphabet size.
     */
    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('A', p.permute('B'));
        assertEquals(3, p.permute(2));
        assertEquals(3, p.permute(6));
        assertEquals(4, p.size());
        assertTrue(p.derangement());
    }
    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals(4, p.size());
        assertEquals(0, p.invert(6));
        assertEquals(0, p.invert(-2));
    }


    @Test
    public void testSINGLE() {
        Permutation p = new Permutation("(SINGL)", new Alphabet("SINGLE"));
        assertEquals('S', p.invert('I'));
        assertEquals('E', p.invert('E'));
    }
    @Test
    public void testDerangement() {
        Permutation perm1 = new Permutation("(A) (BC)", new Alphabet("ABC"));
        Permutation perm2 = new Permutation("(EN) (KVI)",
                new Alphabet("KEVIN"));
        assertFalse(perm1.derangement());
        assertTrue(perm2.derangement());
    }
    @Test
    public void testProcessCycle() {
        String cycles = "(ABCDE) (GH) (F)";
        String[] expected = {"ABCDE", "GH", "F"};
        assertArrayEquals(expected, Permutation.processCycle(cycles));
    }
}
