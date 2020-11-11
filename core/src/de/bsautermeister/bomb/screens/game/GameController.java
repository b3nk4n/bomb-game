package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Circle;
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
        viewport = new StretchViewport(Cfg.WORLD_WIDTH / Cfg.PPM, Cfg.WORLD_HEIGHT / Cfg.PPM, camera);

        world = new World(new Vector2(0, -Cfg.GRAVITY), true);
        world.setContactListener(new WorldContactListener());

        player = new Player(world, new Vector2(0f, 0f), 1f / Cfg.PPM);
        ground = new Ground(world, 11, 3);
    }

    public void update(float delta) {
        handleInput();
        player.update(delta);
        camera.update();
        world.step(delta, 6, 2);
    }

    private void handleInput() {
        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        boolean spacePressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE);

        if (spacePressed) {
            ground.impact(new Circle(player.getPosition(), player.getRadius() * 2));
        }

        player.control(upPressed, leftPressed, rightPressed);
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

    public World getWorld() {
        return world;
    }
}
