package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.utils.ArrayUtils;
import de.bsautermeister.bomb.utils.ClusterUtils;
import de.bsautermeister.bomb.utils.result.ClusterResult;

public class FragmentData {

    private final float x;
    private final float y;
    private final float size;
    private final float delta;
    private final boolean[][] gridData;

    public FragmentData(int resolution, float x, float y, float size) {
        this(x, y, size, createFilledArray(resolution, true));
    }

    private static boolean[][] createFilledArray(int resolution, boolean value) {
        boolean[][] data = new boolean[resolution][resolution];
        ArrayUtils.fill2D(data, value);
        return data;
    }

    public FragmentData(float x, float y, float size, boolean[][] gridData) {
        if (gridData.length > 0 && gridData[0].length > 0 && gridData.length != gridData[0].length) {
            throw new IllegalArgumentException("Grid data have square shape.");
        }

        this.x = x;
        this.y = y;
        this.size = size;
        this.delta = this.size / (gridData.length - 1);
        this.gridData = gridData; // not needed here to copy the array
    }

    public boolean remove(Circle circle) {
        boolean updated = false;
        for (int i = 0; i < gridData.length; ++i) {
            for (int j = 0; j < gridData[i].length; ++j) {
                float x = getNodeX(i);
                float y = getNodeY(j);
                if (circle.contains(x, y)) {
                    gridData[i][j] = false;
                    updated = true;
                }
            }
        }
        return updated;
    }

    /**
     * Computes the outlines clock-wise, which can result either convex or concave polygons.
     */
    public Array<float[]> computeOutlines() {
        ClusterResult clusterResult = ClusterUtils.computeClusters(gridData);

        Array<float[]> result = new Array<>();
        for (int clusterIdx = 0; clusterIdx < clusterResult.getCount(); ++clusterIdx) {
            GridPoint2 startPosition = clusterResult.getStartPosition(clusterIdx);
            int[][] clusterData = clusterResult.getData();

            Array<GridPoint2> gridPoints = ClusterUtils.computeClusterOutline(clusterData, clusterIdx, startPosition);
            result.add(toPolygonArray(gridPoints));
        }

        return result;
    }

    private float[] toPolygonArray(Array<GridPoint2> gridPoints) {
        float[] polygonData = new float[gridPoints.size * 2];
        int i = 0;
        for (GridPoint2 gridPoint : gridPoints) {
            polygonData[i++] = getNodeX(gridPoint.x);
            polygonData[i++] = getNodeY(gridPoint.y);
        }
        return polygonData;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSize() {
        return size;
    }

    public float getNodeX(int i) {
        return x + i * delta;
    }

    public float getNodeY(int j) {
        return y + j * delta;
    }

    public boolean[][] getGridData() {
        return gridData;
    }

    public float getDelta() {
        return delta;
    }

    public int getResolution() {
        return gridData.length;
    }
}
