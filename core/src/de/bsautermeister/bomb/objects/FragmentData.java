package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.utils.ArrayUtils;
import de.bsautermeister.bomb.utils.ClusterUtils;
import de.bsautermeister.bomb.utils.result.ClusterResult;

public class FragmentData {

    private final float size;
    private final float delta;
    private final boolean[][] gridData;

    public FragmentData(int resolution, float size) {
        this(size, createInitialArray(resolution));
    }

    private static boolean[][] createInitialArray(int resolution) {
        boolean[][] data = new boolean[resolution][resolution];
        ArrayUtils.fill2D(data, true);
        return data;
    }

    public FragmentData(float size, boolean[][] gridData) {
        if (gridData.length > 0 && gridData[0].length > 0 && gridData.length != gridData[0].length) {
            throw new IllegalArgumentException("Grid data have square shape.");
        }

        this.size = size;
        this.delta = this.size / (gridData.length - 1);
        this.gridData = gridData; // not needed here to copy the array
    }

    public boolean remove(Circle circle) {
        boolean updated = false;
        for (int i = 0; i < gridData.length; ++i) {
            for (int j = 0; j < gridData[i].length; ++j) {
                float x = getRelativeX(i);
                float y = getRelativeY(j);
                if (gridData[i][j] && circle.contains(x, y)) {
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
            polygonData[i++] = getRelativeX(gridPoint.x);
            polygonData[i++] = getRelativeY(gridPoint.y);
        }
        return polygonData;
    }

    public float getSize() {
        return size;
    }

    public float getRelativeX(int i) {
        return i * delta;
    }

    public float getRelativeY(int j) {
        return j * delta;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (boolean[] gridDatum : gridData) {
            for (int j = 0; j < gridData[0].length; ++j) {
                sb.append(gridDatum[j] ? 'X' : '-');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public static class KryoSerializer extends Serializer<FragmentData> {
        @Override
        public void write(Kryo kryo, Output output, FragmentData object) {
            output.writeFloat(object.getSize());
            kryo.writeObject(output, object.getGridData());
        }

        @Override
        public FragmentData read(Kryo kryo, Input input, Class<? extends FragmentData> type) {
            return new FragmentData(
                    input.readFloat(),
                    kryo.readObject(input, boolean[][].class));
        }
    }
}
