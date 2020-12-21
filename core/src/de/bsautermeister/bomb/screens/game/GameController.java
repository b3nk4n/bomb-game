package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import de.bsautermeister.bomb.BombGame;
import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.assets.Assets;
import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.contact.WorldContactListener;
import de.bsautermeister.bomb.core.GameObjectState;
import de.bsautermeister.bomb.effects.ManagedPooledEffect;
import de.bsautermeister.bomb.factories.BombFactory;
import de.bsautermeister.bomb.factories.BombFactoryImpl;
import de.bsautermeister.bomb.objects.BlastInstance;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.BounceStickyBomb;
import de.bsautermeister.bomb.objects.ClusterBomb;
import de.bsautermeister.bomb.objects.ClusterFragmentBomb;
import de.bsautermeister.bomb.objects.Fragment;
import de.bsautermeister.bomb.objects.FragmentData;
import de.bsautermeister.bomb.objects.Ground;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.objects.StickyBomb;
import de.bsautermeister.bomb.objects.TimedBomb;
import de.bsautermeister.bomb.screens.game.overlay.GameOverOverlay;
import de.bsautermeister.bomb.screens.game.overlay.PauseOverlay;
import de.bsautermeister.bomb.serializers.ArraySerializer;
import de.bsautermeister.bomb.serializers.Vector2Serializer;
import de.bsautermeister.bomb.serializers.Vector3Serializer;

public class GameController implements Disposable {

    private static final Logger LOG = new Logger(GameController.class.getSimpleName(), Cfg.LOG_LEVEL);

    private final BombGame game;
    private OrthographicCamera camera;
    private final Viewport viewport;

    private final World world;
    private Player player;
    private Ground ground;
    private final BombFactory bombFactory;
    private final Array<Bomb> bombs = new Array<>();

    private GameObjectState<GameState> state;

    private boolean markBackToMenu;
    private boolean markRestartGame;

    private static final float INITIAL_BOMB_EMIT_DELAY = 3f;
    private static final float MIN_BOMB_EMIT_DELAY = 1f;
    private float bombEmitTimer = INITIAL_BOMB_EMIT_DELAY;
    private float gameTime = 0f;

    private final Array<BlastInstance> activeBlastEffects = new Array<>();

    private final ManagedPooledEffect explosionEffect;

    private final Sound explosionSound;

    private long heartbeatSoundId = -1;
    private final Sound heartbeatSound;

    private final GameScreenCallbacks gameScreenCallbacks;

    private final PauseOverlay.Callback pauseCallback = new PauseOverlay.Callback() {
        @Override
        public void quit() {
            markBackToMenu = true;
        }

        @Override
        public void resume() {
            state.set(GameState.PLAYING);
        }
    };

    private final GameOverOverlay.Callback gameOverCallback = new GameOverOverlay.Callback() {
        @Override
        public void quit() {
            markBackToMenu = true;
        }

        @Override
        public void restart() {
            markRestartGame = true;
        }
    };

    private final Kryo kryo;

    public GameController(BombGame game, GameScreenCallbacks gameScreenCallbacks, AssetManager assetManager) {
        this.game = game;
        this.gameScreenCallbacks = gameScreenCallbacks;

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Cfg.VIEWPORT_WORLD_WIDTH_PPM, Cfg.VIEWPORT_WORLD_HEIGHT_PPM, camera);

        world = new World(new Vector2(0, -Cfg.GRAVITY), true);
        world.setContactListener(new WorldContactListener());
        createWorldBoundsBodies(world);

        bombFactory = new BombFactoryImpl(world);

        ParticleEffect explosion = assetManager.get(Assets.Effects.EXPLOSION);
        explosionEffect = new ManagedPooledEffect(explosion);

        explosionSound = assetManager.get(Assets.Sounds.EXPLOSION);
        heartbeatSound = assetManager.get(Assets.Sounds.HEARTBEAT);

