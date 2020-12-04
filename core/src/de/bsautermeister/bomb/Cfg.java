package de.bsautermeister.bomb;

import com.badlogic.gdx.utils.Logger;

public interface Cfg {
    int LOG_LEVEL = Logger.DEBUG;
    boolean DEBUG_MODE = true;

    float PPM = 5f;

    float VIEWPORT_WORLD_WIDTH_PPM = 64f / Cfg.PPM;
    float VIEWPORT_WORLD_HEIGHT_PPM = 41f / Cfg.PPM;

    float UI_WIDTH = 1280;
    float UI_HEIGHT = 720;

    int WINDOW_WIDTH = 1280;
    int WINDOW_HEIGHT = 720;

    float GRAVITY = 9.81f;

    float GROUND_FRAGMENT_SIZE_PPM = 5f / Cfg.PPM;
    int GROUND_FRAGMENTS_NUM_COLS = 20;
    int GROUND_FRAGMENTS_NUM_COMPLETE_ROWS = 6;

    float WORLD_WIDTH_PPM = GROUND_FRAGMENT_SIZE_PPM * GROUND_FRAGMENTS_NUM_COLS;

    float PLAYER_RADIUS_PPM = 1.25f / Cfg.PPM;
    float PLAYER_SELF_HEALING_PER_SECOND = 0.01f;
}
