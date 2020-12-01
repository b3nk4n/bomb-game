package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.RegionNames;
import de.bsautermeister.bomb.core.FrameBufferManager;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Fragment;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.screens.game.overlay.GameOverOverlay;
import de.bsautermeister.bomb.screens.game.overlay.PauseOverlay;
import de.bsautermeister.bomb.utils.GdxUtils;

public class GameRenderer implements Disposable {

    private final static short[] TRIANGULATION_IDENTITY = new short[] { 2, 1, 0 };

    private final Viewport uiViewport;
    private final SpriteBatch batch;
    private final GameController controller;
    private final PolygonSpriteBatch polygonBatch = new PolygonSpriteBatch(); // TODO move to other SpriteBatch, or even replace it?

    private final FrameBuffer[] frameBuffers;
    private final FrameBufferManager frameBufferManager;

    private final Box2DDebugRenderer box2DRenderer;

    private final TextureRegion ballRegion;
    private final TextureRegion bombRegion;
    private final TextureRegion surfaceRegion;
    private final TextureRegion groundRegion;

    private final ShaderProgram shaderEffect; // TODO try out shader-program-loader via asset loader?

    private final Skin skin;
    private final Stage overlayStage;
    private final Stage hudStage;

    public GameRenderer(SpriteBatch batch, AssetManager assetManager, GameController controller,
                        FrameBufferManager frameBufferManager) {
        this.batch = batch;
        this.controller = controller;
        this.frameBufferManager = frameBufferManager;

        frameBuffers = new FrameBuffer[2];
        for (int i = 0; i < frameBuffers.length; ++i) {
            frameBuffers[i] = new FrameBuffer(
                    Pixmap.Format.RGBA8888,
                    Gdx.graphics.getWidth(),
                    Gdx.graphics.getHeight(),
                    false);
        }

        uiViewport = new StretchViewport(Cfg.UI_WIDTH, Cfg.UI_HEIGHT);

        this.box2DRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);

        TextureAtlas atlas =  assetManager.get(Assets.Atlas.GAME);
        surfaceRegion = atlas.findRegion(RegionNames.Game.BLOCK_SURFACE);
        groundRegion = atlas.findRegion(RegionNames.Game.BLOCK_GROUND);
        ballRegion = atlas.findRegion(RegionNames.Game.BALL);
        bombRegion = atlas.findRegion(RegionNames.Game.BOMB);

        skin = assetManager.get(Assets.Skins.UI);
        overlayStage = new Stage(uiViewport, batch);
        overlayStage.setDebugAll(Cfg.DEBUG_MODE);

        hudStage = new Stage(uiViewport, batch);

        shaderEffect = GdxUtils.loadCompiledShader("shader/default.vs", "shader/blast.fs");

