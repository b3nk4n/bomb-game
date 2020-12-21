package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.RegionNames;
import de.bsautermeister.bomb.core.FrameBufferManager;
import de.bsautermeister.bomb.objects.BlastInstance;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.BounceStickyBomb;
import de.bsautermeister.bomb.objects.ClusterBomb;
import de.bsautermeister.bomb.objects.ClusterFragmentBomb;
import de.bsautermeister.bomb.objects.Fragment;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.objects.StickyBomb;
import de.bsautermeister.bomb.objects.TimedBomb;
import de.bsautermeister.bomb.screens.game.overlay.GameOverOverlay;
import de.bsautermeister.bomb.screens.game.overlay.Overlays;
import de.bsautermeister.bomb.screens.game.overlay.PauseOverlay;
import de.bsautermeister.bomb.utils.GdxUtils;

public class GameRenderer implements Disposable {

    private final static short[] TRIANGULATION_IDENTITY = new short[] { 2, 1, 0 };

    private final Viewport uiViewport;
    private final PolygonSpriteBatch polygonBatch = new PolygonSpriteBatch();
    private final SpriteBatch batch;
    private final FrameBufferManager frameBufferManager;
    private final FrameBuffer[] frameBuffers;
    private final GameController controller;
    private final Box2DDebugRenderer box2DRenderer;

    private final TextureRegion ballRegion;
    private final Array<TextureAtlas.AtlasRegion> bombRegions;
    private final TextureRegion surfaceRegion;
    private final TextureRegion groundRegion;

    private final ShaderProgram blastShader;
    private final ShaderProgram blurShader;

    private final GameHud hud;
    private final Overlays<GameState> overlays;

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

