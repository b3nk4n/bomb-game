package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.utils.PhysicsUtils;

public class Bomb implements Disposable {
    private final World world;

    private boolean ticking;
    private final float initialTickingTime;
    private float tickingTimer;
    private Body body;
    private final float bodyRadius;
    private final float detonationRadius;

    public Bomb(World world, float x, float y, float tickingTime, float bodyRadius, float detonationRadius) {
        this.world = world;
        this.initialTickingTime = tickingTime;
        this.tickingTimer = initialTickingTime;
        this.bodyRadius = bodyRadius;
        this.detonationRadius = detonationRadius;
        this.body = createBody(x, y, bodyRadius);
    }

    private Body createBody(float x, float y, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

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

    public void update(float delta) {
        if (isTicking()) {
            tickingTimer = Math.max(0f, tickingTimer - delta);
        }

        PhysicsUtils.applyAirResistance(body, 0.1f);
    }

    public void impact(Vector2 position, float radius) {
        PhysicsUtils.applyBlastImpact(body, position, radius);
    }

    public void startTicking() {
        ticking = true;
    }

    public boolean isTicking() {
        return ticking;
    }

    public float getTickingProgress() {
        return 1f - tickingTimer / initialTickingTime;
    }

    public boolean isFlashing() {
        float progress = getTickingProgress();
        return progress > 0.20f && progress <= 0.30f
                || progress > 0.5f && progress <= 0.60f
                || progress > 0.8f && progress <= 1f;
    }

    public boolean doExplode() {
        return tickingTimer <= 0;
    }

    @Override
    public void dispose() {
        world.destroyBody(body);
        body = null;
    }

    public float getBodyRadius() {
        return bodyRadius;
    }

    public float getDetonationRadius() {
        return detonationRadius;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getRotation() {
        return body.getAngle() * MathUtils.radiansToDegrees;
    }
}
