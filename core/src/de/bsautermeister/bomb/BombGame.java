package de.bsautermeister.bomb;

import com.badlogic.gdx.Gdx;

import java.io.File;

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
		// TODO improve implementation

		String fileName = "save.bin";
		// use file in dedicated local storage
		File file = new File(Gdx.files.getLocalStoragePath () + "/" + fileName);
		return file.exists();
	}
}
