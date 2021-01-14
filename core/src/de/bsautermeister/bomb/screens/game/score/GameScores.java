package de.bsautermeister.bomb.screens.game.score;

import com.badlogic.gdx.utils.Array;

public class GameScores {

    private static final int MAX_TOP_LIST_ENTRIES = 5;
    private static final int MIN_BEST_PLAYER_SCORE_DIFF = 3;
    private Array<Integer> topList = new Array<>();
    private int personalBest;

    public void update(Array<Integer> topScores, int personalBest) {
        this.personalBest = personalBest;
        topList.clear();
        int limit = Math.min(topScores.size, MAX_TOP_LIST_ENTRIES);
        for (int i = 0; i < limit; ++i) {
            int scoreValue = topScores.get(i);
            if (Math.abs(scoreValue - personalBest) > MIN_BEST_PLAYER_SCORE_DIFF) { // FIXME if player is within these ranges, then the list 1st - 5th of other players is not correct
                topList.add(scoreValue);
            }
        }
    }

    public Array<Integer> getTopList() {
        return topList;
    }

    public int getPersonalBest() {
        return personalBest;
    }

}
