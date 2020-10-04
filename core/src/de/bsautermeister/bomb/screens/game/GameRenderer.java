package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Disposable;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.utils.GdxUtils;

public class GameRenderer implements Disposable {

    private final SpriteBatch batch;
    private final AssetManager assetManager;
    private final GameController controller;

    private final Box2DDebugRenderer box2DRenderer;

    public GameRenderer(SpriteBatch batch, AssetManager assetManager, GameController controller) {
        this.batch = batch;
        this.assetManager = assetManager;
        this.controller = controller;

        this.box2DRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);
    }

    public void render(float delta) {
        GdxUtils.clearScreen();

        Camera camera = controller.getCamera();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.end();

        if (Cfg.DEBUG_MODE) {
            box2DRenderer.render(controller.getWorld(), camera.combined);
        }
    }

    public void resize(int width, int height) {
        controller.getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {

    }
}
