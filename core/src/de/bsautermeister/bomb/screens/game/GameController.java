package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.contact.WorldContactListener;
import de.bsautermeister.bomb.effects.ManagedPooledEffect;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.ClusterBomb;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.objects.TimedBomb;
import de.bsautermeister.bomb.screens.game.overlay.GameOverOverlay;
import de.bsautermeister.bomb.screens.game.overlay.PauseOverlay;

public class GameController implements Disposable {

    private final OrthographicCamera camera;
    private final Viewport viewport;

    private final World world;
    private final Player player;
    private final Ground ground;
    private final Array<Bomb> bombs = new Array<>();

    private GameState state;

    private boolean markBackToMenu;

    private static final float BOMB_EMIT_DELAY = 2f;
    private float bombEmitTimer = BOMB_EMIT_DELAY;

    private final Array<BlastInstance> activeBlastEffects = new Array<>();

    private final ManagedPooledEffect explosionEffect;

    private final Sound explosionSound;

    public static class BlastInstance {
        private final Vector2 position;
        private final float radius;
        private final float initialTtl;
        private float ttl;

        public BlastInstance(float x, float y, float radius, float ttl) {
            this.position = new Vector2(x, y);
            this.radius = radius;
            this.initialTtl = ttl;
            this.ttl = ttl;
        }

        public void update(float delta) {
            ttl -= delta;
        }

        public Vector2 getPosition() {
            return position;
        }

        public float getProgress() {
            return MathUtils.clamp((initialTtl - ttl) / initialTtl, 0f, 1f);
        }

        public float getRadius() {
            return radius;
        }

        public boolean isExpired() {
            return ttl <= 0;
        }
    }

    private final GameScreenCallbacks gameScreenCallbacks;

    private final PauseOverlay.Callback pauseCallback = new PauseOverlay.Callback() {
        @Override
        public void quit() {
            markBackToMenu = true;
        }

        @Override
        public void resume() {
            state = GameState.PLAYING;
        }
    };

    private final GameOverOverlay.Callback gameOverCallback = new GameOverOverlay.Callback() {
        @Override
        public void quit() {
            markBackToMenu = true;
        }

        @Override
        public void restart() {
            // TODO reset game
            state = GameState.PLAYING;
            player.reset();
        }
    };

    public GameController(GameScreenCallbacks gameScreenCallbacks, AssetManager assetManager) {
        this.gameScreenCallbacks = gameScreenCallbacks;

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Cfg.VIEWPORT_WORLD_WIDTH_PPM, Cfg.VIEWPORT_WORLD_HEIGHT_PPM, camera);

        world = new World(new Vector2(0, -Cfg.GRAVITY), true);
        world.setContactListener(new WorldContactListener());
        createWorldBoundsBodies(world);

        player = new Player(world, new Vector2(viewport.getWorldWidth() / 2, 5f / Cfg.PPM), Cfg.PLAYER_RADIUS_PPM);
        ground = new Ground(world, Cfg.GROUND_FRAGMENTS_NUM_COLS, Cfg.GROUND_FRAGMENTS_NUM_COMPLETE_ROWS, Cfg.GROUND_FRAGMENT_SIZE_PPM);

        ParticleEffect explosion = assetManager.get(Assets.Effects.EXPLOSION);
        explosionEffect = new ManagedPooledEffect(explosion);

        explosionSound = assetManager.get(Assets.Sounds.EXPLOSION);

