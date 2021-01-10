package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.serializers.KryoExternalSerializer;

public class AirStrikeManager implements KryoExternalSerializer {
    private final static Vector2 VELOCITY_RIGHT = new Vector2(3f, -9f);
    private final static Vector2 VELOCITY_LEFT = new Vector2(-3f, -9f);
    private final static float START_OFFSET_FACTOR = 2.5f;

    private final World world;
    private final Vector2 resultStart = new Vector2();
    private final Vector2 resultTarget = new Vector2();
    private boolean ready = false;

    private final static float REQUEST_TIME = 1f;
    private float requestTimer;
    private int requestIndex = -1;
    private final Vector2 requestVelocity = new Vector2();
    private final Vector2 requestedTarget = new Vector2();

    private final RayCastCallback rayCastCallback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getFilterData().categoryBits != Bits.GROUND) {
                return -1f;
            }
            if (isWithinWorldX(point)) {
                ready = true;
                resultTarget.set(point);
                float factor = START_OFFSET_FACTOR;
                resultStart
                        .set(point)
                        .sub(requestVelocity.x * factor * fraction, requestVelocity.y * factor * fraction);
                return fraction;
            }
            return -1f;
        }
    };

    public AirStrikeManager(World world) {
        this.world = world;
    }

    private final Vector2 tmpTarget = new Vector2();
    void update(float delta) {
        if (requestIndex == 0 && requestTimer <= REQUEST_TIME) {
            requestIndex++;
            tmpTarget
                    .set(requestedTarget)
                    .add(isWorldLeft(requestedTarget) ? 1f : -1f, 0f)
                    .add(requestVelocity);
            world.rayCast(rayCastCallback, getStart(tmpTarget, requestVelocity), tmpTarget);
        } else if (requestIndex == 1 && requestTimer <= REQUEST_TIME / 2) {
            requestIndex++;
            tmpTarget
                    .set(requestedTarget)
                    .add(requestVelocity);
            world.rayCast(rayCastCallback, getStart(tmpTarget, requestVelocity), tmpTarget);
        } else if (requestIndex == 2 && requestTimer <= 0f) {
            requestIndex = -1;
            tmpTarget
                    .set(requestedTarget)
                    .add(isWorldLeft(requestedTarget) ? -1f : 1f, 0f)
                    .add(requestVelocity);
            world.rayCast(rayCastCallback, getStart(tmpTarget, requestVelocity), tmpTarget);
        }

        requestTimer -= delta;
    }

    private boolean isWithinWorldX(Vector2 position) {
        return position.x > 0f && position.x < Cfg.World.WIDTH_PPM;
    }

    private boolean isWorldLeft(Vector2 position) {
        return position.x < Cfg.World.WIDTH_PPM / 2f;
    }

    public void request(Vector2 target) {
        requestedTarget.set(target);
        requestVelocity.set(isWorldLeft(target) ? VELOCITY_LEFT : VELOCITY_RIGHT);
        requestTimer = REQUEST_TIME;
        requestIndex = 0;
    }

    private final EmitInfo outEmitInfo = new EmitInfo();
    public EmitInfo getTargetAndReset() {
        ready = false;
        outEmitInfo.start.set(resultStart);
        outEmitInfo.target.set(resultTarget);
        outEmitInfo.velocity.set(requestVelocity);
        return outEmitInfo;
    }

    public boolean isReady() {
        return ready;
    }

    private final Vector2 tmpStart = new Vector2();
    private Vector2 getStart(Vector2 target, Vector2 velocity) {
        tmpStart.set(target);
        tmpStart.sub(velocity.x * START_OFFSET_FACTOR,
                velocity.y * START_OFFSET_FACTOR);
        return  tmpStart;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeBoolean(ready);
        output.writeFloat(requestTimer);
        output.writeInt(requestIndex);
        kryo.writeObject(output, requestedTarget);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        ready = input.readBoolean();
        requestTimer = input.readFloat();
        requestIndex = input.readInt();
        requestedTarget.set(kryo.readObject(input, Vector2.class));
    }

    public class EmitInfo {
        private final Vector2 target = new Vector2();
        private final Vector2 start = new Vector2();
        private final Vector2 velocity = new Vector2();

        public Vector2 getTarget() {
            return target;
        }

        public Vector2 getStart() {
            return start;
        }

        public Vector2 getVelocity() {
            return velocity;
        }

        public float getAngle() {
            return velocity.angleRad();
        }
    }
}
