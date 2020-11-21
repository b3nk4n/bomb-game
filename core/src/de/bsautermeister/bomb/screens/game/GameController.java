package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.WorldContactListener;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;

public class GameController implements Disposable {

    private OrthographicCamera camera;
    private Viewport viewport;

    private World world;
    private Player player;
    private Ground ground;

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
        ground.update(delta);
        updateCamera();

        world.step(delta, 6, 2);
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

    public Ground getGround() {
        return ground;
    }

    public World getWorld() {
        return world;
    }
}
