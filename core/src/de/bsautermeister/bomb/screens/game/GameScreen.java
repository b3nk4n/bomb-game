package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.InputProcessor;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.core.ScreenBase;
import de.bsautermeister.bomb.screens.menu.MenuScreen;
import de.bsautermeister.bomb.screens.transition.ScreenTransitions;

public class GameScreen extends ScreenBase {

    private GameController controller;
    private GameRenderer renderer;

    private final boolean resume;

    private final GameScreenCallbacks callbacks = new GameScreenCallbacks() {
        @Override
        public void backToMenu() {
            // actually wanted to do a SLIDE transition here, but somehow the transition was not
            // really noticeable when going back to main menu
            setScreen(new MenuScreen(getGame()), ScreenTransitions.FADE);
        }

        @Override
        public void restartGame() {
            setScreen(new GameScreen(getGame(), false), ScreenTransitions.FADE);
        }
    };

    public GameScreen(GameApp game, boolean resume) {
        super(game);
        this.resume = resume;
    }

    @Override
    public void show() {
        super.show();

        BombGame game = (BombGame) getGame();

        controller = new GameController(game, callbacks, getAssetManager());
        controller.initialize(resume);
        renderer = new GameRenderer(getBatch(), getAssetManager(), controller);

        game.getAdService().load();
    }

    @Override
    public void pause() {
        super.pause();
        controller.pause();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        controller.update(delta);
        renderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        renderer.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        renderer.dispose();
        controller.dispose();
    }

    @Override
    public InputProcessor getInputProcessor() {
        return renderer.getInputProcessor();
    }
}
