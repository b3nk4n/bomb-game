package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ContactListener;
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
import de.bsautermeister.bomb.audio.LoopSound;
import de.bsautermeister.bomb.audio.MusicPlayer;
import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.contact.WorldContactListener;
import de.bsautermeister.bomb.core.GameObjectState;
import de.bsautermeister.bomb.core.graphics.BoundedCamera2D;
import de.bsautermeister.bomb.core.graphics.Camera2D;
import de.bsautermeister.bomb.core.graphics.OrthographicCamera2D;
import de.bsautermeister.bomb.core.graphics.ShakableCamera2D;
import de.bsautermeister.bomb.effects.ManagedPooledBox2DEffect;
import de.bsautermeister.bomb.effects.ManagedPooledEffect;
import de.bsautermeister.bomb.effects.ParticleEffectBox2D;
import de.bsautermeister.bomb.factories.BombFactory;
import de.bsautermeister.bomb.factories.BombFactoryImpl;
import de.bsautermeister.bomb.objects.AirStrikeBomb;
import de.bsautermeister.bomb.objects.AirStrikeTargetMarker;
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

import static com.badlogic.gdx.Input.Keys.SPACE;

public class GameController implements Disposable {

    private static final Logger LOG = new Logger(GameController.class.getSimpleName(), Cfg.LOG_LEVEL);

    private final BombGame game;
    private ShakableCamera2D camera;
    private final Viewport viewport;

    private final World world;
    private Player player;
    private Ground ground;
    private final BombFactory bombFactory;
    private final Array<Bomb> bombs = new Array<>();

    private static final float MIN_AIR_STRIKE_DELAY = Player.MAX_CAMP_TIME + 5f;
    private float airStrikeUnlockTimer = 0f;
    private final AirStrikeManager airStrikeManager;
    private final Array<AirStrikeTargetMarker> airStrikeTargets = new Array<>();

    private GameObjectState<GameState> state;

    private boolean markBackToMenu;
    private boolean markRestartGame;

    private static final float BOMB_START_Y = 32f / Cfg.World.PPM;
    private static final float INITIAL_BOMB_EMIT_DELAY = 3f;
    private static final float MIN_BOMB_EMIT_DELAY = 1f;
    private float bombEmitTimer = INITIAL_BOMB_EMIT_DELAY;
    private float gameTime = 0f;

    private final Array<BlastInstance> activeBlastEffects = new Array<>();

    private ManagedPooledBox2DEffect explosionEffect;
    private ManagedPooledBox2DEffect playerParticlesEffect;
    private final ManagedPooledEffect explosionGlowEffect;

    private final Sound explosionSound;
    private final LoopSound heartbeatSound;
    private final Sound hitSound;

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

        camera = new ShakableCamera2D(
                new BoundedCamera2D(
                        new OrthographicCamera2D(),
                        Cfg.World.VIEWPORT_WIDTH_PPM / 2f,
                        Cfg.World.WIDTH_PPM - Cfg.World.VIEWPORT_WIDTH_PPM / 2f,
                        Float.MAX_VALUE, -Float.MAX_VALUE)
        );
        viewport = new StretchViewport(Cfg.World.VIEWPORT_WIDTH_PPM, Cfg.World.VIEWPORT_HEIGHT_PPM, camera.getGdxCamera());

        world = new World(new Vector2(0, -Cfg.World.GRAVITY), true);

        ContactListener contactListener = new WorldContactListener(new WorldContactListener.Callbacks() {
            @Override
            public void hitGround(Bomb bomb, float strength) {
                Vector2 bombPosition = bomb.getPosition();
                float volume = camera.isInView(bombPosition)
                        ? strength * 0.5f : strength * 0.1f;
                hitSound.play(volume);
            }
        });

        world.setContactListener(contactListener);
        createWorldBoundsWallBodies(world);

        bombFactory = new BombFactoryImpl(world);

        // TODO load async
        explosionEffect = blockedLoadPooledBox2DEffect(
                assetManager, Assets.Effects.LazyEffect.EXPLOSION_PARTICLES);
        playerParticlesEffect = blockedLoadPooledBox2DEffect(
                assetManager, Assets.Effects.LazyEffect.PLAYER_PARTICLES);

        ParticleEffect explosionGlow = assetManager.get(Assets.Effects.EXPLOSION_GLOW);
        explosionGlowEffect = new ManagedPooledEffect(explosionGlow);

        explosionSound = assetManager.get(Assets.Sounds.EXPLOSION);
        heartbeatSound = new LoopSound(assetManager.get(Assets.Sounds.HEARTBEAT));
        hitSound = assetManager.get(Assets.Sounds.HIT);

