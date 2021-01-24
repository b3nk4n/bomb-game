package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import org.junit.Test;

import java.util.Arrays;

import de.bsautermeister.bomb.utils.result.ClusterResult;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
		assertTrue(Arrays.deepEquals(expected, result.getData()));
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
		assertTrue(Arrays.deepEquals(expected, result.getData()));
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
		assertTrue(Arrays.deepEquals(expected, result.getData()));
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
		assertTrue(Arrays.deepEquals(expected, result.getData()));
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
		assertTrue(Arrays.deepEquals(expected, result.getData()));
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
		assertTrue(Arrays.deepEquals(expected, result.getData()));
	}

	@Test
	public void computeClusterOutline() {
		int[][] clusterData = new int[][]{
				{N, N, N, N, 0, 0},
				{N, N, 1, 1, N, 0},
				{N, N, 1, 1, N, N},
				{N, 2, N, N, 3, N},
				{2, 2, N, N, 3, 3},
				{2, N, N, 3, 3, 3},
				{N, N, N, N, N, 3},
		};

		int clusterIdx = 0;
		GridPoint2 startPosition = new GridPoint2(0, 4);
		Array<GridPoint2> result = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertEquals(3, result.size);
		assertArrayEquals(new GridPoint2[]{
						new GridPoint2(0,4),
						new GridPoint2(0,5),
						new GridPoint2(1, 5)
				},
				result.toArray());

		clusterIdx = 1;
		startPosition = new GridPoint2(1, 2);
		result = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertEquals(4, result.size);
		assertArrayEquals(new GridPoint2[]{
						new GridPoint2(1,2),
						new GridPoint2(1,3),
						new GridPoint2(2, 3),
						new GridPoint2(2, 2)
				},
				result.toArray());

		clusterIdx = 2;
		startPosition = new GridPoint2(3, 1);
		result = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertEquals(4, result.size);
		assertArrayEquals(new GridPoint2[]{
						new GridPoint2(3,1),
						new GridPoint2(4,1),
						new GridPoint2(5, 0),
						new GridPoint2(4, 0)
				},
				result.toArray());

		clusterIdx = 3;
		startPosition = new GridPoint2(3, 4);
		result = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertEquals(4, result.size);
		assertArrayEquals(new GridPoint2[]{
						new GridPoint2(3,4),
						new GridPoint2(4,5),
						new GridPoint2(6, 5),
						new GridPoint2(5, 3)
				},
				result.toArray());
	}

	@Test
	public void computeClusterOutlineFromDataOneMissing() {
		boolean[][] data = new boolean[][]{
				{T, T, T, T, T},
				{T, T, T, T, T},
				{T, T, T, T, T},
				{T, T, T, T, F},
				{T, T, T, T, T},
		};
		int[][] clusterData = new int[][]{
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, 0},
		};

		ClusterResult clusterResult = ClusterUtils.computeClusters(data);

		assertEquals(1, clusterResult.getCount());

		GridPoint2 startPosition = new GridPoint2(0, 0);
		assertEquals(startPosition, clusterResult.getStartPosition(0));
		assertTrue(Arrays.deepEquals(clusterData, clusterResult.getData()));

		int clusterIdx = 0;
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertNotNull(outlineResult);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(0, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(2, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(3, 3), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutlineFromDataTwoMissing() {
		boolean[][] data = new boolean[][]{
				{T, T, T, T, T},
				{T, T, T, T, T},
				{T, T, T, T, F},
				{T, T, T, T, F},
				{T, T, T, T, T},
		};
		int[][] clusterData = new int[][]{
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, 0},
		};

		ClusterResult clusterResult = ClusterUtils.computeClusters(data);

		assertEquals(1, clusterResult.getCount());

		GridPoint2 startPosition = new GridPoint2(0, 0);
		assertEquals(startPosition, clusterResult.getStartPosition(0));
		assertTrue(Arrays.deepEquals(clusterData, clusterResult.getData()));

		int clusterIdx = 0;
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertNotNull(outlineResult);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(0, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(1, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(3, 3), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutlineFromDataThreeMissing() {
		boolean[][] data = new boolean[][]{
				{T, T, T, T, T},
				{T, T, T, T, F},
				{T, T, T, T, F},
				{T, T, T, T, F},
				{T, T, T, T, T},
		};
		int[][] clusterData = new int[][]{
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, 0},
		};

		ClusterResult clusterResult = ClusterUtils.computeClusters(data);

		assertEquals(1, clusterResult.getCount());

		GridPoint2 startPosition = new GridPoint2(0, 0);
		assertEquals(startPosition, clusterResult.getStartPosition(0));
		assertTrue(Arrays.deepEquals(clusterData, clusterResult.getData()));

		int clusterIdx = 0;
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		assertNotNull(outlineResult);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(0, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(2, 3), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutline5x5A() {
		int[][] clusterData = new int[][]{
				{1, 1, 1, N, N},
				{1, 1, N, N, N},
				{1, 1, N, N, N},
				{1, N, N, N, N},
				{1, N, N, N, N},
		};

		int clusterIdx = 1;
		GridPoint2 startPosition = new GridPoint2(0, 0);
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(0, 2), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutline5x5B() {
		int[][] clusterData = new int[][]{
				{1, 1, 1, N, N},
				{1, 1, N, N, N},
				{1, 1, N, N, N},
				{1, N, N, N, N},
				{N, N, N, N, N},
		};

		int clusterIdx = 1;
		GridPoint2 startPosition = new GridPoint2(0, 0);
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(0, 2), outlineResult.get(idx++));
		assertEquals(new GridPoint2(2, 1), outlineResult.get(idx++));
		assertEquals(new GridPoint2(3, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutline5x5C() {
		int[][] clusterData = new int[][]{
				{N, N, N, N, N},
				{N, N, 0, N, N},
				{0, 0, 0, 0, N},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
		};

		int clusterIdx = 0;
		GridPoint2 startPosition = new GridPoint2(2, 0);
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(1, 2), outlineResult.get(idx++));
		assertEquals(new GridPoint2(3, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutline5x5D() {
		int[][] clusterData = new int[][]{
				{N, N, N, N, N},
				{N, N, N, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
				{0, 0, 0, 0, 0},
		};

		int clusterIdx = 0;
		GridPoint2 startPosition = new GridPoint2(2, 0);
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(2, 1), outlineResult.get(idx++));
		assertEquals(new GridPoint2(1, 3), outlineResult.get(idx++));
		assertEquals(new GridPoint2(1, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 0), outlineResult.get(idx++));
	}

	@Test
	public void computeClusterOutline5x5E() {
		int[][] clusterData = new int[][]{
				{N, N, N, N, N},
				{N, N, N, 3, N},
				{N, N, N, 3, 3},
				{N, N, 3, 3, 3},
				{N, N, N, N, 3},
		};

		int clusterIdx = 3;
		GridPoint2 startPosition = new GridPoint2(3, 2);
		Array<GridPoint2> outlineResult = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);

		int idx = 0;
		assertEquals(startPosition, outlineResult.get(idx++));
		assertEquals(new GridPoint2(1, 3), outlineResult.get(idx++));
		assertEquals(new GridPoint2(2, 4), outlineResult.get(idx++));
		assertEquals(new GridPoint2(4, 4), outlineResult.get(idx++));
	}
}