        this.box2DRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);

        TextureAtlas atlas =  assetManager.get(Assets.Atlas.GAME);
        surfaceRegion = atlas.findRegion(RegionNames.Game.BLOCK_SURFACE);
        groundRegion = atlas.findRegion(RegionNames.Game.BLOCK_GROUND);
        ballRegion = atlas.findRegion(RegionNames.Game.BALL);
        bombRegions = atlas.findRegions(RegionNames.Game.BOMB);

        uiViewport = new StretchViewport(Cfg.UI_WIDTH, Cfg.UI_HEIGHT);

        Skin skin = assetManager.get(Assets.Skins.UI);

        hud = new GameHud(assetManager, uiViewport, batch);
        overlays = new Overlays<>(uiViewport, batch, 0x000000BB);
        overlays.register(GameState.PAUSED,
                new PauseOverlay(skin, controller.getPauseCallback()));
        overlays.register(GameState.GAME_OVER,
                new GameOverOverlay(skin, controller.getGameOverCallback()));

        blastShader = assetManager.get(Assets.ShaderPrograms.BLAST);
        blurShader = assetManager.get(Assets.ShaderPrograms.BLUR);
    }

    private final Vector3 tmpBlastProjection = new Vector3();
    public void render(float delta) {
        Camera camera = controller.getCamera();
        Viewport viewport = controller.getViewport();

        viewport.apply();
        int fbIdx = 0;
        frameBufferManager.begin(frameBuffers[fbIdx]);
        fbIdx = ++fbIdx % frameBuffers.length;
        GdxUtils.clearScreen();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBall(batch);
        renderBombs(batch);
        controller.getExplosionEffect().draw(batch);

        batch.end();

        polygonBatch.setProjectionMatrix(camera.combined);
        polygonBatch.begin();

        renderGround(polygonBatch);

        polygonBatch.end();
        frameBufferManager.end();

        // set shader once before the loop, because it causes a fair amount of setup under the hood
        batch.setShader(blastShader);

        Array<BlastInstance> blasts = controller.getActiveBlastEffects();
        for (BlastInstance blast : blasts) {
            frameBufferManager.begin(frameBuffers[fbIdx]);
            fbIdx = ++fbIdx % frameBuffers.length;
            GdxUtils.clearScreen();

            batch.begin();
            Vector2 blastPosition = blast.getPosition();
            tmpBlastProjection.set(blastPosition.x, blastPosition.y, 0f);
            camera.project(tmpBlastProjection);
            tmpBlastProjection.scl(1f / viewport.getScreenWidth(), 1f / viewport.getScreenHeight(), 1f);
            blastShader.setUniformf("u_time", blast.getProgress());
            blastShader.setUniformf("u_center_uv", tmpBlastProjection.x, tmpBlastProjection.y);
            blastShader.setUniformf("u_dist_range", blast.getRadius() / 6f);

            Texture sourceTexture = frameBuffers[fbIdx].getColorBufferTexture();
            batch.draw(sourceTexture,
                    camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight,
                    0, 0, sourceTexture.getWidth(), sourceTexture.getHeight(), false, true);
            batch.end();
            frameBufferManager.end();
        }

        batch.begin();
        float criticalHealthRatio = controller.getPlayer().getCriticalHealthRatio();
        if (criticalHealthRatio > 0f) {
            batch.setShader(blurShader);
            blurShader.setUniformf("u_radius", Interpolation.smooth.apply(0f, 0.01f, criticalHealthRatio));
            float otherColorRatio = 1f - criticalHealthRatio / 2f;
            batch.setColor(1f, otherColorRatio, otherColorRatio, 1f);
        } else {
            batch.setShader(null);
        }
        fbIdx = ++fbIdx % frameBuffers.length;
        Texture sourceTexture = frameBuffers[fbIdx].getColorBufferTexture();
        GdxUtils.clearScreen();
        batch.draw(sourceTexture,
                camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight,
                0, 0, sourceTexture.getWidth(), sourceTexture.getHeight(), false, true);
        batch.end();
        batch.setColor(Color.WHITE);
        batch.setShader(null);

        if (Cfg.DEBUG_MODE) {
            box2DRenderer.render(controller.getWorld(), camera.combined);
        }

        uiViewport.apply();
        batch.setProjectionMatrix(hud.getCamera().combined);
        renderHud(delta);
        overlays.update(controller.getState());
        overlays.render(batch);
    }

    private void renderBall(Batch batch) {
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

    private void renderBombs(Batch batch) {
        for (Bomb bomb : controller.getBombs()) {
            Vector2 position = bomb.getPosition();
            float radius = bomb.getBodyRadius();
            batch.draw(getTexture(bomb),
                    position.x - radius, position.y - radius,
                    radius, radius,
                    radius * 2f, radius * 2f,
                    1f, 1f, bomb.getRotation());
        }
    }

    private TextureRegion getTexture(Bomb bomb) {
        if (bomb instanceof ClusterBomb) {
            return bombRegions.get(bomb.isFlashing() ? 3 : 2);
        } else if (bomb instanceof ClusterFragmentBomb) {
            return bombRegions.get(2);
        } else if (bomb instanceof TimedBomb) {
            return bombRegions.get(bomb.isFlashing() ? 1 : 0);
        } else if (bomb instanceof StickyBomb) {
            return bombRegions.get(bomb.isFlashing() ? 5 : 4);
        } else if (bomb instanceof BounceStickyBomb) {
            return bombRegions.get(bomb.isFlashing() ? 7 : 6);
        }

        throw new IllegalArgumentException("The given type is not supported yet.");
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

    private void renderHud(float delta) {
        Player player = controller.getPlayer();
        hud.updateLifeRatio(player.getLifeRatio());
        hud.updateScore(player.getScore());
        hud.render(delta);
    }



    public void resize(int width, int height) {
        controller.getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        polygonBatch.dispose();
        for (FrameBuffer frameBuffer : frameBuffers) {
            frameBuffer.dispose();
        }
    }

    public InputProcessor getInputProcessor() {
        return overlays.getInputProcessor();
    }
}
