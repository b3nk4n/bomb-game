package de.bsautermeister.bomb;

import de.bsautermeister.bomb.game.BuildConfig;

public class AndroidGameEnv implements GameEnv {
    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
