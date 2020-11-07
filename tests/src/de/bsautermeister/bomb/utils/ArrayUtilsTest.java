package de.bsautermeister.bomb.utils;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ArrayUtilsTest {

    private static final boolean T = true;
    private static final boolean F = false;

    @Test
    public void fill2D() {
        boolean[][] data = new boolean[3][2];
        ArrayUtils.fill2D(data, true);

        for (boolean[] slice : data) {
            for (boolean value : slice) {
                assertEquals(true, value);
            }
        }
    }

    @Test
    public void copyToInt() {
        boolean[][] data = new boolean[][]{
                {T, F, T},
                {F, T, F}
        };
        int[][] expected = new int[][]{
                {1, 0, 1},
                {0, 1, 0}
        };

        int[][] actual = ArrayUtils.copyToInt(data, 1, 0);

        assertEquals(true, Arrays.deepEquals(expected, actual));
    }

    @Test
    public void replace2D() {
        int[][] data = new int[][] {
                {1, 2, 3, 4},
                {4, 3, 2, 1},
                {1, 1, 1, 1}
        };
        int[][] expected = new int[][] {
                {9, 2, 3, 4},
                {4, 3, 2, 9},
                {9, 9, 9, 9}
        };

        ArrayUtils.replace2D(data, 1, 9);

        assertEquals(true, Arrays.deepEquals(expected, data));
    }
}
