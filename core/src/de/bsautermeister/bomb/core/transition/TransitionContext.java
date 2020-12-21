package de.bsautermeister.bomb.core.transition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.core.graphics.FrameBufferManager;
import de.bsautermeister.bomb.core.ScreenBase;

public class TransitionContext {

    private float transitionTime;
    private ScreenTransition transition;
    private boolean renderedToTexture;
    private boolean transitionInProgress;
    private Viewport transitionViewport;
    private ScreenBase currentScreen;
    private ScreenBase nextScreen;
    private final FrameBufferManager frameBufferManager;
    private FrameBuffer currentFrameBuffer;
    private FrameBuffer nextFrameBuffer;

    private SpriteBatch batch;

    public TransitionContext(SpriteBatch batch, FrameBufferManager frameBufferManager) {
        transitionViewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.batch = batch;
        this.frameBufferManager = frameBufferManager;
    }

    public void setScreen(ScreenBase screen, ScreenTransition transtion) {
        if (transitionInProgress) {
            return;
        }

        if (currentScreen == screen) {
            return;
        }

        this.transition = transtion;

        // screen size
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        // create frame buffers
        currentFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);
        nextFrameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

        // disable input processor during screen transition
        Gdx.input.setInputProcessor(null);

        // start new transition
        nextScreen = screen;
        nextScreen.show();
        nextScreen.resize(width, height);
        nextScreen.resume();
        transitionTime = getDuration();

        if (currentScreen != null) {
            currentScreen.pause();
        }
    }

    public ScreenBase getScreen() {
        return currentScreen;
    }

    public void render(float delta) {
        if (nextScreen == null) {
            // no transition
            if (currentScreen != null) {
                currentScreen.render(delta);
            }
        } else {
            // transition
            transitionInProgress = true;

            transitionTime = transitionTime - delta;

            // render to texture only once
            if (!renderedToTexture) {
                renderedToTexture = true;
                renderScreensToTexture(delta);
            }

            updateTransition();
        }
    }

    private void renderScreensToTexture(float delta) {
        // render current screen to buffer
        if (currentScreen != null) {
            frameBufferManager.begin(currentFrameBuffer);
            currentScreen.render(0f);
            frameBufferManager.end();
        }

        // render next screen to buffer
        frameBufferManager.begin(nextFrameBuffer);
        nextScreen.render(delta);
        frameBufferManager.end();
    }

    private void updateTransition() {
        if (transition == null || isTransitionFinished()) {
            if (currentScreen != null) {
                currentScreen.hide();
            }

            nextScreen.resume();
            Gdx.input.setInputProcessor(nextScreen.getInputProcessor());

            // switch screens and reset
            currentScreen = nextScreen;
            nextScreen = null;
            transition = null;
            currentFrameBuffer.dispose();
            currentFrameBuffer = null;
            nextFrameBuffer.dispose();
            nextFrameBuffer = null;
            renderedToTexture = false;
            transitionInProgress = false;
            return;
        }

        // calculate progress
        float progress = 1.0f - transitionTime / getDuration();

        // get textures from the buffers (these textures are auto-disposed when buffers are disposed)
        Texture currentScreenTexture = currentFrameBuffer.getColorBufferTexture();
        Texture nextScreenTexture = nextFrameBuffer.getColorBufferTexture();

        // render transition to screen
        batch.setProjectionMatrix(transitionViewport.getCamera().combined);
        transition.render(batch, currentScreenTexture, nextScreenTexture, progress);
    }

    public void resize(int width, int height) {
        if (currentScreen != null) {
            currentScreen.resize(width, height);
        }

        if (nextScreen != null) {
            nextScreen.resize(width, height);
        }

        // set world size to width/height to keep pixel per unit ratio
        transitionViewport.setWorldSize(width, height);
        transitionViewport.update(width, height, true);
    }

    public void pause() {
        if (currentScreen != null) {
            currentScreen.pause();
        }
    }

    public void resume() {
        if (currentScreen != null) {
            currentScreen.resume();
        }
    }

    public void dispose() {
        if (currentScreen != null) {
            currentScreen.dispose();
        }

        if (nextScreen != null) {
            nextScreen.dispose();
        }

        currentScreen = null;
        nextScreen = null;

        if (currentFrameBuffer != null) {
            currentFrameBuffer.dispose();
        }

        if (nextFrameBuffer != null) {
            nextFrameBuffer.dispose();
        }
    }

    private boolean isTransitionFinished() {
        return transitionTime <= 0;
    }

    private float getDuration() {
        return transition == null ? 0 : transition.getDuration();
    }
}
