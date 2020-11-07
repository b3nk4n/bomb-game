package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;

import org.junit.Test;

import java.util.Arrays;

import de.bsautermeister.bomb.utils.result.ClusterResult;

import static org.junit.Assert.assertEquals;

public class ClusterUtilsTest {

    private static final boolean T = true;
    private static final boolean F = false;
    private static final int N = ClusterUtils.EMPTY;

	@Test
	public void computeClustersForInitialGrid() {
		boolean[][] data = new boolean[][]{
				{T, T, T, T, T},
				{T, T, T, T, T},
				{T, T, T, T, T},
				{T, T, T, T, T},
		};
		int[][] expected = new int[][]{
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
		};

		ClusterResult result = ClusterUtils.computeClusters(data);

		assertEquals(1, result.getCount());
		assertEquals(new GridPoint2(0, 0), result.getStartPosition(0));
		assertEquals(true, Arrays.deepEquals(expected, result.getData()));
	}

    @Test
    public void computeClustersWithClearSeparation() {
        boolean[][] data = new boolean[][]{
                {T, T, F, F, F},
                {T, T, F, F, F},
                {T, F, F, F, T},
                {F, F, F, T, T},
        };
        int[][] expected = new int[][]{
                {0, 0, N, N, N},
                {0, 0, N, N, N},
                {0, N, N, N, 1},
                {N, N, N, 1, 1},
        };

        ClusterResult result = ClusterUtils.computeClusters(data);

        assertEquals(2, result.getCount());
        assertEquals(new GridPoint2(0, 0), result.getStartPosition(0));
		assertEquals(new GridPoint2(2, 4), result.getStartPosition(1));
        assertEquals(true, Arrays.deepEquals(expected, result.getData()));
    }

	@Test
	public void computeClustersWithOutliersToBeRemoved() {
		boolean[][] data = new boolean[][]{
				{T, T, T, T, F, F},
				{T, T, F, F, F, T},
				{T, F, F, F, T, F},
				{F, F, F, T, T, F},
		};
		int[][] expected = new int[][]{
				{0, 0, 0, N, N, N},
				{0, 0, N, N, N, N},
				{0, N, N, N, 1, N},
				{N, N, N, 1, 1, N},
		};

		ClusterResult result = ClusterUtils.computeClusters(data);

		assertEquals(2, result.getCount());
		assertEquals(new GridPoint2(0, 0), result.getStartPosition(0));
		assertEquals(new GridPoint2(2, 4), result.getStartPosition(1));
		assertEquals(true, Arrays.deepEquals(expected, result.getData()));
	}

	@Test
	public void computeClustersWithHorizontalVerticalConnectionsToBeRemoved() {
		boolean[][] data = new boolean[][]{
				{F, T, T, T, T, T},
				{T, T, F, F, T, T},
				{T, F, F, F, F, T},
				{T, F, F, F, F, T},
				{T, T, F, F, T, T},
		};
		int[][] expected = new int[][]{
				{N, 0, 0, 1, 1, 1},
				{0, 0, N, N, 1, 1},
				{0, N, N, N, N, 1},
				{2, N, N, N, N, 3},
				{2, 2, N, N, 3, 3},
		};

		ClusterResult result = ClusterUtils.computeClusters(data);

		assertEquals(4, result.getCount());
		assertEquals(new GridPoint2(0, 1), result.getStartPosition(0));
		assertEquals(new GridPoint2(0, 3), result.getStartPosition(1));
		assertEquals(new GridPoint2(3, 0), result.getStartPosition(2));
		assertEquals(new GridPoint2(3, 5), result.getStartPosition(3));
		assertEquals(true, Arrays.deepEquals(expected, result.getData()));
	}

	@Test
	public void computeClustersWithWithDiagonalConnectionsToBeRemoved() {
		boolean[][] data = new boolean[][]{
				{F, F, F, F, T, T},
				{F, F, T, T, F, T},
				{F, F, T, T, F, F},
				{F, T, F, F, T, F},
				{T, T, F, F, T, T},
		};
		int[][] expected = new int[][]{
				{N, N, N, N, 0, 0},
				{N, N, 1, 1, N, 0},
				{N, N, 1, 1, N, N},
				{N, 2, N, N, 3, N},
				{2, 2, N, N, 3, 3},
		};

		ClusterResult result = ClusterUtils.computeClusters(data);

		assertEquals(4, result.getCount());
		assertEquals(new GridPoint2(0, 4), result.getStartPosition(0));
		assertEquals(new GridPoint2(1, 2), result.getStartPosition(1));
		assertEquals(new GridPoint2(3, 1), result.getStartPosition(2));
		assertEquals(new GridPoint2(3, 4), result.getStartPosition(3));
		assertEquals(true, Arrays.deepEquals(expected, result.getData()));
	}

	@Test
	public void computeClustersWithNoiseOnlyToBeRemoved() {
		boolean[][] data = new boolean[][]{
				{F, T, F, F, T, F},
				{T, F, F, T, F, T},
				{F, F, F, T, F, T},
				{F, T, F, F, F, F},
				{F, F, F, F, T, T},
		};
		int[][] expected = new int[][]{
				{N, N, N, N, N, N},
				{N, N, N, N, N, N},
				{N, N, N, N, N, N},
				{N, N, N, N, N, N},
				{N, N, N, N, N, N},
		};

		ClusterResult result = ClusterUtils.computeClusters(data);

		assertEquals(0, result.getCount());
		assertEquals(true, Arrays.deepEquals(expected, result.getData()));
	}

}
