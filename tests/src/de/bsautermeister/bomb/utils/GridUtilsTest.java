package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GridUtilsTest {

    private static final int N = -1;

    @Test
    public void isInBoundsWhenInBounds() {
        int[][] data = new int[][] {
                {N, N, N},
                {N, N, N}
        };

        assertTrue(GridUtils.isInBounds(data, 0, 0));
        assertTrue(GridUtils.isInBounds(data, 1, 2));
        assertTrue(GridUtils.isInBounds(data, 1, 1));
    }

    @Test
    public void isInBoundsWhenOutOfBounds() {
        int[][] data = new int[][] {
                {N, N, N},
                {N, N, N}
        };

        assertFalse(GridUtils.isInBounds(data, -1, 0));
        assertFalse(GridUtils.isInBounds(data, 1, -1));
        assertFalse(GridUtils.isInBounds(data, 2, 0));
        assertFalse(GridUtils.isInBounds(data, 1, 3));
    }

    @Test
    public void countNeighborsInside() {
        final int V = 1;
        int[][] data = new int[][] {
                {N, N, V, N},
                {N, V, V, N},
                {N, N, 2, V},
                {V, N, N, N},
        };

        assertEquals(3, GridUtils.countNeighbors(data, 1, 2, V));
    }

    @Test
    public void countNeighborsInCorner() {
        final int V = 1;
        int[][] data = new int[][] {
                {N, N, V, V},
                {N, V, 2, N},
                {N, N, N, V},
                {N, N, N, N},
        };

        assertEquals(1, GridUtils.countNeighbors(data, 0, 3, V));
    }

    @Test
    public void getNextPosCW8() {
        assertEquals(new GridPoint2(1, -1), GridUtils.getNextPosCWInner8(1, 0, 0, 0));
        assertEquals(new GridPoint2(0, -1), GridUtils.getNextPosCWInner8(1, -1, 0, 0));
        assertEquals(new GridPoint2(-1, -1), GridUtils.getNextPosCWInner8(0, -1, 0, 0));
        assertEquals(new GridPoint2(-1, 0), GridUtils.getNextPosCWInner8(-1, -1, 0, 0));
        assertEquals(new GridPoint2(-1, 1), GridUtils.getNextPosCWInner8(-1, 0, 0, 0));
        assertEquals(new GridPoint2(0, 1), GridUtils.getNextPosCWInner8(-1, 1, 0, 0));
        assertEquals(new GridPoint2(1, 1), GridUtils.getNextPosCWInner8(0, 1, 0, 0));
        assertEquals(new GridPoint2(1, 0), GridUtils.getNextPosCWInner8(1, 1, 0, 0));
    }

    @Test
    public void getNextPosCCW8() {
        assertEquals(new GridPoint2(1, 1), GridUtils.getNextPosCCWInner8(1, 0, 0, 0));
        assertEquals(new GridPoint2(1, 0), GridUtils.getNextPosCCWInner8(1, -1, 0, 0));
        assertEquals(new GridPoint2(1, -1), GridUtils.getNextPosCCWInner8(0, -1, 0, 0));
        assertEquals(new GridPoint2(0, -1), GridUtils.getNextPosCCWInner8(-1, -1, 0, 0));
        assertEquals(new GridPoint2(-1, -1), GridUtils.getNextPosCCWInner8(-1, 0, 0, 0));
        assertEquals(new GridPoint2(-1, 0), GridUtils.getNextPosCCWInner8(-1, 1, 0, 0));
        assertEquals(new GridPoint2(-1, 1), GridUtils.getNextPosCCWInner8(0, 1, 0, 0));
        assertEquals(new GridPoint2(0, 1), GridUtils.getNextPosCCWInner8(1, 1, 0, 0));
    }

    @Test
    public void getNextPosCW8Circle() {
        GridPoint2 start = new GridPoint2(0, 1);
        GridPoint2 prev = new GridPoint2(start);
        GridPoint2 center = new GridPoint2();
        for (int i = 0; i < 8; ++i) {
            GridPoint2 next = GridUtils.getNextPosCWInner8(prev.x, prev.y, center.x, center.y);
            prev.set(next);
        }
        assertEquals(start, prev);
    }

    @Test
    public void getNextPosCCW8Circle() {
        GridPoint2 start = new GridPoint2(0, 1);
        GridPoint2 prev = new GridPoint2(start);
        GridPoint2 center = new GridPoint2();
        for (int i = 0; i < 8; ++i) {
            GridPoint2 next = GridUtils.getNextPosCCWInner8(prev.x, prev.y, center.x, center.y);
            prev.set(next);
        }
        assertEquals(start, prev);
    }

    @Test
    public void getNextPosCW16Circle() {
        GridPoint2 start = new GridPoint2(0, 1);
        GridPoint2 prev = new GridPoint2(start);
        GridPoint2 center = new GridPoint2();
        for (int i = 0; i < 16; ++i) {
            GridPoint2 next = GridUtils.getNextPosCWInner16(prev.x, prev.y, center.x, center.y);
            prev.set(next);
        }
        assertEquals(start, prev);
    }

    @Test
    public void getNextPosCCW16Circle() {
        GridPoint2 start = new GridPoint2(0, 1);
        GridPoint2 prev = new GridPoint2(start);
        GridPoint2 center = new GridPoint2();
        for (int i = 0; i < 16; ++i) {
            GridPoint2 next = GridUtils.getNextPosCCWInner16(prev.x, prev.y, center.x, center.y);
            prev.set(next);
        }
        assertEquals(start, prev);
    }

    @Test
    public void getNextPosCWOuter12Circle() {
        GridPoint2 start = new GridPoint2(0, 2);
        GridPoint2 prev = new GridPoint2(start);
        GridPoint2 center = new GridPoint2();
        for (int i = 0; i < 12; ++i) {
            GridPoint2 next = GridUtils.getNextPosCWOuter12(prev.x, prev.y, center.x, center.y);
            prev.set(next);
        }
        assertEquals(start, prev);
    }

    @Test
    public void hasConnectibleNeighborsLeftOrRight() {
        int[][] data = new int[][] {
                {N, 0, 0, N, N},
                {0, N, 0, N, 0},
                {1, N, 1, N, 0},
                {1, N, 1, 1, N},
                {N, N, 1, 1, N},
        };

        assertFalse(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 2, 0, 1, 0, N));
        assertFalse(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 0, 1, 1, 0, N));
        assertFalse(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 2, 2, 1, 2, N));
        assertFalse(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 1, 2, 2, 2, N));

        assertTrue(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 3, 2, 2, 2, N));
        assertTrue(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 2, 2, 3, 2, N));
        assertTrue(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 2, 2, 3, 3, N));
        assertTrue(GridUtils.hasConnectibleNeighborsLeftOrRight(data, 3, 3, 2, 2, N));
    }
}
