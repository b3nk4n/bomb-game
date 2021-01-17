package de.bsautermeister.bomb;

import com.badlogic.gdx.Gdx;

import java.io.File;

import de.bsautermeister.bomb.audio.MusicPlayer;
import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.screens.game.GameStats;
import de.bsautermeister.bomb.screens.game.score.GameScores;
import de.bsautermeister.bomb.screens.loading.LoadingScreen;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

public class BombGame extends GameApp {

	private GameSettings gameSettings;
	private GameStats gameStats;
	private GameScores gameScores;
	private MusicPlayer musicPlayer;

	public BombGame(IGameServiceClient gameServiceClient) {
		super(gameServiceClient);
	}

	@Override
	public void create () {
		super.create();
		Gdx.app.setLogLevel(Cfg.LOG_LEVEL);
		gameSettings = new GameSettings();
		gameStats = new GameStats();
		gameScores = new GameScores();
		musicPlayer = new MusicPlayer();

		setScreen(new LoadingScreen(this));
	}

	@Override
	public void render() {
		super.render();
		musicPlayer.setMasterVolume(gameSettings.getMusicVolume());
		musicPlayer.update(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void pause() {
		super.pause();
		if (musicPlayer.isPlaying()) {
			musicPlayer.pause();
		}
	}

	@Override
	public void resume() {
		super.resume();
		musicPlayer.resumeOrPlay();
	}

	@Override
	public void dispose() {
		super.dispose();
		musicPlayer.dispose();
	}

	public File getGameFile() {
		return new File(Gdx.files.getLocalStoragePath() + "/" + Cfg.SAVE_GAME_FILE);
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public GameStats getGameStats() {
		return gameStats;
	}

	public GameScores getGameScores() {
		return gameScores;
	}

	public MusicPlayer getMusicPlayer() {
		return musicPlayer;
	}
}
