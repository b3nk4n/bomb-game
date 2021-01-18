package de.bsautermeister.bomb.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.service.NoopRateService;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "The Downfall";
		config.width = Cfg.Window.WIDTH;
		config.height = Cfg.Window.HEIGHT;

		new LwjglApplication(
				new BombGame(
						new NoGameServiceClient(),
						new NoopRateService()),
				config);
	}
}
