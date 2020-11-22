package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.WorldContactListener;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;

public class GameController implements Disposable {

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final World world;
    private final Player player;
    private final Ground ground;
    private final Array<Bomb> bombs = new Array<>();

    private static final float BOMB_EMIT_DELAY = 5f;
    private float bombEmitTimer = BOMB_EMIT_DELAY;

    public GameController() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(Cfg.VIEWPORT_WORLD_WIDTH_PPM, Cfg.VIEWPORT_WORLD_HEIGHT_PPM, camera);

        world = new World(new Vector2(0, -Cfg.GRAVITY), true);
        world.setContactListener(new WorldContactListener());

        player = new Player(world, new Vector2(viewport.getWorldWidth() / 2, 5f / Cfg.PPM), Cfg.PLAYER_RADIUS_PPM);
        ground = new Ground(world, Cfg.GROUND_FRAGMENTS_NUM_COLS, Cfg.GROUND_FRAGMENTS_NUM_COMPLETE_ROWS, Cfg.GROUND_FRAGMENT_SIZE_PPM);
    }

    public void update(float delta) {
        handleInput();
        player.update(delta);
        updateCamera();

        updateBombEmitter(delta);
        updateEnvironment(delta);

        world.step(delta, 6, 2);
    }

    private void updateBombEmitter(float delta) {
        bombEmitTimer -= delta;

        if (bombEmitTimer < 0) {
            bombEmitTimer = BOMB_EMIT_DELAY;
            emitBomb();
        }
    }

    private void updateEnvironment(float delta) {
        ground.update(delta);

        for (int i = bombs.size - 1; i >= 0; --i) {
            Bomb bomb = bombs.get(i);
            bomb.update(delta);

            if (bomb.doExplode()) {
                ground.impact(bomb.getPosition(), bomb.getDetonationRadius());
                bomb.dispose();
                bombs.removeValue(bomb, true);
            }
        }
    }

    private void updateCamera() {
        camera.position.x = camera.position.x - (camera.position.x - player.getPosition().x) * 0.1f;
        camera.position.y = camera.position.y - (camera.position.y - player.getPosition().y + viewport.getWorldHeight() * 0.075f) * 0.166f;

        // check camera in bounds (X)
        if (camera.position.x - viewport.getWorldWidth() / 2 < 0) {
            camera.position.x = viewport.getWorldWidth() / 2;
        } else if (camera.position.x + viewport.getWorldWidth() / 2 > Cfg.WORLD_WIDTH_PPM) {
            camera.position.x = Cfg.WORLD_WIDTH_PPM - viewport.getWorldWidth() / 2;
            camera.position.x = Cfg.WORLD_WIDTH_PPM - viewport.getWorldWidth() / 2;
        }

        camera.update();
    }

    private void handleInput() {
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        boolean downPressed = Gdx.input.isKeyJustPressed(Input.Keys.DOWN);

        if (downPressed) {
            ground.impact(player.getPosition(), player.getRadius() * 2);
        }

        player.control(upPressed, leftPressed, rightPressed);
    }

    private void emitBomb() {
        float bodyRadius = MathUtils.random(0.5f, 1.0f);
        float detonationRadius = bodyRadius * 15;
        float ttl = MathUtils.random(1f, 5f);
        float x = MathUtils.random(bodyRadius / Cfg.PPM, Cfg.WORLD_WIDTH_PPM - bodyRadius / Cfg.PPM);
        float y = 20f / Cfg.PPM;

        bombs.add(new Bomb(world, x, y, ttl, bodyRadius / Cfg.PPM, detonationRadius / Cfg.PPM));
    }

    public void save() {

    }

    @Override
    public void dispose() {

    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Player getPlayer() {
        return player;
    }

    public Array<Bomb> getBombs() {
        return bombs;
    }

    public Ground getGround() {
        return ground;
    }

    public World getWorld() {
        return world;
    }
}
