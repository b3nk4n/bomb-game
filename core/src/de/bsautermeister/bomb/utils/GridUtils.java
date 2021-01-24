package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.GridPoint2;

public class GridUtils {

    private GridUtils() {}

    private static final GridPoint2 tmpCWInner16 = new GridPoint2();
    public static GridPoint2 getNextPosCWInner16(int i, int j, int centerI, int centerJ) {
        int diffI = centerI - i;
        int diffJ = centerJ - j;
        if (diffI == 0 && diffJ == 1) tmpCWInner16.set(centerI - 1, centerJ - 2);
        if (diffI == 1 && diffJ == 2) tmpCWInner16.set(centerI - 1, centerJ - 1);
        if (diffI == 1 && diffJ == 1) tmpCWInner16.set(centerI - 2, centerJ - 1);
        if (diffI == 2 && diffJ == 1) tmpCWInner16.set(centerI - 1, centerJ);
        if (diffI == 1 && diffJ == 0) tmpCWInner16.set(centerI - 2, centerJ + 1);
        if (diffI == 2 && diffJ == -1) tmpCWInner16.set(centerI - 1, centerJ + 1);
        if (diffI == 1 && diffJ == -1) tmpCWInner16.set(centerI - 1, centerJ + 2);
        if (diffI == 1 && diffJ == -2) tmpCWInner16.set(centerI, centerJ + 1);
        if (diffI == 0 && diffJ == -1) tmpCWInner16.set(centerI + 1, centerJ + 2);
        if (diffI == -1 && diffJ == -2) tmpCWInner16.set(centerI + 1, centerJ + 1);
        if (diffI == -1 && diffJ == -1) tmpCWInner16.set(centerI + 2, centerJ + 1);
        if (diffI == -2 && diffJ == -1) tmpCWInner16.set(centerI + 1, centerJ);
        if (diffI == -1 && diffJ == 0) tmpCWInner16.set(centerI + 2, centerJ - 1);
        if (diffI == -2 && diffJ == 1) tmpCWInner16.set(centerI + 1, centerJ - 1);
        if (diffI == -1 && diffJ == 1) tmpCWInner16.set(centerI + 1, centerJ - 2);
        if (diffI == -1 && diffJ == 2) tmpCWInner16.set(centerI, centerJ - 1);
        return tmpCWInner16;
    }

    private static final GridPoint2 tmpCCWInner16 = new GridPoint2();
    public static GridPoint2 getNextPosCCWInner16(int i, int j, int centerI, int centerJ) {
        int diffI = centerI - i;
        int diffJ = centerJ - j;
        if (diffI == 0 && diffJ == 1) tmpCCWInner16.set(centerI + 1, centerJ - 2);
        if (diffI == -1 && diffJ == 2) tmpCCWInner16.set(centerI + 1, centerJ - 1);
        if (diffI == -1 && diffJ == 1) tmpCCWInner16.set(centerI + 2, centerJ - 1);
        if (diffI == -2 && diffJ == 1) tmpCCWInner16.set(centerI + 1, centerJ);
        if (diffI == -1 && diffJ == 0) tmpCCWInner16.set(centerI + 2, centerJ + 1);
        if (diffI == -2 && diffJ == -1) tmpCCWInner16.set(centerI + 1, centerJ + 1);
        if (diffI == -1 && diffJ == -1) tmpCCWInner16.set(centerI + 1, centerJ + 2);
        if (diffI == -1 && diffJ == -2) tmpCCWInner16.set(centerI, centerJ + 1);
        if (diffI == 0 && diffJ == -1) tmpCCWInner16.set(centerI - 1, centerJ + 2);
        if (diffI == 1 && diffJ == -2) tmpCCWInner16.set(centerI - 1, centerJ + 1);
        if (diffI == 1 && diffJ == -1) tmpCCWInner16.set(centerI - 2, centerJ + 1);
        if (diffI == 2 && diffJ == -1) tmpCCWInner16.set(centerI - 1, centerJ);
        if (diffI == 1 && diffJ == 0) tmpCCWInner16.set(centerI - 2, centerJ - 1);
        if (diffI == 2 && diffJ == 1) tmpCCWInner16.set(centerI - 1, centerJ - 1);
        if (diffI == 1 && diffJ == 1) tmpCCWInner16.set(centerI - 1, centerJ - 2);
        if (diffI == 1 && diffJ == 2) tmpCCWInner16.set(centerI, centerJ - 1);
        return tmpCCWInner16;
    }

    private static final GridPoint2 tmpCWInner8 = new GridPoint2();
    public static GridPoint2 getNextPosCWInner8(int i, int j, int centerI, int centerJ) {
        int diffI = centerI - i;
        int diffJ = centerJ - j;
        if (diffI == 0 && diffJ == 1) tmpCWInner8.set(centerI - 1, centerJ - 1);
        if (diffI == 1 && diffJ == 1) tmpCWInner8.set(centerI - 1, centerJ);
        if (diffI == 1 && diffJ == 0) tmpCWInner8.set(centerI - 1, centerJ + 1);
        if (diffI == 1 && diffJ == -1) tmpCWInner8.set(centerI, centerJ + 1);
        if (diffI == 0 && diffJ == -1) tmpCWInner8.set(centerI + 1, centerJ + 1);
        if (diffI == -1 && diffJ == -1) tmpCWInner8.set(centerI + 1, centerJ);
        if (diffI == -1 && diffJ == 0) tmpCWInner8.set(centerI + 1, centerJ - 1);
        if (diffI == -1 && diffJ == 1) tmpCWInner8.set(centerI, centerJ - 1);
        return tmpCWInner8;
    }

