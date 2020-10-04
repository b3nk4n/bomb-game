package de.bsautermeister.bomb;

import com.badlogic.gdx.utils.Logger;

public interface Cfg {
    int LOG_LEVEL = Logger.DEBUG;
    boolean DEBUG_MODE = true;

    float PPM = 5f;

    int WORLD_WIDTH = 64;
    int WORLD_HEIGHT = 41;

    int WINDOW_WIDTH = 1280;
    int WINDOW_HEIGHT = 720;

    float GRAVITY = 9.81f;
}
