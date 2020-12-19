package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class GameStats {
    private final Preferences prefs;

    private static final String KEY_BOMB_KICKS = "bombKicks";
    private static final String KEY_MAX_FEET = "maxFeet";

    public GameStats() {
        prefs = Gdx.app.getPreferences(GameStats.class.getName());
    }

    public void incrementBombKicks() {
        int value = getBombKicks();
        prefs.putInteger(KEY_BOMB_KICKS, ++value);
        prefs.flush();
    }

    public int getBombKicks() {
        return prefs.getInteger(KEY_BOMB_KICKS, 0);
    }

    public void updateMaxFeet(int value) {
        int oldValue = getMaxFeet();
        if (value > oldValue) {
            prefs.putInteger(KEY_MAX_FEET, value);
            prefs.flush();
        }
    }

    public int getMaxFeet() {
        return prefs.getInteger(KEY_MAX_FEET, 0);
    }
}
