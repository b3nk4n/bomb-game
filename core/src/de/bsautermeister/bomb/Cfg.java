package de.bsautermeister.bomb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Logger;

public interface Cfg {

    int LOG_LEVEL = Logger.INFO;
    boolean DEBUG_MODE = false;
    boolean DEBUG_ADS = false;

    /**
     * Enable consent form debugging using a local device.
     * To get the hased ID, start the app with {@link Cfg#DEBUG_ADS} enabled and check for the log:
     * Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("XXXXXX") to set this as a debug device.
     */
    String DEBUG_ADS_TEST_DEVICE_HASHED_ID = "6AF04AC20756A978832ACC7053531D1B"; // Pixel 4

    /**
     * When recording a video via Android 11, the music is still playing even though the volume
     * is muted. Therefore this flag prevents the Music from playing. Furthermore, sound effects
     * are played with the full volume.
     */
    boolean RECORD_MODE = false;

    String SAVE_GAME_FILE = "save.bin";
    float GAME_OVER_DELAY = 3f;
    float DEPTH_TO_SCORE_FACTOR = 10f;

    interface World {
        float GRAVITY = 9.81f;
        float PPM = 5f;
        float WIDTH_PPM = Ground.FRAGMENT_SIZE_PPM * Ground.FRAGMENTS_NUM_COLS;
        float VIEWPORT_HEIGHT_PPM = 41f / PPM;
    }

    interface Ui {
        float WIDTH = 1280;
        float HEIGHT = 720;
    }

    interface Window {
        int WIDTH = 1280;
        int HEIGHT = 720;
    }

    interface Ground {
        float FRAGMENT_SIZE_PPM = 5f / World.PPM;
        int FRAGMENTS_NUM_COLS = 24;
        int FRAGMENTS_NUM_COMPLETE_ROWS = 6;
        int FRAGMENT_RESOLUTION = 16;
    }

    interface Player {
        float RADIUS_PPM = 1.25f / World.PPM;
        float SELF_HEALING_PER_SECOND = 0.015f;
        float START_POSITION_Y = 24f / World.PPM;
    }

    interface Bomb {
        float DETONATION_TO_BLAST_OFFSET = 1.1f;
    }

    interface Colors {
        Color DARK_RED = new Color(0.7f, 0f, 0f, 1f);
    }
}