        state = GameState.PLAYING;
    }

    private void createWorldBoundsBodies(World world) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.filter.categoryBits = Bits.GROUND;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.BALL;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f, 1e10f);
        fixtureDef.shape = shape;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(-1f, 0f);
        Body leftBounds = world.createBody(bodyDef);
        leftBounds.createFixture(fixtureDef);

        bodyDef.position.set(Cfg.WORLD_WIDTH_PPM + 1f, 0f);
        Body rightBounds = world.createBody(bodyDef);
        rightBounds.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float delta) {
        if (markBackToMenu) {
            markBackToMenu = false;
            gameScreenCallbacks.backToMenu();
            return;
        }

        if (state.isPaused()) {
            return;
        }

        if (!state.isGameOver()) {
            handleInput();
            player.update(delta);
            updateCamera();
        }

        updateBombEmitter(delta);
        updateEnvironment(delta);
        explosionEffect.update(delta);

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
                Vector2 bombPosition = bomb.getPosition();
                ground.impact(bombPosition, bomb.getDetonationRadius());

                if (player.impact(bombPosition, bomb.getDetonationRadius())) {
                    state = GameState.GAME_OVER;
                }

                for (Bomb otherBomb : bombs) {
                    if (otherBomb == bomb) continue;

                    otherBomb.impact(bombPosition, bomb.getDetonationRadius());
                }

                bombs.addAll(bomb.releaseBombs());

                activeBlastEffects.add(new BlastInstance(bombPosition.x, bombPosition.y, bomb.getDetonationRadius(), 2.5f));
                explosionEffect.emit(bombPosition.x, bombPosition.y, 0.0066f * bomb.getDetonationRadius());
                explosionSound.play(
                        MathUtils.clamp(bomb.getDetonationRadius() / 2, 0f, 1f),
                        MathUtils.random(0.9f, 1.1f), 0f);
                bomb.dispose();
                bombs.removeValue(bomb, true);
            }
        }

        for (int i = activeBlastEffects.size - 1; i >= 0; --i) {
            BlastInstance explosionInstance = activeBlastEffects.get(i);
            explosionInstance.update(delta);

            if (explosionInstance.isExpired()) {
                activeBlastEffects.removeIndex(i);
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
        handlePauseInput();

        boolean upPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean leftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean rightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        for (int pointer = 0; pointer < Gdx.input.getMaxPointers(); ++pointer) {
            if (!Gdx.input.isTouched(pointer)) {
                continue;
            }
            float x = Gdx.input.getX(pointer);
            float y = Gdx.input.getY(pointer);

            x = x / Gdx.graphics.getWidth();
            y = y / Gdx.graphics.getHeight();

            if (y < 0.5) {
                upPressed = true;
            } else {
                if (x <= 0.5) {
                    leftPressed = true;
                } else {
                    rightPressed = true;
                }
            }
        }

        player.control(upPressed, leftPressed, rightPressed);
    }

    private void emitBomb() {
        float bodyRadius = MathUtils.random(0.5f, 1.0f);
        float detonationRadius = bodyRadius * 15;
        float tickingTime = MathUtils.random(2f, 5f);
        float x = MathUtils.random(bodyRadius / Cfg.PPM, Cfg.WORLD_WIDTH_PPM - bodyRadius / Cfg.PPM);
        float y = 20f / Cfg.PPM;

        if (MathUtils.random() < 0.5) {
            bombs.add(new TimedBomb(world, x, y, tickingTime, bodyRadius / Cfg.PPM, detonationRadius / Cfg.PPM));
        } else {
            bombs.add(new ClusterBomb(world, x, y, tickingTime, bodyRadius / Cfg.PPM, detonationRadius / Cfg.PPM));
        }
    }

    public void save() {
        if (state.isGameOver()) {
            // TODO delete saved game
            return;
        }

        state = GameState.PAUSED;

        // TODO save game
    }

    @Override
    public void dispose() {

    }

    private void handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            state = GameState.PAUSED;
        }
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

    public Array<BlastInstance> getActiveBlastEffects() {
        return activeBlastEffects;
    }

    public Ground getGround() {
        return ground;
    }

    public World getWorld() {
        return world;
    }

    public GameState getState() {
        return state;
    }

    public PauseOverlay.Callback getPauseCallback() {
        return pauseCallback;
    }

    public GameOverOverlay.Callback getGameOverCallback() {
        return gameOverCallback;
    }

    public ManagedPooledEffect getExplosionEffect() {
        return explosionEffect;
    }
}
