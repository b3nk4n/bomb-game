package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.utils.ArrayUtils;

public class FragmentData {

    private final float x;
    private final float y;
    private final float size;
    private final float delta;
    private final boolean[][] data;

    public FragmentData(int resolution, float x, float y, float size) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.delta = (resolution - 1) / this.size;

        this.data = new boolean[resolution][resolution];
        ArrayUtils.fill2D(data, true);
    }

    public boolean remove(Circle circle) {
        boolean updated = false;
        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                float x = getNodeX(i);
                float y = getNodeY(j);
                if (circle.contains(x, y)) {
                    data[i][j] = false;
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
        int[][] clusterData = ArrayUtils.copyToInt(data, 0, -1);
        boolean[][] dataCopy = data.clone();

        for (int i = 0; i < data.length; ++i) {
            for (int j = 0; j < data[i].length; ++j) {
                if (!dataCopy[i][j]) {
                    continue;
                }

                // TODO implement
            }
        }

        Array<float[]> result = new Array<>();
        return result;
    }

    public float getNodeX(int i) {
        return x + i * delta;
    }

    public float getNodeY(int j) {
        return y + j * delta;
    }

    public boolean[][] getData() {
        return data;
    }

    public float getDelta() {
        return delta;
    }

    public int getResolution() {
        return data.length;
    }
}