        kryo = new Kryo();
        kryo.setRegistrationRequired(false);
        kryo.register(Vector2.class, new Vector2Serializer());
        kryo.register(Vector3.class, new Vector3Serializer());
        kryo.register(Array.class, new ArraySerializer());
        kryo.register(Player.class, new Player.KryoSerializer(world));
        kryo.register(Ground.class, new Ground.KryoSerializer(world));
        kryo.register(Fragment.class, new Fragment.KryoSerializer(world));
        kryo.register(FragmentData.class, new FragmentData.KryoSerializer());
        kryo.register(BlastInstance.class, new BlastInstance.KryoSerializer());
        kryo.register(TimedBomb.class, new TimedBomb.KryoSerializer(world));
        kryo.register(StickyBomb.class, new StickyBomb.KryoSerializer(world));
        kryo.register(ClusterBomb.class, new ClusterBomb.KryoSerializer(world));
        kryo.register(ClusterFragmentBomb.class, new ClusterFragmentBomb.KryoSerializer(world));
        kryo.register(BounceStickyBomb.class, new BounceStickyBomb.KryoSerializer(world));
    }

    public void initialize() {
        player = new Player(world, Cfg.PLAYER_RADIUS_PPM);
        player.setTransform(Cfg.PLAYER_START_POSITION, 0f);
        ground = new Ground(world, Cfg.GROUND_FRAGMENTS_NUM_COLS, Cfg.GROUND_FRAGMENTS_NUM_COMPLETE_ROWS, Cfg.GROUND_FRAGMENT_SIZE_PPM);

        state = new GameObjectState<>(GameState.PLAYING);
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

        if (markRestartGame) {
            markRestartGame = false;
            gameScreenCallbacks.restartGame();
            return;
        }

        if (state.is(GameState.PAUSED)) {
            return;
        }

        if (!state.is(GameState.GAME_OVER)) {
            gameTime += delta;
            handleInput();
            player.update(delta);
            updateCamera();
            updateBombEmitter(delta);

            float criticalHealthRatio = player.getCriticalHealthRatio();
            if (criticalHealthRatio > 0f) {
                float volume = Interpolation.pow5Out.apply(criticalHealthRatio);
                if (heartbeatSoundId == -1) {
                    heartbeatSoundId = heartbeatSound.loop(volume);
                } else {
                    heartbeatSound.setVolume(heartbeatSoundId, volume);
                }
            } else if (heartbeatSoundId != -1) {
                heartbeatSound.stop(heartbeatSoundId);
                heartbeatSoundId = -1;
            }
        }

        updateEnvironment(delta);
        explosionEffect.update(delta);

        world.step(delta, 6, 2);
    }

    private void updateBombEmitter(float delta) {
        bombEmitTimer -= delta;

        if (bombEmitTimer < 0) {
            float delay = INITIAL_BOMB_EMIT_DELAY - gameTime * 0.005f - 0.5f + MathUtils.random();
            bombEmitTimer = Math.max(MIN_BOMB_EMIT_DELAY, delay);
            emitBomb();
        }
    }

    private void emitBomb() {
        Bomb bomb = bombFactory.createRandomBomb();
        Vector2 position = new Vector2(
                MathUtils.random(bomb.getBodyRadius() / Cfg.PPM, Cfg.WORLD_WIDTH_PPM - bomb.getBodyRadius() / Cfg.PPM),
                20f / Cfg.PPM
        );
        float angleRad = MathUtils.random(0, MathUtils.PI2);
        bomb.setTransform(position, angleRad);
        bombs.add(bomb);
    }

    private void updateEnvironment(float delta) {
        ground.update();

        for (int i = bombs.size - 1; i >= 0; --i) {
            Bomb bomb = bombs.get(i);
            bomb.update(delta);

            if (bomb.doExplode()) {
                Vector2 bombPosition = bomb.getPosition();
                ground.impact(bombPosition, bomb.getDetonationRadius());

                if (player.impact(bombPosition, bomb.getDetonationRadius())) {
                    state.set(GameState.GAME_OVER);
                }

                for (Bomb otherBomb : bombs) {
                    if (otherBomb == bomb) continue;

                    otherBomb.impact(bombPosition, bomb.getDetonationRadius());
                }

                bombs.addAll(bomb.releaseBombs());

                activeBlastEffects.add(new BlastInstance(bombPosition, bomb.getDetonationRadius(), 2.5f));
                explosionEffect.emit(bombPosition, 0.0066f * bomb.getDetonationRadius());
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

    public void save() {
        if (state.is(GameState.GAME_OVER)) {
            return;
        }

        // set state to paused before saving
        state.set(GameState.PAUSED);

        try {
            File file = game.getGameFile();
            Output output = new Output(new FileOutputStream(file));
            output.writeFloat(gameTime);
            kryo.writeObject(output, state);
            kryo.writeObject(output, camera.position);
            kryo.writeObject(output, player);
            kryo.writeObject(output, ground);
            kryo.writeObject(output, activeBlastEffects);
            kryo.writeObject(output, bombs);
            output.close();
        } catch (Exception e) {
            LOG.error("Failed to save game.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void load() {
        File file = game.getGameFile();
        if (!file.exists()) {
            return;
        }

        try {
            com.esotericsoftware.kryo.io.Input input = new com.esotericsoftware.kryo.io.Input(
                    new FileInputStream(file));
            gameTime = input.readFloat();
            state = kryo.readObject(input, GameObjectState.class);
            camera.position.set(kryo.readObject(input, Vector3.class));
            player = kryo.readObject(input, Player.class);
            ground = kryo.readObject(input, Ground.class);
            activeBlastEffects.clear();
            activeBlastEffects.addAll(kryo.readObject(input, Array.class));
            bombs.clear();
            bombs.addAll(kryo.readObject(input, Array.class));
            input.close();
        } catch (FileNotFoundException e) {
            LOG.error("Failed to load game.", e);
        }

        // save the file after the game was restored:
        if (!file.delete()) {
            LOG.error("Failed to delete saved game.");
        }
    }

    @Override
    public void dispose() {

    }

    private void handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            state.set(GameState.PAUSED);
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
        return state.current();
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
