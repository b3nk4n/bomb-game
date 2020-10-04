package de.bsautermeister.bomb.screens.game;

import de.bsautermeister.bomb.core.GameApp;
import de.bsautermeister.bomb.core.ScreenBase;

public class GameScreen extends ScreenBase {

    private GameController controller;
    private GameRenderer renderer;

    public GameScreen(GameApp game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        controller = new GameController();
        renderer = new GameRenderer(getBatch(), getAssetManager(), controller);
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
}
