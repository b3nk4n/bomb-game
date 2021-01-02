package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class ClusterFragmentBomb extends Bomb {
    /**
     * Grant initial delay, because otherwise the the fragments are already touching the ground,
     * because the world is updated after the bombs are emitted.
     */
    private float delayToFirstContact = 0.1f;

    private boolean groundContact;

    public ClusterFragmentBomb(World world, float bodyRadius, float detonationRadius) {
        super(world, bodyRadius, 3, detonationRadius, 0.05f);
    }

    @Override
    public void update(float delta) {
        delayToFirstContact -= delta;
    }

    @Override
    public boolean doExplode() {
        return groundContact;
    }

    @Override
    public void beginContact(Fixture otherFixture) {
        super.beginContact(otherFixture);
        if (delayToFirstContact > 0) return;
        groundContact = true;
    }

    @Override
    public void endContact(Fixture otherFixture) {
        super.endContact(otherFixture);
    }

    @Override
    public boolean isFlashing() {
        return false;
    }

    @Override
    public boolean isTicking() {
        return false;
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    public static class KryoSerializer extends Serializer<ClusterFragmentBomb> {

        private final World world;

        public KryoSerializer(World world) {
            this.world = world;
        }

        @Override
        public void write(Kryo kryo, Output output, ClusterFragmentBomb object) {
            output.writeFloat(object.getBodyRadius());
            output.writeFloat(object.getDetonationRadius());
            output.writeBoolean(object.groundContact);
            kryo.writeObject(output, object.getBody().getPosition());
            kryo.writeObject(output, object.getBody().getAngle());
            kryo.writeObject(output, object.getBody().getLinearVelocity());
            kryo.writeObject(output, object.getBody().getAngularVelocity());
        }

        @Override
        public ClusterFragmentBomb read(Kryo kryo, Input input, Class<ClusterFragmentBomb> type) {
            ClusterFragmentBomb bomb = new ClusterFragmentBomb(
                    world,
                    input.readFloat(),
                    input.readFloat()
            );
            bomb.groundContact = input.readBoolean();
            bomb.getBody().setTransform(kryo.readObject(input, Vector2.class), input.readFloat());
            bomb.getBody().setLinearVelocity(kryo.readObject(input, Vector2.class));
            bomb.getBody().setAngularVelocity(input.readFloat());
            return bomb;
        }
    }
}
