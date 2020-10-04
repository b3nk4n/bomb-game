package de.bsautermeister.bomb;

import com.badlogic.gdx.Gdx;

import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.screens.game.GameScreen;

public class BombGame extends GameApp {
	@Override
	public void create () {
		super.create();
		Gdx.app.setLogLevel(Cfg.LOG_LEVEL);

		setScreen(new GameScreen(this));
	}
}