        Gdx.input.setInputProcessor(overlayStage);
    }

    private final Vector3 tmpBlastProjection = new Vector3();
    public void render(float delta) {

        Camera camera = controller.getCamera();
        Viewport viewport = controller.getViewport();

        GdxUtils.clearScreen();

        viewport.apply();
        int fbIdx = 0;
        frameBufferManager.begin(frameBuffers[fbIdx++]);
        GdxUtils.clearScreen();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBall(batch);
        renderBombs(batch);

        batch.end();

        polygonBatch.setProjectionMatrix(camera.combined);
        polygonBatch.begin();

        renderGround(polygonBatch);

        polygonBatch.end();
        frameBufferManager.end();

        Array<GameController.BlastInstance> blasts = controller.getActiveBlastEffects();

        for (GameController.BlastInstance blast : blasts) {
            frameBufferManager.begin(frameBuffers[fbIdx]);
            fbIdx = fbIdx == 0 ? 1 : 0;
            batch.begin();

            Vector2 blastPosition = blast.getPosition();
            tmpBlastProjection.set(blastPosition.x, blastPosition.y, 0f);
            camera.project(tmpBlastProjection);
            tmpBlastProjection.scl(1f / viewport.getScreenWidth(), 1f / viewport.getScreenHeight(), 1f);
            batch.setShader(shaderEffect);
            shaderEffect.setUniformf("u_time", blast.getProgress());
            shaderEffect.setUniformf("u_center_uv", tmpBlastProjection.x, tmpBlastProjection.y);

            batch.draw(frameBuffers[fbIdx].getColorBufferTexture(),
                    camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight,
                    0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);

            batch.setShader(null);
            batch.end();
            frameBufferManager.end();
        }

        batch.begin();
        batch.draw(frameBuffers[fbIdx == 0 ? 1 : 0].getColorBufferTexture(),
                camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight,
                0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, true);
        batch.end();

        if (Cfg.DEBUG_MODE) {
            box2DRenderer.render(controller.getWorld(), camera.combined);
        }

        uiViewport.apply();
        batch.setProjectionMatrix(hudStage.getCamera().combined);
        hudStage.act();
        renderHud(batch);
    }

    private void renderBall(SpriteBatch batch) {
        Player player = controller.getPlayer();
        if (!player.isDead()) {
            Vector2 position = player.getPosition();
            float radius = player.getRadius();
            batch.draw(ballRegion,
                    position.x - radius, position.y - radius,
                    radius, radius,
                    radius * 2f, radius * 2f,
                    1f, 1f, player.getRotation());
        }
    }

    private void renderBombs(SpriteBatch batch) {
        for (Bomb bomb : controller.getBombs()) {
            Vector2 position = bomb.getPosition();
            float radius = bomb.getBodyRadius();
            batch.draw(bombRegion,
                    position.x - radius, position.y - radius,
                    radius, radius,
                    radius * 2f, radius * 2f,
                    1f, 1f, bomb.getRotation());
        }
    }

    private static Vector2 tmpVertex = new Vector2();
    private static float[] tmpVerticesArray = new float[6];
    private void renderGround(PolygonSpriteBatch polygonBatch) {
        Ground ground = controller.getGround();

        for (Array<Fragment> fragmentRows : ground.getFragments()) {
            for (Fragment fragment : fragmentRows) {
                if (fragment.isEmpty()) continue;

                TextureRegion textureRegion = fragment.getBottomY() >= -1 ? surfaceRegion : groundRegion;
                float texWidth = textureRegion.getRegionWidth();
                float texHeight = textureRegion.getRegionHeight();

                for (Fixture fixture : fragment.getBody().getFixtureList()) {
                    PolygonShape polygon = (PolygonShape) fixture.getShape();
                    polygon.getVertex(0, tmpVertex);
                    tmpVerticesArray[0] = tmpVertex.x * texWidth;
                    tmpVerticesArray[1] = tmpVertex.y * texHeight;
                    polygon.getVertex(1, tmpVertex);
                    tmpVerticesArray[2] = tmpVertex.x * texWidth;
                    tmpVerticesArray[3] = tmpVertex.y * texHeight;
                    polygon.getVertex(2, tmpVertex);
                    tmpVerticesArray[4] = tmpVertex.x * texWidth;
                    tmpVerticesArray[5] = tmpVertex.y * texHeight;

                    PolygonRegion polyReg = new PolygonRegion(textureRegion, tmpVerticesArray, TRIANGULATION_IDENTITY);
                    PolygonSprite polySprite = new PolygonSprite(polyReg);
                    polySprite.setPosition(fragment.getLeftX(), fragment.getBottomY());
                    polySprite.setSize(1f, 1f);
                    polySprite.draw(polygonBatch);
                }
            }
        }
    }

    private void renderHud(SpriteBatch batch) {
        updateOverlay();
        renderHudOverlay();
    }

    private void renderHudOverlay() {
        if (!overlayStage.getActors().isEmpty()) {
            if (skipNextOverlayAct) {
                skipNextOverlayAct = false;
                return;
            }
            overlayStage.act();
            batch.begin();
            // batch.draw(backgroundOverlayRegion, 0f, 0f, Cfg.UI_WIDTH, Cfg.UI_HEIGHT);
            batch.end();
            overlayStage.draw();
        }
    }

    // workaround: do not act during the first frame, otherwise button event which triggered
    // this overlay to show are processed in the overlay, which could immediately close it again
    private boolean skipNextOverlayAct = false;

    private void updateOverlay() {
        if (overlayStage.getActors().isEmpty()) {
            if (controller.getState().isPaused()) {
                overlayStage.addActor(new PauseOverlay(skin, controller.getPauseCallback()));
                skipNextOverlayAct = true;
            } else if (controller.getState().isGameOver()) {
                overlayStage.addActor(new GameOverOverlay(skin, controller.getGameOverCallback()));
                skipNextOverlayAct = true;
            }
        } else {
            if (!controller.getState().isPaused() && !controller.getState().isGameOver()) {
                overlayStage.clear();
            }
        }
    }

    public void resize(int width, int height) {
        controller.getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        polygonBatch.dispose();
        shaderEffect.dispose();
        for (FrameBuffer frameBuffer : frameBuffers) {
            frameBuffer.dispose();
        }
    }

    public InputProcessor getInputProcessor() {
        return overlayStage;
    }
}
