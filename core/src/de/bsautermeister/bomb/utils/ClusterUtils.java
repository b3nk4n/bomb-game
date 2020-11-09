package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.utils.result.ClusterResult;

public class ClusterUtils {

    public static final int EMPTY = -2;
    public static final int DEFINED = -1;

    private ClusterUtils() {}

    public static ClusterResult computeClusters(boolean[][] gridData) {
        int[][] clusterData = ArrayUtils.copyToInt(gridData, DEFINED, EMPTY);
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

    public static Array<GridPoint2> computeClusterOutline(int[][] clusterData, int clusterIdx, GridPoint2 startPosition) {
        Array<GridPoint2> result = new Array<>();
        result.add(new GridPoint2(startPosition));
        System.out.println("start added " + startPosition.x + " " + startPosition.y);

        int currentI = startPosition.x;
        int currentJ = startPosition.y;
        int nextI = startPosition.x;
        int nextJ = startPosition.y - 1;
        boolean open = true;
        while (open) {
            for (int i = 0; i < 7; ++i) {
                GridPoint2 next = GridUtils.getNextPosCW(nextI, nextJ, currentI, currentJ);
                nextI = next.x;
                nextJ = next.y;

                if (nextI == startPosition.x && nextJ == startPosition.y) {
                    // outline is closed: stop connecting the dots
                    open = false;
                    break;
                }

                if (GridUtils.isInBounds(clusterData, nextI, nextJ) && clusterData[nextI][nextJ] == clusterIdx) {
                    System.out.println("added " + nextI + " " + nextJ);
                    result.add(new GridPoint2(nextI, nextJ));
                    int tmpI = currentI;
                    int tmpJ = currentJ;
                    currentI = nextI;
                    currentJ = nextJ;
                    nextI = tmpI;
                    nextJ = tmpJ;
                    break;
                }
            }
        }

        return result;
    }
}
