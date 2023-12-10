package de.bsautermeister.bomb.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.service.NoopAdService;
import de.bsautermeister.bomb.service.NoopRateService;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("The Downfall");
        config.setWindowedMode(Cfg.Window.WIDTH, Cfg.Window.HEIGHT);
        config.setForegroundFPS(60);

        new Lwjgl3Application(
                new BombGame(
                        new NoGameServiceClient(),
                        new NoopRateService(),
                        new NoopAdService()),
                config);
    }
}
