package de.bsautermeister.bomb;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class GameSettings {
    private final Preferences prefs;

    public static final int MAX_MUSIC_VOLUME_LEVEL = 3;

    private static final String KEY_MUSIC_VOLUME_LEVEL = "musicVolume";
    private static final String KEY_VIBRATION = "vibration";

    private boolean localVibration;
    private int localMusicVolumeLevel;

    public GameSettings() {
        prefs = Gdx.app.getPreferences(GameSettings.class.getName());
        localVibration = prefs.getBoolean(KEY_VIBRATION, true);
        localMusicVolumeLevel = prefs.getInteger(KEY_MUSIC_VOLUME_LEVEL, 2);
    }

    public boolean toggleVibration() {
        localVibration = !localVibration;
        prefs.putBoolean(KEY_VIBRATION, localVibration);
        prefs.flush();
        return localVibration;
    }

    public boolean getVibration() {
        return localVibration;
    }

    public int toggleMusicVolumeLevel() {
        localMusicVolumeLevel = ++localMusicVolumeLevel % (MAX_MUSIC_VOLUME_LEVEL + 1);
        prefs.putInteger(KEY_MUSIC_VOLUME_LEVEL, localMusicVolumeLevel);
        prefs.flush();
        return localMusicVolumeLevel;
    }

    public int getMusicVolumeLevel() {
        return localMusicVolumeLevel;
    }

    public float getMusicVolume() {
        switch (localMusicVolumeLevel) {
            case 0:
                return 0;
            case 1:
                return 0.2f;
            case 2:
                return 0.5f;
            case 3:
            default:
                return 0.75f;
        }
    }
}
