package de.bsautermeister.bomb;

public class AndroidGameEnv implements GameEnv {
    @Override
    public String getVersion() {
        return BuildConfig.VERSION_NAME;
    }
}