    private static final GridPoint2 tmpCCWInner8 = new GridPoint2();
    public static GridPoint2 getNextPosCCWInner8(int i, int j, int centerI, int centerJ) {
        int diffI = centerI - i;
        int diffJ = centerJ - j;
        if (diffI == 0 && diffJ == 1) tmpCCWInner8.set(centerI + 1, centerJ - 1);
        if (diffI == 1 && diffJ == 1) tmpCCWInner8.set(centerI, centerJ - 1);
        if (diffI == 1 && diffJ == 0) tmpCCWInner8.set(centerI - 1, centerJ - 1);
        if (diffI == 1 && diffJ == -1) tmpCCWInner8.set(centerI - 1, centerJ);
        if (diffI == 0 && diffJ == -1) tmpCCWInner8.set(centerI - 1, centerJ + 1);
        if (diffI == -1 && diffJ == -1) tmpCCWInner8.set(centerI, centerJ + 1);
        if (diffI == -1 && diffJ == 0) tmpCCWInner8.set(centerI + 1, centerJ + 1);
        if (diffI == -1 && diffJ == 1) tmpCCWInner8.set(centerI + 1, centerJ);
        return tmpCCWInner8;
    }

    private static final GridPoint2 tmpCWOuter16 = new GridPoint2();
    public static GridPoint2 getNextPosCWOuter16(int i, int j, int centerI, int centerJ) {
        int diffI = centerI - i;
        int diffJ = centerJ - j;
        if (diffI == 0 && diffJ >= 1) tmpCWOuter16.set(centerI - 1, centerJ - 2);
        if (diffI == 1 && diffJ == 2) tmpCWOuter16.set(centerI - 2, centerJ - 2);
        if (diffI == 2 && diffJ == 2) tmpCWOuter16.set(centerI - 2, centerJ - 1);
        if (diffI == 2 && diffJ == 1) tmpCWOuter16.set(centerI - 2, centerJ);
        if (diffI >= 1 && diffJ == 0) tmpCWOuter16.set(centerI - 2, centerJ + 1);
        if (diffI == 2 && diffJ == -1) tmpCWOuter16.set(centerI - 2, centerJ + 2);
        if (diffI == 2 && diffJ == -2) tmpCWOuter16.set(centerI - 1, centerJ + 2);
        if (diffI == 1 && diffJ == -2) tmpCWOuter16.set(centerI, centerJ + 2);
        if (diffI == 0 && diffJ <= -1) tmpCWOuter16.set(centerI + 1, centerJ + 2);
        if (diffI == -1 && diffJ == -2) tmpCWOuter16.set(centerI + 2, centerJ + 2);
        if (diffI == -2 && diffJ == -2) tmpCWOuter16.set(centerI + 2, centerJ + 1);
        if (diffI == -2 && diffJ == -1) tmpCWOuter16.set(centerI + 2, centerJ);
        if (diffI <= -1 && diffJ == 0) tmpCWOuter16.set(centerI + 2, centerJ - 1);
        if (diffI == -2 && diffJ == 1) tmpCWOuter16.set(centerI + 2, centerJ - 2);
        if (diffI == -2 && diffJ == 2) tmpCWOuter16.set(centerI + 1, centerJ - 2);
        if (diffI == -1 && diffJ == 2) tmpCWOuter16.set(centerI, centerJ - 2);
        return tmpCWOuter16;
    }

    /**
     * Checks whether the node on i,j from prevI, prevJ would be connectable to any other neighbor.
     */
    public static boolean hasConnectibleNeighborsLeftOrRight(int[][] data, int i, int j, int prevI, int prevJ, int emptyValue) {
        GridPoint2 leftPos = getNextPosCCWInner8(i, j, prevI, prevJ);
        if (isInBounds(data, leftPos) &&  data[leftPos.x][leftPos.y] != emptyValue) {
            return true;
        }
        GridPoint2 rightPos = getNextPosCWInner8(i, j, prevI, prevJ);
        if (isInBounds(data, rightPos) && data[rightPos.x][rightPos.y] != emptyValue) {
            return true;
        }
        int diffI = i - prevI;
        int diffJ = j - prevJ;
        if (diffI == 0 || diffJ == 0) {
            // for horizontal/vertical steps there are 2 neighbors each side that can be connected
            leftPos = getNextPosCCWInner8(leftPos.x, leftPos.y, prevI, prevJ);
            if (isInBounds(data, leftPos) &&  data[leftPos.x][leftPos.y] != emptyValue) {
                return true;
            }
            rightPos = getNextPosCWInner8(rightPos.x, rightPos.y, prevI, prevJ);
            if (isInBounds(data, rightPos) && data[rightPos.x][rightPos.y] != emptyValue) {
                return true;
            }
        }
        return false;
    }

    public static int countNeighbors(int[][] data, int i, int j, int value) {
        int result = 0;
        for (int ii = i - 1; ii <= i + 1; ++ii) {
            for (int jj = j - 1; jj <= j + 1; ++jj) {
                if (!isInBounds(data, ii, jj) || (ii == i && jj == j)) {
                    continue;
                }
                if (data[ii][jj] == value) {
                    result++;
                }
            }
        }
        return result;
    }

    public static boolean isInBounds(int[][] data, GridPoint2 ij) {
        return isInBounds(data, ij.x, ij.y);
    }

    public static boolean isInBounds(int[][] data, int i, int j) {
        return i >= 0 && i < data.length && j >= 0 && j < data[0].length;
    }
}
