package de.bsautermeister.bomb.utils.result;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

public class ClusterResult {
    private final Array<GridPoint2> startPositions;
    private final int[][] data;

    public ClusterResult(Array<GridPoint2> startPositions, int[][] clusterData) {
        this.startPositions = startPositions;
        this.data = clusterData;
    }

    public GridPoint2 getStartPosition(int clusterIdx) {
        return startPositions.get(clusterIdx);
    }

    public int getCount() {
        return startPositions.size;
    }

    public int[][] getData() {
        return data;
    }
}
