package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class GameStats {
    private final Preferences prefs;

    private static final String KEY_TUTORIAL_COMPLETED = "tutorialCompleted";

    public GameStats() {
        prefs = Gdx.app.getPreferences(GameStats.class.getName());
    }

    public void setTutorialCompleted() {
        prefs.putBoolean(KEY_TUTORIAL_COMPLETED, true);
        prefs.flush();
    }

    public boolean hasTutorialCompleted() {
        return prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false);
    }
}
