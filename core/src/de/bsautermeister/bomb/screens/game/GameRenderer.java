package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import de.bsautermeister.bomb.core.graphics.Camera2D;
import de.bsautermeister.bomb.core.graphics.ExtendedShapeRenderer;
import de.bsautermeister.bomb.core.graphics.FrameBufferManager;
import de.bsautermeister.bomb.objects.BlastInstance;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Fragment;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.screens.game.overlay.GameOverOverlay;
import de.bsautermeister.bomb.screens.game.overlay.Overlays;
import de.bsautermeister.bomb.screens.game.overlay.PauseOverlay;
import de.bsautermeister.bomb.utils.GdxUtils;
import de.bsautermeister.bomb.utils.PolygonUtils;

public class GameRenderer implements Disposable {

    private static final short[] TRIANGULATION_IDENTITY = new short[] { 2, 1, 0 };
    private static final float[] POLYGON_BUFFER = new float[64];
    private static final float POLYGON_ZOOM = 1.1f;

    private final Viewport uiViewport;
    private final PolygonSpriteBatch polygonBatch = new PolygonSpriteBatch();
    private final SpriteBatch batch;
    private final FrameBufferManager frameBufferManager;
    private final FrameBuffer[] frameBuffers;
    private final GameController controller;
    private final Box2DDebugRenderer box2DRenderer;

    private final TextureRegion surfaceRegion;
    private final TextureRegion groundRegion;

    private final ShaderProgram blastShader;
    private final ShaderProgram blurShader;

    private final ShapeRenderer shapeRenderer;

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

        this.box2DRenderer = Cfg.DEBUG_MODE
                ? null
                : new Box2DDebugRenderer(true, true, false, true, true, true);

        TextureAtlas atlas =  assetManager.get(Assets.Atlas.GAME);
        surfaceRegion = atlas.findRegion(RegionNames.Game.BLOCK_SURFACE);
        groundRegion = atlas.findRegion(RegionNames.Game.BLOCK_GROUND);

        uiViewport = new StretchViewport(Cfg.Ui.WIDTH, Cfg.Ui.HEIGHT);

        Skin skin = assetManager.get(Assets.Skins.UI);

        hud = new GameHud(assetManager, uiViewport, batch);
        overlays = new Overlays<>(uiViewport, batch, 0x000000BB);
        overlays.register(GameState.PAUSED,
                new PauseOverlay(skin, controller.getPauseCallback()));
        overlays.register(GameState.GAME_OVER,
                new GameOverOverlay(skin, controller.getGameOverCallback()));

        blastShader = assetManager.get(Assets.ShaderPrograms.BLAST);
        blurShader = assetManager.get(Assets.ShaderPrograms.BLUR);

