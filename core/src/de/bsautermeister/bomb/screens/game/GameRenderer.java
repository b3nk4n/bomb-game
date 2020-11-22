package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.assets.RegionNames;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Fragment;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.utils.GdxUtils;

public class GameRenderer implements Disposable {

    private final static short[] TRIANGULATION_IDENTITY = new short[] { 2, 1, 0 };

    private final SpriteBatch batch;
    private final GameController controller;
    private final PolygonSpriteBatch polygonBatch = new PolygonSpriteBatch(); // TODO move to other SpriteBatch, or even replace it?

    private final Box2DDebugRenderer box2DRenderer;

    private final TextureRegion ballRegion;
    private final TextureRegion bombRegion;
    private final TextureRegion surfaceRegion;
    private final TextureRegion groundRegion;

    public GameRenderer(SpriteBatch batch, AssetManager assetManager, GameController controller) {
        this.batch = batch;
        this.controller = controller;

        this.box2DRenderer = new Box2DDebugRenderer(true, true, false, true, true, true);

        TextureAtlas atlas =  assetManager.get(Assets.Atlas.GAME);
        surfaceRegion = atlas.findRegion(RegionNames.Game.BLOCK_SURFACE);
        groundRegion = atlas.findRegion(RegionNames.Game.BLOCK_GROUND);
        ballRegion = atlas.findRegion(RegionNames.Game.BALL);
        bombRegion = atlas.findRegion(RegionNames.Game.BOMB);
    }

    public void render(float delta) {
        GdxUtils.clearScreen();

        Camera camera = controller.getCamera();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        renderBall(batch);
        renderBombs(batch);

        batch.end();

        polygonBatch.setProjectionMatrix(camera.combined);
        polygonBatch.begin();

        renderGround(polygonBatch);

        polygonBatch.end();

        if (Cfg.DEBUG_MODE) {
            box2DRenderer.render(controller.getWorld(), camera.combined);
        }
    }

    private void renderBall(SpriteBatch batch) {
        Player player = controller.getPlayer();
        Vector2 position = player.getPosition();
        float radius = player.getRadius();
        batch.draw(ballRegion,
                position.x - radius, position.y - radius,
                radius, radius,
                radius * 2f, radius * 2f,
                1f, 1f, player.getRotation());
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

    public void resize(int width, int height) {
        controller.getViewport().update(width, height, false);
    }

    @Override
    public void dispose() {
        polygonBatch.dispose();
    }
}
