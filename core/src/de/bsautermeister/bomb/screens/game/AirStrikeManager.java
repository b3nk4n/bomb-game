package de.bsautermeister.bomb.screens.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.Bits;

public class AirStrikeManager {
    private final World world;
    private final Vector2 result = new Vector2();
    private boolean ready = false;

    private final static float REQUEST_TIME = 1f;
    private float requestTimer;
    private float requestIndex;
    private final Vector2 requestedTarget = new Vector2();

    private final RayCastCallback rayCastCallback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getFilterData().categoryBits != Bits.GROUND) {
                return -1f;
            }

            ready = true;
            result.set(point);
            return fraction;
        }
    };

    public AirStrikeManager(World world) {
        this.world = world;
    }

    void update(float delta) {
        if (requestIndex == 0 && requestTimer <= REQUEST_TIME) {
            requestIndex++;
            requestedTarget.sub(1f, 0f);
            world.rayCast(rayCastCallback, getStart(requestedTarget), requestedTarget);
        } else if (requestIndex == 1 && requestTimer <= REQUEST_TIME / 2) {
            requestIndex++;
            requestedTarget.add(1f, 0f);
            world.rayCast(rayCastCallback, getStart(requestedTarget), requestedTarget);
        } else if (requestIndex == 2 && requestTimer <= 0f) {
            requestIndex++;
            requestedTarget.add(1f, 0f);
            world.rayCast(rayCastCallback, getStart(requestedTarget), requestedTarget);
        }

        requestTimer -= delta;
    }

    public void request(Vector2 target) {
        requestedTarget.set(target).add(Cfg.AirStrike.VELOCITY);
        requestTimer = REQUEST_TIME;
        requestIndex = 0;
    }

    public Vector2 getTargetAndReset() {
        ready = false;
        return result;
    }

    public boolean isReady() {
        return ready;
    }

    private final Vector2 tmpStart = new Vector2();
    private Vector2 getStart(Vector2 target) {
        tmpStart.set(target);
        tmpStart.sub(Cfg.AirStrike.VELOCITY.x * Cfg.AirStrike.START_OFFSET_FACTOR,
                Cfg.AirStrike.VELOCITY.y * Cfg.AirStrike.START_OFFSET_FACTOR);
        return  tmpStart;
    }
}
