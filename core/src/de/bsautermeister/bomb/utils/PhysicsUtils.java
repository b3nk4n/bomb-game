package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public final class PhysicsUtils {

    private PhysicsUtils() {}

    private static final Vector2 blastImpactDirection = new Vector2();
    public static float applyBlastImpact(Body body, Vector2 blastCenter, float blastRadius, float strengthFactor) {
        Vector2 bodyPosition = body.getPosition();

        // blast impact
        blastImpactDirection.set(bodyPosition).sub(blastCenter);
        float blastDistance = blastImpactDirection.len();
        final float maxBlast = 3 * blastRadius;
        if (blastDistance < maxBlast) {
            blastImpactDirection.nor().scl(strengthFactor * (maxBlast - blastDistance));
            body.applyLinearImpulse(blastImpactDirection, bodyPosition, true);
        }
        return blastDistance;
    }
}
