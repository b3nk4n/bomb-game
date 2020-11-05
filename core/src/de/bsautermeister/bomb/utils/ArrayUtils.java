package de.bsautermeister.bomb.utils;

import java.util.Arrays;

public class ArrayUtils {

    private ArrayUtils() { }

    public  static void fill2D(boolean[][] array, boolean value) {
        for (boolean[] slice : array) {
            Arrays.fill(slice, value);
        }
    }
}
