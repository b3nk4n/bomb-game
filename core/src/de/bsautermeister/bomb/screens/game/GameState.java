package de.bsautermeister.bomb.screens.game;

public enum GameState {
    PLAYING,
    PAUSED,
    GAME_OVER;

    public boolean isPlaying() {
        return this == PLAYING;
    }

    public boolean isPaused() {
        return this == PAUSED;
    }

    public boolean isGameOver() {
        return this == GAME_OVER;
    }
}
