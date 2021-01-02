package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ClusterBomb extends Bomb {
    private static final Vector2[] RELEASE_CLUSTER_OFFSETS = new Vector2[] {
            new Vector2(0, 0f),
            new Vector2(-0.5f, 0f),
            new Vector2(0.5f, 0f)
    };

    private boolean ticking;
    private final float initialTickingTime;
    private float tickingTimer;

    public ClusterBomb(World world, float tickingTime, float bodyRadius, float detonationRadius) {
        super(world, bodyRadius, 4, detonationRadius, 1f);
        this.initialTickingTime = tickingTime;
        this.tickingTimer = initialTickingTime;
    }

    @Override
    public void update(float delta) {
        if (isTicking()) {
            tickingTimer = Math.max(0f, tickingTimer - delta);
        }
    }

    @Override
    public boolean doExplode() {
        return tickingTimer <= 0;
    }

    @Override
    public Bomb[] releaseBombs() {
        Bomb[] bombs = new Bomb[3];
        for (int i = 0; i < bombs.length; ++i) {
            Vector2 position = getPosition();
            position.add(RELEASE_CLUSTER_OFFSETS[i]);
            float theta = MathUtils.random(0.5f, MathUtils.PI - 0.5f);
            Vector2 velocity = new Vector2(MathUtils.cos(theta), MathUtils.sin(theta)).scl(MathUtils.random(7.5f, 12f));
            Bomb clusterFragment = new ClusterFragmentBomb(getWorld(), getBodyRadius() / 2f, getDetonationRadius() / 2);
            clusterFragment.setTransform(position, 0f);
            clusterFragment.setLinearVelocity(velocity);
            bombs[i] = clusterFragment;
        }
        return bombs;
    }

    @Override
    public void beginContact(Fixture otherFixture) {
        super.beginContact(otherFixture);
        ticking = true;
    }

    @Override
    public void endContact(Fixture otherFixture) {
        super.endContact(otherFixture);
    }

    @Override
    public boolean isTicking() {
        return ticking;
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    public float getTickingProgress() {
        return 1f - tickingTimer / initialTickingTime;
    }

    @Override
    public boolean isFlashing() {
        float progress = getTickingProgress();
        return progress > 0.20f && progress <= 0.25f
                || progress > 0.6f && progress <= 0.65f
                || progress > 0.9f && progress <= 1f;
    }

    public static class KryoSerializer extends Serializer<ClusterBomb> {

        private final World world;

        public KryoSerializer(World world) {
            this.world = world;
        }

        @Override
        public void write(Kryo kryo, Output output, ClusterBomb object) {
            output.writeFloat(object.initialTickingTime);
            output.writeFloat(object.getBodyRadius());
            output.writeFloat(object.getDetonationRadius());
            output.writeFloat(object.tickingTimer);
            output.writeBoolean(object.ticking);
            kryo.writeObject(output, object.getBody().getPosition());
            kryo.writeObject(output, object.getBody().getAngle());
            kryo.writeObject(output, object.getBody().getLinearVelocity());
            kryo.writeObject(output, object.getBody().getAngularVelocity());
        }

        @Override
        public ClusterBomb read(Kryo kryo, Input input, Class<ClusterBomb> type) {
            ClusterBomb bomb = new ClusterBomb(
                    world,
                    input.readFloat(),
                    input.readFloat(),
                    input.readFloat()
            );
            bomb.tickingTimer = input.readFloat();
            bomb.ticking = input.readBoolean();
            bomb.getBody().setTransform(kryo.readObject(input, Vector2.class), input.readFloat());
            bomb.getBody().setLinearVelocity(kryo.readObject(input, Vector2.class));
            bomb.getBody().setAngularVelocity(input.readFloat());
            return bomb;
        }
    }
}
