package de.bsautermeister.bomb;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Logger;

public interface Cfg {

    int LOG_LEVEL = Logger.DEBUG;
    boolean DEBUG_MODE = false;
    boolean DEBUG_ADS = true;

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
