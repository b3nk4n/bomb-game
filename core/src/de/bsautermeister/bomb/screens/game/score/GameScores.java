package de.bsautermeister.bomb.screens.game.score;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;

import java.util.Locale;

public class GameScores {

    private final Preferences prefs;

    private Array<ScoreEntry> otherScores = new Array<>();

    private int personalBestScore;

    private static final String KEY_PERSONAL_BEST = "maxFeet";

    public GameScores() {
        prefs = Gdx.app.getPreferences(GameScores.class.getName());

        personalBestScore = prefs.getInteger(KEY_PERSONAL_BEST, 0);
    }

    public synchronized void clearOtherScores() {
        otherScores.clear();
    }

    /**
     * We currently have the assumption that we add other scores in order starting
     * from the highest score (global 1st).
     */
    public synchronized void addOtherScore(int score, String username) {
        otherScores.add(new ScoreEntry(score, username));
    }

    public synchronized void updatePersonalBest(int score) {
        if (score > personalBestScore) {
            personalBestScore = score;
            prefs.putInteger(KEY_PERSONAL_BEST, score);
            prefs.flush();
        }
    }

    public int getPersonalBestScore() {
        return personalBestScore;
    }

    public synchronized Array<ScoreEntry.InGame> getAllScoreEntries(int minDelta) {
        Array<ScoreEntry.InGame> results = new Array<>();

        if (personalBestScore > 0) {
            results.add(new ScoreEntry.InGame(personalBestScore, "Personal Best", true));
        }

        int previousScore = 0;
        for (int i = 0; i < otherScores.size; ++i) {
            ScoreEntry entry = otherScores.get(i);
            int otherScore = entry.getScore();
            int currentRank = otherScore > personalBestScore ? i + 1 : i + 2;

            if (Math.abs(otherScore - personalBestScore) >= minDelta
                    && Math.abs(otherScore - previousScore) >= minDelta) {
                results.add(new ScoreEntry.InGame(
                        otherScore,
                        String.format(Locale.ROOT, "%d. %s", currentRank, entry.getLabel()),
                        false));
                previousScore = otherScore;
            }
        }

        return results;
    }
}
