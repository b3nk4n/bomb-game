package de.bsautermeister.bomb;

import com.badlogic.gdx.Gdx;

import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.screens.loading.LoadingScreen;

public class BombGame extends GameApp {
	@Override
	public void create () {
		super.create();
		Gdx.app.setLogLevel(Cfg.LOG_LEVEL);

		setScreen(new LoadingScreen(this));
	}

	public static void deleteSavedData() {
		// TODO implement
	}

	public static boolean hasSavedData() {
		// TODO implement
		return false;
	}
}
