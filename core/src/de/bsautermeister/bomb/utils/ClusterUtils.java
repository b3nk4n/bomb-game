package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;
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
        for (int k = 0; k < 8 - 1; ++k) {
            GridPoint2 next = GridUtils.getNextPosCWInner8(outerI, outerJ, i, j);
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

        int currentI = startPosition.x;
        int currentJ = startPosition.y;
        int nextI = startPosition.x;
        int nextJ = startPosition.y - 1;
        int lastDiffI = nextI - currentI;
        int lastDiffJ = nextJ - currentJ;

        boolean open = true;
        while (open) {
            int circleStartI = nextI;
            int circleStartJ = nextJ;

            for (int i = 0; i < 8 - 1; ++i) { // TODO does this loop make sense? Or should we BREAK after this loop anyways, otherwise we would try the same thing over and over again!? At least when we reach the end of this loop.
                GridPoint2 next = GridUtils.getNextPosCWInner8(nextI, nextJ, currentI, currentJ);
                nextI = next.x;
                nextJ = next.y;

                if (nextI == startPosition.x && nextJ == startPosition.y) {
                    // outline is closed: stop connecting the dots
                    open = false;

                    int diffI = nextI - currentI;
                    int diffJ = nextJ - currentJ;
                    if (diffI == lastDiffI && diffJ == lastDiffJ) {
                        result.removeIndex(result.size - 1);
                    }

                    return result;
                }

                if (GridUtils.isInBounds(clusterData, nextI, nextJ) && clusterData[nextI][nextJ] == clusterIdx) {
                    // check whether there would have been a 22.5° angle as well
                    next = GridUtils.getNextPosCCWInner16(nextI, nextJ, currentI, currentJ);

                    int shortCutNextI = next.x;
                    int shortCutNextJ = next.y;
                    boolean needToCorrectNext = false;

                    if (GridUtils.isInBounds(clusterData, shortCutNextI, shortCutNextJ) && clusterData[nextI][nextJ] == clusterIdx) {
                        // use shortcut if we would have hit the same target using the outer circle

                        // skip the first two
                        next = GridUtils.getNextPosCWOuter16(circleStartI, circleStartJ, currentI, currentJ);
                        next = GridUtils.getNextPosCWOuter16(next.x, next.y, currentI, currentJ);

                        for (int k = 2; k < 16 - 1; ++k) {
                            next = GridUtils.getNextPosCWOuter16(next.x, next.y, currentI, currentJ);

                            if (GridUtils.isInBounds(clusterData, next.x, next.y) && clusterData[next.x][next.y] == clusterIdx) {
                                if (next.x == shortCutNextI && next.y == shortCutNextJ) {

                                    if (shortCutNextI == startPosition.x && shortCutNextJ == startPosition.y) {
                                        // outline is closed: stop connecting the dots
                                        open = false;

                                        int diffI = shortCutNextI - currentI;
                                        int diffJ = shortCutNextJ - currentJ;
                                        if (diffI == lastDiffI && diffJ == lastDiffJ) {
                                            result.removeIndex(result.size - 1);
                                        }

                                        return result;
                                    }

                                    nextI = shortCutNextI;
                                    nextJ = shortCutNextJ;
                                    needToCorrectNext = true;
                                }
                                break;
                            }
                        }
                    }

                    int diffI = nextI - currentI;
                    int diffJ = nextJ - currentJ;
                    if (diffI == lastDiffI && diffJ == lastDiffJ) {
                        result.removeIndex(result.size - 1);
                    }
                    lastDiffI = diffI;
                    lastDiffJ = diffJ;

                    result.add(new GridPoint2(nextI, nextJ));
                    int tmpI = currentI;
                    int tmpJ = currentJ;
                    currentI = nextI;
                    currentJ = nextJ;
                    nextI = tmpI;
                    nextJ = tmpJ;

                    if (needToCorrectNext) {
                        // we did one step on inner-16, which we need to repeat to be on a inner-8 position again
                        next = GridUtils.getNextPosCCWInner16(nextI, nextJ, currentI, currentJ);
                        nextI = next.x;
                        nextJ = next.y;
                    }

                    break;
                }
            }
        }

        return result;
    }
}
