package de.bsautermeister.bomb.screens.game.score;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

public class GameScores {

    private final Preferences prefs;

    private static final int MIN_BEST_PERSONAL_SCORE_DIFF = 5;
    private Array<Integer> otherBestScores = new Array<>();
    private int personalBestScore;

    private static final String KEY_PERSONAL_BEST = "maxFeet";

    private static final String[] RANKS = {
            "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th"
    };

    /**
     * Single list containing both the local player and other players scores, with proper
     * rank-labels assigned.
     */
    private Array<ScoreEntry> scoreEntries = new Array<>();

    public GameScores() {
        prefs = Gdx.app.getPreferences(GameScores.class.getName());

        personalBestScore = prefs.getInteger(KEY_PERSONAL_BEST, 0);
        updateScoreEntries();
    }

    public void updateTopList(Array<Integer> newTopList) {
        otherBestScores.clear();
        otherBestScores.addAll(newTopList);
        updateScoreEntries();
    }

    public void updatePersonalBest(int score) {
        if (score > personalBestScore) {
            personalBestScore = score;
            prefs.putInteger(KEY_PERSONAL_BEST, score);
            prefs.flush();
            updateScoreEntries();
        }
    }

    private synchronized void updateScoreEntries() {
        scoreEntries.clear();

        if (personalBestScore > 0) {
            scoreEntries.add(new ScoreEntry(personalBestScore, "My Highscore"));
        }

        for (int i = 0; i < otherBestScores.size; ++i) {
            int currentScore = otherBestScores.get(i);
            int currentRankIdx = currentScore > personalBestScore ? i : i + 1;

            if (Math.abs(currentScore - personalBestScore) >= MIN_BEST_PERSONAL_SCORE_DIFF) {
                scoreEntries.add(new ScoreEntry(currentScore, RANKS[currentRankIdx]));
            }
        }
    }

    public Array<Integer> getOtherBestScores() {
        return otherBestScores;
    }

    public int getPersonalBestScore() {
        return personalBestScore;
    }

    public Array<ScoreEntry> getScoreEntries() {
        return scoreEntries;
    }
}
