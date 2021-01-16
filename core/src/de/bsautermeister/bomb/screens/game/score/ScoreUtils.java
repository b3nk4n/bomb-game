package de.bsautermeister.bomb.screens.game.score;

import de.bsautermeister.bomb.Cfg;

public final class ScoreUtils {

    private ScoreUtils() { }

    public static int toScore(float depth) {
        return (int)(depth * Cfg.DEPTH_TO_SCORE_FACTOR);
    }

    public static float toDepth(int score) {
        return score / Cfg.DEPTH_TO_SCORE_FACTOR;
    }
}
