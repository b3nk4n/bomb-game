package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.InputProcessor;

import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.core.ScreenBase;
import de.bsautermeister.bomb.screens.menu.MenuScreen;

public class GameScreen extends ScreenBase {

    private GameController controller;
    private GameRenderer renderer;

    private final GameScreenCallbacks callbacks = new GameScreenCallbacks() {
        @Override
        public void backToMenu() {
            setScreen(new MenuScreen(getGame()));
        }
    };

    public GameScreen(GameApp game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        controller = new GameController(callbacks, getAssetManager());
        renderer = new GameRenderer(getBatch(), getAssetManager(), controller,
                getGame().getFrameBufferManager());
    }

    @Override
    public void pause() {
        super.pause();
        controller.save();
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
