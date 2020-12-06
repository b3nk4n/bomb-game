package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import de.bsautermeister.bomb.utils.PhysicsUtils;

public abstract class Bomb implements Disposable {
    private static final Bomb[] EMPTY_BOMB_RELEASE = new Bomb[0];

    private final World world;
    private final Body body;
    private final float bodyRadius;
    private final float detonationRadius;
    private final float blastImpactStrengthFactor;

    public Bomb(World world, float bodyRadius, float detonationRadius, float blastImpactStrengthFactor) {
        this.world = world;
        this.bodyRadius = bodyRadius;
        this.detonationRadius = detonationRadius;
        this.blastImpactStrengthFactor = blastImpactStrengthFactor;
        this.body = createBody();
    }

    protected abstract Body createBody();

    public void update(float delta) {
        PhysicsUtils.applyAirResistance(getBody(), 0.066f);
    }

    public void impact(Vector2 position, float radius) {
        PhysicsUtils.applyBlastImpact(getBody(), position, radius, blastImpactStrengthFactor);
    }

    public abstract void contact();

    public abstract boolean doExplode();

    public abstract boolean isFlashing();

    public Bomb[] releaseBombs() {
        return EMPTY_BOMB_RELEASE;
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
    }

    public World getWorld() {
        return world;
    }

    public Body getBody() {
        return body;
    }

    public float getBodyRadius() {
        return bodyRadius;
    }

    public float getDetonationRadius() {
        return detonationRadius;
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public float getRotation() {
        return getBody().getAngle() * MathUtils.radiansToDegrees;
    }
}
