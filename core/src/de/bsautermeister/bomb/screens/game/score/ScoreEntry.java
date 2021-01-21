package de.bsautermeister.bomb.screens.game.score;

public class ScoreEntry {
    private final int score;
    private final String label;

    public ScoreEntry(int score, String label) {
        this.score = score;
        this.label = label;
    }

    public int getScore() {
        return score;
    }

    public String getLabel() {
        return label;
    }

    public static class InGame extends ScoreEntry {
        private static final float TOTAL_EXPIRY_TIME = 0.5f;

        private final boolean currentPlayer;
        private final float depth;
        private float ttl;

        public InGame(int score, String label, boolean currentPlayer) {
            super(score, label);
            this.currentPlayer = currentPlayer;
            this.depth = ScoreUtils.toDepth(score);
            this.ttl = TOTAL_EXPIRY_TIME;
        }

        public void update(float delta, int currentScore) {
            if (currentScore >= getScore()) {
                ttl -= delta;
            }
        }

        public boolean isCurrentPlayer() {
            return currentPlayer;
        }

        public float getDepth() {
            return depth;
        }

        public float getInverseProgress() {
            return Math.max(0f, ttl / TOTAL_EXPIRY_TIME);
        }

        public boolean isExpired() {
            return ttl <= 0f;
        }
    }
}
