package de.bsautermeister.bomb.screens.game;

public enum GameState {
    PLAYING,
    PAUSED,
    PLAYER_JUST_DIED,
    GAME_OVER;

    public boolean anyGameOver() {
        return this == GAME_OVER || this == PLAYER_JUST_DIED;
    }
}
