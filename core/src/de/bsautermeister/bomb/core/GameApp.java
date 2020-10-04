package de.bsautermeister.bomb.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import de.bsautermeister.bomb.core.transition.ScreenTransition;
import de.bsautermeister.bomb.core.transition.TransitionContext;

public abstract class GameApp implements ApplicationListener {
    private AssetManager assetManager;
    private SpriteBatch batch;

    private TransitionContext transitionContext;
    private FrameBufferManager frameBufferManager;

    @Override
    public void create() {
        assetManager = new AssetManager();
        batch = new SpriteBatch();

        frameBufferManager = new FrameBufferManager();
        transitionContext = new TransitionContext(batch, frameBufferManager);
    }

    public void setScreen(ScreenBase screen) {
        setScreen(screen, null);
    }

    public void setScreen(ScreenBase screen, ScreenTransition transition) {
        transitionContext.setScreen(screen, transition);
    }

    public ScreenBase getScreen() {
        return transitionContext.getScreen();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        transitionContext.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        transitionContext.resize(width, height);
    }

    @Override
    public void pause() {
        transitionContext.pause();
    }

    @Override
    public void resume() {
        transitionContext.resume();
    }

    @Override
    public void dispose() {
        transitionContext.dispose();
        assetManager.dispose();
        batch.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public FrameBufferManager getFrameBufferManager() {
        return frameBufferManager;
    }
}
