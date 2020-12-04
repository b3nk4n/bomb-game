package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public final class PhysicsUtils {

    private PhysicsUtils() {}

    public static void applyAirResistance(Body body, float strength) {
        Vector2 velocity = body.getLinearVelocity();
        float speed = velocity.len();
        velocity.nor();
        body.applyForceToCenter(velocity.scl(-strength * speed * speed), true);
    }
}