        airStrikeManager = new AirStrikeManager(world);

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
        kryo.register(AirStrikeBomb.class, new AirStrikeBomb.KryoSerializer(world));
        kryo.register(AirStrikeTargetMarker.class, new AirStrikeTargetMarker.KryoSerializer());
        kryo.register(GameObjectState.class, new GameObjectState.KryoSerializer());
    }

    private ManagedPooledBox2DEffect blockedLoadPooledBox2DEffect(AssetManager assetManager, Assets.Effects.LazyEffect lazyEffect) {
        AssetDescriptor<ParticleEffectBox2D> explosionParticlesDescriptor = Assets.Effects.lazyEffect(
                world, lazyEffect);
        if (assetManager.isLoaded(explosionParticlesDescriptor)) {
            // unload this effect if it already exists, because it would be associated with the
            // previous Box2D world instance
            assetManager.unload(explosionParticlesDescriptor.fileName);
        }
        assetManager.load(explosionParticlesDescriptor);
        ParticleEffectBox2D effect = assetManager.finishLoadingAsset(explosionParticlesDescriptor);
        return new ManagedPooledBox2DEffect(effect);
    }

    public void initialize(boolean resume) {
        if (resume) {
            load();
        }

        if (player == null) {
            player = new Player(world, Cfg.Player.RADIUS_PPM);
            player.setTransform(Cfg.Player.START_POSITION, 0f);
            camera.setPosition(player.getPosition());
        }

        if (ground == null) {
            ground = new Ground(world, Cfg.Ground.FRAGMENTS_NUM_COLS, Cfg.Ground.FRAGMENTS_NUM_COMPLETE_ROWS, Cfg.Ground.FRAGMENT_SIZE_PPM);
        }

        if (!game.getMusicPlayer().isSelected(Assets.Music.GAME_SONG)) {
            game.getMusicPlayer().selectSmoothLoopedMusic(Assets.Music.GAME_SONG, 85f);
            game.getMusicPlayer().setVolume(MusicPlayer.MAX_VOLUME, true);
            game.getMusicPlayer().playFromBeginning();
        }

        if (state == null) {
            state = new GameObjectState<>(GameState.PLAYING);
        }
        state.setStateCallback(new GameObjectState.StateCallback<GameState>() {
            @Override
            public void changed(GameState previousState, GameState newState) {
                if (newState == GameState.PAUSED) {
                    game.getMusicPlayer().setVolume(0.1f, false);
                }
                if (previousState == GameState.PAUSED && newState == GameState.PLAYING) {
                    game.getMusicPlayer().setVolume(MusicPlayer.MAX_VOLUME, false);
                }
                if (!previousState.anyGameOver() && newState.anyGameOver()) {
                    game.getMusicPlayer().selectSmoothLoopedMusic(Assets.Music.MENU_SONG, 46.5f);
                    game.getMusicPlayer().setVolume(MusicPlayer.MAX_VOLUME, true);
                    game.getMusicPlayer().playFromBeginning();
                }
            }
        });
    }

    private void createWorldBoundsWallBodies(World world) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.1f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.filter.categoryBits = Bits.WALL;
        fixtureDef.filter.maskBits = Bits.OBJECTS;
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f, 1e7f);
        fixtureDef.shape = shape;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(-1f, -9e6f);
        Body leftBounds = world.createBody(bodyDef);
        leftBounds.createFixture(fixtureDef);

        bodyDef.position.set(Cfg.World.WIDTH_PPM + 1f, -9e6f);
        Body rightBounds = world.createBody(bodyDef);
        rightBounds.createFixture(fixtureDef);
        shape.dispose();
    }

    public void update(float delta) {
        state.update(delta);

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

        handlePauseInput();

        if (state.is(GameState.PAUSED)) {
            heartbeatSound.stop();
            return;
        }

        if (!state.isAnyOf(GameState.GAME_OVER, GameState.PLAYER_JUST_DIED)) {
            gameTime += delta;
            handleInput();
            player.update(delta);
            updateAirStrike(delta);
            updateCamera(delta);
            updateBombEmitter(delta);

            float criticalHealthRatio = player.getCriticalHealthRatio();
            float musicVolume;
            if (criticalHealthRatio > 0f) {
                float volume = Interpolation.pow5Out.apply(criticalHealthRatio);
                heartbeatSound.loop(volume);
                musicVolume = Interpolation.pow5Out.apply(MusicPlayer.MAX_VOLUME, 0.1f, criticalHealthRatio);
            } else {
                heartbeatSound.stop();
                musicVolume = MusicPlayer.MAX_VOLUME;
            }
            game.getMusicPlayer().setVolume(musicVolume, false);
        }

        if (state.is(GameState.PLAYER_JUST_DIED) && state.timer() > Cfg.GAME_OVER_DELAY) {
            state.set(GameState.GAME_OVER);
        }

        updateAirStrikeTargetMarkers(delta);
        updateEnvironment(delta);
        explosionEffect.update(delta);
        playerParticlesEffect.update(delta);
        explosionGlowEffect.update(delta);

        world.step(delta, 6, 2);
    }

    private void updateAirStrike(float delta) {
        airStrikeUnlockTimer -= delta;

        if (airStrikeUnlockTimer < 0 && player.isCamping()) {
            airStrikeUnlockTimer = MIN_AIR_STRIKE_DELAY;

            // launch new air strike
            Vector2 playerPosition = player.getPosition();
            airStrikeManager.request(playerPosition);
        }

        airStrikeManager.update(delta);

        if (airStrikeManager.isReady()) {
            AirStrikeManager.EmitInfo emitInfo = airStrikeManager.getTargetAndReset();
            airStrikeTargets.add(new AirStrikeTargetMarker(emitInfo.getTarget(), 1f));
            emitAirStrikeBomb(emitInfo);
        }
    }

    private void updateAirStrikeTargetMarkers(float delta) {
        for (int i = airStrikeTargets.size - 1; i >= 0; i--) {
            AirStrikeTargetMarker targetMarker = airStrikeTargets.get(i);
            targetMarker.update(delta);
            if (targetMarker.isReady()) {
                airStrikeTargets.removeIndex(i);
            }
        }
    }

    public Array<AirStrikeTargetMarker> getAirStrikeTargets() {
        return airStrikeTargets;
    }

    private void emitAirStrikeBomb(AirStrikeManager.EmitInfo emitInfo) {
        Bomb bomb = bombFactory.createAirStrikeBomb();
        bomb.setTransform(emitInfo.getStart(), emitInfo.getAngle());
        bomb.setLinearVelocity(emitInfo.getVelocity());
        bombs.add(bomb);
    }

    private void updateBombEmitter(float delta) {
        bombEmitTimer -= delta;

        if (bombEmitTimer < 0) {
            float delay = INITIAL_BOMB_EMIT_DELAY - gameTime * 0.005f - 0.5f + MathUtils.random();
            bombEmitTimer = Math.max(MIN_BOMB_EMIT_DELAY, delay);
            float x = MathUtils.random(Cfg.World.WIDTH_PPM);
            emitBomb(x);
        }
    }

    private final Vector2 tmpBombEmitPosition = new Vector2();
    private void emitBomb(float x) {
        Bomb bomb = bombFactory.createRandomBomb();
        float bodyRadiusPPM = bomb.getBodyRadius() / Cfg.World.PPM;
        tmpBombEmitPosition.set(
                MathUtils.clamp(x, bodyRadiusPPM, Cfg.World.WIDTH_PPM - bodyRadiusPPM),
                BOMB_START_Y
        );
        float angleRad = MathUtils.random(0, MathUtils.PI2);
        bomb.setTransform(tmpBombEmitPosition, angleRad);
        bombs.add(bomb);
    }

    private static float[] outRemovedVertices = new float[32 * 2 * Cfg.Ground.FRAGMENT_RESOLUTION * Cfg.Ground.FRAGMENT_RESOLUTION];
    private void updateEnvironment(float delta) {
        ground.update();

        for (int i = bombs.size - 1; i >= 0; --i) {
            Bomb bomb = bombs.get(i);
            bomb.update(delta);

            if (bomb.doExplode()) {
                Vector2 bombPosition = bomb.getPosition();

                if (!player.isDead() && player.impact(bombPosition, bomb.getDetonationRadius())) {
                    int vibrationMillis;
                    if (player.isDead()) {
                        playerParticlesEffect.emit(player.getPosition(), 0.0166f);
                        state.set(GameState.PLAYER_JUST_DIED);
                        vibrationMillis = 500;
                    } else {
                        vibrationMillis = 250;
                    }

                    if (game.getGameSettings().getVibration()) {
                        Gdx.input.vibrate(vibrationMillis);
                    }
                }

                for (Bomb otherBomb : bombs) {
                    if (otherBomb == bomb) continue;

                    otherBomb.impact(bombPosition, bomb.getDetonationRadius());
                }

                bombs.addAll(bomb.releaseBombs());

                activeBlastEffects.add(new BlastInstance(bombPosition, bomb.getDetonationRadius(), 1f));
                explosionGlowEffect.emit(bombPosition, 0.0066f * bomb.getDetonationRadius());

                float explosionVolume = camera.isInView(bombPosition)
                        ? MathUtils.clamp(bomb.getDetonationRadius() / 2, 0f, 1f)
                        : MathUtils.clamp(bomb.getDetonationRadius() / 8, 0f, 1f);

                explosionSound.play(
                        explosionVolume,
                        MathUtils.random(0.9f, 1.1f), 0f);

                bomb.dispose();
                bombs.removeIndex(i);

                float shakeTime = camera.isInView(bombPosition)
                        ? 1f : 0.5f;
                camera.shake(shakeTime);
            }
        }

        for (int i = activeBlastEffects.size - 1; i >= 0; --i) {
            BlastInstance explosionInstance = activeBlastEffects.get(i);
            explosionInstance.update(delta);

            float radialImpactProgress = Interpolation.fastSlow.apply(0.33f, 2f, explosionInstance.getProgress());
            if (radialImpactProgress <= 1f) {
                float currentRadius = explosionInstance.getRadius() * radialImpactProgress;
                int removed = ground.impact(outRemovedVertices, explosionInstance.getPosition(), currentRadius);
                if (removed > 0) {
                    for (int r = 0; r < removed; ++r) {
                        float x = outRemovedVertices[2 * r];
                        float y = outRemovedVertices[2 * r + 1];
                        explosionEffect.emit(x, y, 0.0166f);
                    }
                }
            }

            if (explosionInstance.isExpired()) {
                activeBlastEffects.removeIndex(i);
            }
        }
    }

    private void updateCamera(float delta) {
        Vector2 position = camera.getPosition();
        position.x -= (position.x - player.getPosition().x) * 0.0666f;
        position.y -= (position.y - player.getPosition().y) * 0.1f;
        camera.setPosition(position);
        camera.update(delta);
    }

    private void handleInput() {
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

        if (Cfg.DEBUG_MODE) {
            boolean space = Gdx.input.isKeyJustPressed(SPACE);
            if (space) {
                emitBomb(player.getPosition().x);
            }
        }

        player.control(upPressed, leftPressed, rightPressed);
    }

    public void pause() {
        heartbeatSound.stop();
        save();
    }

    private void save() {
        if (state.isAnyOf(GameState.GAME_OVER, GameState.PLAYER_JUST_DIED)) {
            return;
        }

        // set state to paused before saving
        state.set(GameState.PAUSED);

        try {
            File file = game.getGameFile();
            Output output = new Output(new FileOutputStream(file));
            output.writeFloat(gameTime);
            output.writeFloat(airStrikeUnlockTimer);
            kryo.writeObject(output, state);
            kryo.writeObject(output, camera.getPosition());
            kryo.writeObject(output, player);
            kryo.writeObject(output, ground);
            kryo.writeObject(output, activeBlastEffects);
            kryo.writeObject(output, bombs);
            airStrikeManager.write(kryo, output);
            kryo.writeObject(output, airStrikeTargets);
            game.getMusicPlayer().write(kryo, output);
            output.close();
        } catch (Exception e) {
            LOG.error("Failed to save game.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        File file = game.getGameFile();
        if (!file.exists()) {
            return;
        }

        try {
            com.esotericsoftware.kryo.io.Input input = new com.esotericsoftware.kryo.io.Input(
                    new FileInputStream(file));
            gameTime = input.readFloat();
            airStrikeUnlockTimer = input.readFloat();
            state = kryo.readObject(input, GameObjectState.class);
            camera.setPosition(kryo.readObject(input, Vector2.class));
            player = kryo.readObject(input, Player.class);
            ground = kryo.readObject(input, Ground.class);
            activeBlastEffects.clear();
            activeBlastEffects.addAll(kryo.readObject(input, Array.class));
            bombs.clear();
            bombs.addAll(kryo.readObject(input, Array.class));
            airStrikeManager.read(kryo, input);
            airStrikeTargets.clear();
            airStrikeTargets.addAll(kryo.readObject(input, Array.class));
            game.getMusicPlayer().read(kryo, input);
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

    public float getGameTime() {
        return gameTime;
    }

    public Camera2D getCamera() {
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

    public ManagedPooledBox2DEffect getExplosionEffect() {
        return explosionEffect;
    }

    public ManagedPooledBox2DEffect getPlayerParticlesEffect() {
        return playerParticlesEffect;
    }

    public ManagedPooledEffect getExplosionGlowEffect() {
        return explosionGlowEffect;
    }
}
