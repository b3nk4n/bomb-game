package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.contact.Bits;

public class ClusterFragmentBomb extends Bomb {
    /**
     * Grant initial delay, because otherwise the the fragments are already touching the ground,
     * because the world is updated after the bombs are emitted.
     */
    private float delayToFirstContact = 0.1f;

    private boolean groundContact;

    public ClusterFragmentBomb(World world, Vector2 position, float bodyRadius, float detonationRadius, Vector2 velocity) {
        super(world, bodyRadius, detonationRadius, 0.05f);
        getBody().setTransform(position, 0f);
        getBody().setLinearVelocity(velocity);
    }

    @Override
    protected Body createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        Body body = getWorld().createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(getBodyRadius());

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.8f;
        fixtureDef.density = 10.0f;
        fixtureDef.restitution = 0.25f;
        fixtureDef.filter.categoryBits = Bits.BOMB;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.GROUND;
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        return body;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        delayToFirstContact -= delta;
    }

    @Override
    public boolean doExplode() {
        return groundContact;
    }

    @Override
    public void beginContact(Fixture otherFixture) {
        if (delayToFirstContact > 0) return;
        groundContact = true;
    }

    @Override
    public void endContact(Fixture otherFixture) {

    }

    @Override
    public boolean isFlashing() {
        return false;
    }

    public static class KryoSerializer extends Serializer<ClusterFragmentBomb> {

        private final World world;

        public KryoSerializer(World world) {
            this.world = world;
        }

        @Override
        public void write(Kryo kryo, Output output, ClusterFragmentBomb object) {
            kryo.writeObject(output, object.getBody().getPosition());
            kryo.writeObject(output, object.getBody().getLinearVelocity());
            output.writeFloat(object.getBodyRadius());
            output.writeFloat(object.getDetonationRadius());
            output.writeBoolean(object.groundContact);
            kryo.writeObject(output, object.getBody().getAngle());
            kryo.writeObject(output, object.getBody().getAngularVelocity());
        }

        @Override
        public ClusterFragmentBomb read(Kryo kryo, Input input, Class<? extends ClusterFragmentBomb> type) {
            Vector2 position = kryo.readObject(input, Vector2.class);
            Vector2 velocity = kryo.readObject(input, Vector2.class); // TODO move setting position / velocity out of all constructors?
            ClusterFragmentBomb bomb = new ClusterFragmentBomb(
                    world,
                    position,
                    input.readFloat(),
                    input.readFloat(),
                    velocity
            );
            bomb.groundContact = input.readBoolean();
            bomb.getBody().setTransform(position, input.readFloat());
            bomb.getBody().setAngularVelocity(input.readFloat());
            return bomb;
        }
    }
}
