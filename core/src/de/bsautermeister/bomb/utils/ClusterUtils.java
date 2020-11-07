package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.utils.result.ClusterResult;

public class ClusterUtils {

    public static final int EMPTY = -2;
    public static final int DEFINED = -1;

    private ClusterUtils() {}

    public static ClusterResult computeClusters(boolean[][] data) {
        int[][] clusterData = ArrayUtils.copyToInt(data, DEFINED, EMPTY);
        Array<GridPoint2> clusterStartPositions = new Array<>();

        int clusterId = 0;
        for (int i = 0; i < clusterData.length; ++i) {
            for (int j = 0; j < clusterData[0].length; ++j) {
                if (clusterData[i][j] == DEFINED) {
                    if (markClusterDepthFirst(clusterData, clusterId, i, j, i, j - 1)) {
                        clusterStartPositions.add(new GridPoint2(i, j));
                        clusterId++;
                    }
                }
            }
        }

        // cleanup remaining grid points
        ArrayUtils.replace2D(clusterData, DEFINED, EMPTY);

        return new ClusterResult(clusterStartPositions, clusterData);
    }

    private static boolean markClusterDepthFirst(int[][] clusterData, int clusterId, int i, int j, int prevI, int prevJ) {
        boolean marked = false;

        if (clusterData[i][j] == clusterId) {
            return false;
        }

        int outerI = prevI;
        int outerJ = prevJ;
        for (int k = 0; k < 7; ++k) {
            GridPoint2 next = GridUtils.getNextPosCW(outerI, outerJ, i, j);
            outerI = next.x;
            outerJ = next.y;

            if (GridUtils.isInBounds(clusterData, outerI, outerJ) && clusterData[outerI][outerJ] != EMPTY) {
                if (GridUtils.hasConnectibleNeighborsLeftOrRight(clusterData, outerI, outerJ, i, j, EMPTY)) {
                    marked = true;
                    clusterData[i][j] = clusterId;
                    if (clusterData[outerI][outerJ] != clusterId) {
                        markClusterDepthFirst(clusterData, clusterId, outerI, outerJ, i, j);
                    }
                }
            }
        }

        return marked;
    }
}
