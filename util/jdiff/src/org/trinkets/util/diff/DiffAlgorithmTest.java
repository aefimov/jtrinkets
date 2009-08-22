package org.trinkets.util.diff;

import junit.framework.TestCase;

/**
 * Diff algorithm tests.
 *
 * @author Alexey Efimov
 */
public class DiffAlgorithmTest extends TestCase {
    public void testLcs() {
        Character[] x = Strings.toArray("XMJYAUZ".toCharArray());
        Character[] y = Strings.toArray("MZJAWXU".toCharArray());
        int[][] c = DiffAlgorithm.lcs(
            new DiffAlgorithm.ArrayRange<Character>(x, 0, x.length),
            new DiffAlgorithm.ArrayRange<Character>(y, 0, y.length)
        );
        StringBuilder builder = new StringBuilder();
        for (int[] cx : c) {
            for (int cy : cx) {
                builder.append(cy);
                builder.append(' ');
            }
        }
        assertEquals("0 0 0 0 0 0 0 0 " +
            "0 0 0 0 0 0 1 1 " +
            "0 1 1 1 1 1 1 1 " +
            "0 1 1 2 2 2 2 2 " +
            "0 1 1 2 2 2 2 2 " +
            "0 1 1 2 3 3 3 3 " +
            "0 1 1 2 3 3 3 4 " +
            "0 1 2 2 3 3 3 4 ", builder.toString());
    }

    public void testCompareChars() {
        DiffNode[] nodes = DiffAlgorithm.compare(Strings.toArray("true"), Strings.toArray("false")).toArray();
        assertEquals(3, nodes.length);
        assertEquals("R:0,3->A:0,3", nodes[0].toString());
        assertEquals("V:3,0->A:0,1", nodes[1].toString());
        assertEquals("U:3,1->U:3,1", nodes[2].toString());
    }
}