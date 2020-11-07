package de.bsautermeister.bomb.utils;

import java.util.Arrays;

public class ArrayUtils {

    private ArrayUtils() { }

    public static void fill2D(boolean[][] array, boolean value) {
        for (boolean[] slice : array) {
            Arrays.fill(slice, value);
        }
    }

    public static int[][] copyToInt(boolean[][] array, int trueValue, int falseValue) {
        int[][] result = new int[array.length][array[0].length];
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[0].length; ++j) {
                result[i][j] = array[i][j] ? trueValue : falseValue;
            }
        }
        return result;
    }

    public static void replace2D(int[][] array, int replace, int value) {
        for (int[] slice : array) {
            for (int i = 0; i < slice.length; ++i) {
                if (slice[i] == replace) {
                    slice[i] = value;
                }
            }
        }
    }
}
