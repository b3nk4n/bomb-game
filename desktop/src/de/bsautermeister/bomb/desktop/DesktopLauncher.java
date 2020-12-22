package de.bsautermeister.bomb.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.Cfg;
import de.golfgl.gdxgamesvcs.NoGameServiceClient;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Downfall";
		config.width = Cfg.WINDOW_WIDTH;
		config.height = Cfg.WINDOW_HEIGHT;

		new LwjglApplication(new BombGame(new NoGameServiceClient()), config);
	}
}
