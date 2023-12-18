package de.bsautermeister.bomb.core;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Logger;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.GameEnv;
import de.bsautermeister.bomb.core.graphics.FrameBufferManager;
import de.bsautermeister.bomb.core.transition.ScreenTransition;
import de.bsautermeister.bomb.core.transition.TransitionContext;
import de.bsautermeister.bomb.effects.ParticleEffectBox2D;
import de.bsautermeister.bomb.effects.ParticleEffectBox2DLoader;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;

public abstract class GameApp implements ApplicationListener {
    private static final Logger LOG = new Logger(GameApp.class.getSimpleName(), Cfg.LOG_LEVEL);

    private AssetManager assetManager;
    private SpriteBatch batch;

    private final FrameBufferManager frameBufferManager;

    private final GameEnv gameEnv;

    private TransitionContext transitionContext;

    private final IGameServiceClient gameServiceClient;

    public GameApp(GameEnv gameEnv, IGameServiceClient gameServiceClient) {
        this.gameEnv = gameEnv;
        this.gameServiceClient = gameServiceClient;
        this.frameBufferManager = new FrameBufferManager();
    }

    @Override
    public void create() {
        assetManager = new AssetManager();
        assetManager.setLoader(ParticleEffectBox2D.class, new ParticleEffectBox2DLoader(assetManager.getFileHandleResolver()));

        batch = new SpriteBatch();

        transitionContext = new TransitionContext(batch, frameBufferManager);

        gameServiceClient.setListener(new IGameServiceListener() {
            @Override
            public void gsOnSessionActive() {
                LOG.info("Game service session active");
            }

            @Override
            public void gsOnSessionInactive() {
                LOG.info("Game service session inactive");
            }

            @Override
            public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
                LOG.error("Game service error: " + msg, t);
            }
        });

        gameServiceClient.resumeSession();
    }

    public void setScreen(ScreenBase screen) {
        setScreen(screen, null);
    }

    public void setScreen(ScreenBase screen, ScreenTransition transition) {
        transitionContext.setScreen(screen, transition);
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
        gameServiceClient.pauseSession();
    }

    @Override
    public void resume() {
        transitionContext.resume();
        gameServiceClient.resumeSession();
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

    public IGameServiceClient getGameServiceClient() {
        return gameServiceClient;
    }

    public GameEnv getEnv() {
        return gameEnv;
    }
}