        shapeRenderer = new ExtendedShapeRenderer();
    }

    private final Vector3 tmpBlastProjection = new Vector3();
    private final float[] tmpBlastEntries = new float[64];
    public void render(float delta) {
        Camera2D camera = controller.getCamera();
        Viewport viewport = controller.getViewport();

        viewport.apply();
        int fbIdx = 0;
        frameBufferManager.begin(frameBuffers[fbIdx]);
        fbIdx = ++fbIdx % frameBuffers.length;
        GdxUtils.clearScreen(Cfg.Colors.DARK_RED);

        shapeRenderer.setProjectionMatrix(controller.getCamera().getGdxCamera().combined);
        polygonBatch.setProjectionMatrix(camera.getGdxCamera().combined);
        batch.setProjectionMatrix(camera.getGdxCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderBackground(shapeRenderer, controller.getCamera());
        shapeRenderer.end();

        frameBufferManager.end();

        frameBufferManager.begin(frameBuffers[fbIdx]);
        fbIdx = ++fbIdx % frameBuffers.length;

        batch.begin();
        batch.setShader(blurShader);
        blurShader.setUniformf("u_radius", 0.01f);
        renderFrameBufferToScreen(batch, camera, frameBuffers[fbIdx]);
        batch.setShader(null);
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        renderBall(shapeRenderer, controller.getPlayer());
        renderBombs(shapeRenderer, controller.getBombs());
        shapeRenderer.end();

        batch.begin();
        controller.getExplosionEffect().draw(batch);
        controller.getExplosionGlowEffect().draw(batch);
        batch.end();

        polygonBatch.begin();
        renderGround(polygonBatch);
        polygonBatch.end();

        frameBufferManager.end();

        Array<BlastInstance> blasts = controller.getActiveBlastEffects();
        if (blasts.size > 0) {
            for (int i = 0; i < blasts.size; ++i) {
                BlastInstance blast = blasts.get(i);
                Vector2 blastPosition = blast.getPosition();
                tmpBlastProjection.set(blastPosition.x, blastPosition.y, 0f);
                camera.getGdxCamera().project(tmpBlastProjection);
                tmpBlastProjection.scl(1f / viewport.getScreenWidth(), 1f / viewport.getScreenHeight(), 1f);
                tmpBlastEntries[4 * i] = tmpBlastProjection.x;
                tmpBlastEntries[4 * i + 1] = tmpBlastProjection.y;
                tmpBlastEntries[4 * i + 2] = blast.getProgress();
                tmpBlastEntries[4 * i + 3] = blast.getRadius() / 6f;
            }

            frameBufferManager.begin(frameBuffers[fbIdx]);
            fbIdx = ++fbIdx % frameBuffers.length;

            batch.begin();
            batch.setShader(blastShader);
            blastShader.setUniform4fv("u_entries", tmpBlastEntries, 0, blasts.size * 4);
            blastShader.setUniformi("u_num_entries", blasts.size);
            renderFrameBufferToScreen(batch, camera, frameBuffers[fbIdx]);
            batch.end();
            batch.setShader(null);

            frameBufferManager.end();
        }

        batch.begin();
        float criticalHealthRatio = controller.getPlayer().getCriticalHealthRatio();
        if (criticalHealthRatio > 0f) {
            batch.setShader(blurShader);
            blurShader.setUniformf("u_radius", Interpolation.smooth.apply(0f, 0.01f, criticalHealthRatio));
            float tintFactor = Math.abs(MathUtils.sin(controller.getGameTime() * MathUtils.PI));
            float otherColorRatio = 1f - criticalHealthRatio / 2f * tintFactor;
            batch.setColor(1f, otherColorRatio, otherColorRatio, 1f);
        } else {
            batch.setShader(null);
        }
        fbIdx = ++fbIdx % frameBuffers.length;
        renderFrameBufferToScreen(batch, camera, frameBuffers[fbIdx]);
        batch.end();
        batch.setColor(Color.WHITE);
        batch.setShader(null);

        if (Cfg.DEBUG_MODE) {
            box2DRenderer.render(controller.getWorld(), camera.getGdxCamera().combined);
        }

        uiViewport.apply();
        batch.setProjectionMatrix(hud.getCamera().combined);
        renderHud(delta);
        overlays.update(controller.getState());
        overlays.render(batch);
    }

    private static void renderFrameBufferToScreen(Batch batch, Camera2D camera, FrameBuffer frameBuffer) {
        Texture sourceTexture = frameBuffer.getColorBufferTexture();
        GdxUtils.clearScreen();
        batch.draw(sourceTexture,
                camera.getPosition().x - camera.getGdxCamera().viewportWidth / 2,
                camera.getPosition().y - camera.getGdxCamera().viewportHeight / 2,
                camera.getGdxCamera().viewportWidth, camera.getGdxCamera().viewportHeight,
                0, 0, sourceTexture.getWidth(), sourceTexture.getHeight(), false, true);
    }

    private static final Color CITY_FRONT_COLOR = new Color(0.4f, 0f, 0f, 1f);
    private static final Color CITY_MID_COLOR = new Color(0.5f, 0f, 0f, 1f);
    private static final Color CITY_BACK_COLOR = new Color(0.6f, 0f, 0f, 1f);
    private static void renderBackground(ShapeRenderer renderer, Camera2D camera) {
        Vector2 cameraPosition = camera.getPosition();

        renderer.setColor(CITY_BACK_COLOR);
        renderCityLayer(renderer,  cameraPosition.x * 0.6f - 10f, cameraPosition.y * 0.9f);

        renderer.setColor(CITY_MID_COLOR);
        renderCityLayer(renderer, cameraPosition.x * 0.5f, 0.5f + cameraPosition.y * 0.8f);

        renderer.setColor(CITY_FRONT_COLOR);
        renderCityLayer(renderer, cameraPosition.x * 0.4f - 5f, 1f + cameraPosition.y * 0.7f);
    }

    private static void renderCityLayer(ShapeRenderer renderer, float offsetX, float offsetY) {
        renderer.rect(offsetX + -1f, offsetY - 100f, 2f, 103f);

        renderer.rect(offsetX + 3f, offsetY - 100f, 2.5f, 105f);
        renderer.rect(offsetX + 4.75f, offsetY - 100f, 2f, 102f);

        renderer.rect(offsetX + 7f, offsetY - 100f, 2f, 103f);

        renderer.rect(offsetX + 11f, offsetY - 100f, 2f, 102f);

        renderer.rect(offsetX + 13f, offsetY - 100f, 2.5f, 103f);
        renderer.rect(offsetX + 14.75f, offsetY - 100f, 2f, 102f);

        renderer.rect(offsetX + 17f, offsetY - 100f, 2f, 102.5f);
    }

    private static void renderBall(ShapeRenderer renderer, Player player) {
        if (!player.isDead()) {
            Vector2 position = player.getPosition();
            float radius = player.getRadius() * POLYGON_ZOOM;
            renderer.setColor(Color.WHITE);
            int count = PolygonUtils.polygon(POLYGON_BUFFER, radius, 9, position, player.getRotation());
            renderer.polygon(POLYGON_BUFFER, 0, count);
        }
    }

    private static void renderBombs(ShapeRenderer renderer, Array<Bomb> bombs) {
        for (Bomb bomb : bombs) {
            Vector2 position = bomb.getPosition();
            float radius = bomb.getBodyRadius() * POLYGON_ZOOM;
            if (!bomb.isFlashing()) {
                renderer.setColor(Color.BLACK);
            } else {
                renderer.setColor(Color.WHITE);
            }
            int count = PolygonUtils.polygon(POLYGON_BUFFER, radius, bomb.getBodySegments(), position, bomb.getRotation());
            renderer.polygon(POLYGON_BUFFER, 0, count);
            if (bomb.isSticky()) {
                count = PolygonUtils.spikes(POLYGON_BUFFER, radius * 0.66f, radius * 1.33f, bomb.getBodySegments(), position, bomb.getRotation());
                renderer.polygon(POLYGON_BUFFER, 0, count);
            }
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
