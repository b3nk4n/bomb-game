package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WheelJointDef;
import com.badlogic.gdx.utils.Logger;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.utils.PhysicsUtils;

public class Player {
    private final static Logger LOG = new Logger(Player.class.getSimpleName(), Cfg.LOG_LEVEL);

    public static final float CRITICAL_HEALTH_THRESHOLD = 0.33f;

    private World world;
    private Body ballBody;
    private Body fixedSensorBody;

    private float radius;

    private float lifeRatio;
    private int score;

    private boolean blockJumpUntilRelease;
    private int groundContacts;

    public Player(World world, float radius) {
        this.world = world;
        this.radius = radius;
        this.ballBody = createBody(radius);
        reset();
    }

    public void reset() {
        lifeRatio = 1f;
        ballBody.setActive(true);
    }

    private Body createBody(float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 0.25f;
        bodyDef.angularDamping = 0.9f;

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.9f;
        fixtureDef.density = 10.0f;
        fixtureDef.filter.categoryBits = Bits.BALL;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.ENVIRONMENT;
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        BodyDef fixedSensorBodyDef = new BodyDef();
        fixedSensorBodyDef.fixedRotation = true;
        fixedSensorBodyDef.type = BodyDef.BodyType.DynamicBody;
        fixedSensorBody = world.createBody(fixedSensorBodyDef);

        FixtureDef groundSensorFixtureDef = new FixtureDef();
        EdgeShape groundSensorShape = new EdgeShape();
        groundSensorShape.set(-radius * 0.66f, -radius * 1.1f,
                radius * 0.66f, -radius * 1.1f);
        groundSensorFixtureDef.filter.categoryBits = Bits.BALL_SENSOR;
        groundSensorFixtureDef.filter.maskBits = Bits.GROUND;
        groundSensorFixtureDef.filter.groupIndex = 1;
        groundSensorFixtureDef.shape = groundSensorShape;
        groundSensorFixtureDef.isSensor = true;

        fixedSensorBody.createFixture(groundSensorFixtureDef).setUserData(this);
        groundSensorShape.dispose();

        JointDef jointDef = new WheelJointDef();
        jointDef.bodyA = fixedSensorBody;
        jointDef.bodyB = body;

        world.createJoint(jointDef);

        return body;
    }

    public void control(boolean up, boolean left, boolean right) {
        if (right) {
            if (hasGroundContact()) {
                ballBody.applyTorque(-3f, true);
            }
            ballBody.applyForceToCenter(12.5f, 0, true);
        }
        if (left) {
            if (hasGroundContact()) {
                ballBody.applyTorque(3f, true);
            }
            ballBody.applyForceToCenter(-12.5f, 0, true);
        }
        if (up && hasGroundContact() && !blockJumpUntilRelease) {
            blockJumpUntilRelease = true;
            ballBody.applyLinearImpulse(0f, 12.5f, ballBody.getWorldCenter().x, ballBody.getWorldCenter().y, true);
        }
        if (!up) {
            blockJumpUntilRelease = false;
        }
    }

    public void update(float delta) {
        if (!isDead()) {
            lifeRatio = Math.min(1f, lifeRatio + Cfg.Player.SELF_HEALING_PER_SECOND * delta);

            float lowestPositionY = -ballBody.getPosition().y - radius;
            score = Math.max(score, (int)(lowestPositionY * 10));

            //PhysicsUtils.applyAirResistance(ballBody, 0.5f);
        }
    }

    private static final Circle impactCircle = new Circle();
    private static final Circle playerCircle = new Circle();
    public boolean impact(Vector2 position, float radius) {
        float blastDistance = PhysicsUtils.applyBlastImpact(ballBody, position, radius, 1f);

        Vector2 bodyPosition = ballBody.getPosition();
        impactCircle.set(position, radius);
        playerCircle.set(bodyPosition, getRadius());
        boolean hasImpact = Intersector.overlaps(impactCircle, playerCircle);
        if (hasImpact) {
            float damage = -1f / radius * blastDistance + 1.5f;
            lifeRatio = Math.max(0f, lifeRatio - damage);
            LOG.debug("Applied damage: " + damage);

            if (isDead()) {
                ballBody.setActive(false);
            }
        }
        return hasImpact;
    }

    public void setTransform(Vector2 position, float angle) {
        ballBody.setTransform(position, angle);
        fixedSensorBody.setTransform(position, angle);
    }

    public void beginGroundContact() {
        groundContacts++;
    }

    public void endGroundContact() {
        groundContacts--;
    }

    public boolean hasGroundContact() {
        return groundContacts > 0;
    }

    public Vector2 getPosition() {
        return ballBody.getPosition();
    }

    public Vector2 getLinearVelocity () {
        return ballBody.getLinearVelocity();
    }

    public float getRotation() {
        return ballBody.getAngle();
    }

    public float getRadius() {
        return radius;
    }

    public float getLifeRatio() {
        return lifeRatio;
    }

    public float getCriticalHealthRatio() {
        if (lifeRatio > CRITICAL_HEALTH_THRESHOLD) {
            return 0f;
        } else {
            return MathUtils.clamp(1f - lifeRatio * 1 / CRITICAL_HEALTH_THRESHOLD, 0f, 1f);
        }
    }

    public boolean isDead() {
        return lifeRatio <= 0f;
    }

    public int getScore() {
        return score;
    }

    public static class KryoSerializer extends Serializer<Player> {

        private final World world;

        public KryoSerializer(World world) {
            this.world = world;
        }

        @Override
        public void write(Kryo kryo, Output output, Player object) {
            output.writeFloat(object.getRadius());
            kryo.writeObject(output, object.ballBody.getPosition());
            kryo.writeObject(output, object.ballBody.getAngle());
            kryo.writeObject(output, object.ballBody.getLinearVelocity());
            kryo.writeObject(output, object.ballBody.getAngularVelocity());
            kryo.writeObject(output, object.fixedSensorBody.getPosition());
            output.writeFloat(object.lifeRatio);
            output.writeInt(object.score);
            output.writeBoolean(object.blockJumpUntilRelease);
        }

        @Override
        public Player read(Kryo kryo, Input input, Class<Player> type) {
            Player player = new Player(world, input.readFloat());
            player.ballBody.setTransform(kryo.readObject(input, Vector2.class), input.readFloat());
            player.ballBody.setLinearVelocity(kryo.readObject(input, Vector2.class));
            player.ballBody.setAngularVelocity(input.readFloat());
            player.fixedSensorBody.setTransform(kryo.readObject(input, Vector2.class), 0f);
            player.lifeRatio = input.readFloat();
            player.score = input.readInt();
            player.blockJumpUntilRelease = input.readBoolean();
            return player;
        }
    }
}
