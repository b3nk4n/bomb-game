package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import de.bsautermeister.bomb.contact.Bits;
import de.bsautermeister.bomb.utils.PhysicsUtils;
import de.bsautermeister.bomb.utils.PolygonUtils;

public abstract class Bomb implements Disposable {
    private static final Bomb[] EMPTY_BOMB_RELEASE = new Bomb[0];

    private final World world;
    private final Body body;
    private final float bodyRadius;
    private final int bodySegments;
    private final float detonationRadius;
    private final float blastImpactStrengthFactor;

    private int contactCounter;

    public Bomb(World world, float bodyRadius, int bodySegments, float detonationRadius, float blastImpactStrengthFactor) {
        this.world = world;
        this.bodyRadius = bodyRadius;
        this.bodySegments = bodySegments;
        this.detonationRadius = detonationRadius;
        this.blastImpactStrengthFactor = blastImpactStrengthFactor;
        this.body = createBody();
    }

    private Body createBody() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearDamping = 0.66f;
        bodyDef.angularDamping = 0.9f;
        defineBody(bodyDef);

        Body body = getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.set(PolygonUtils.createPolygon(getBodyRadius(), getBodySegments()));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.8f;
        fixtureDef.density = 10.0f;
        fixtureDef.restitution = 0.55f;
        defineFilter(fixtureDef.filter);
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        return body;
    }

    /**
     * Allow a subclass to modify the body definition
     */
    protected void defineBody(BodyDef bodyDef) {
        // noop
    }

    protected void defineFilter(Filter filter) {
        filter.categoryBits = Bits.BOMB;
        filter.maskBits = Bits.ENVIRONMENT | Bits.OBJECTS;
    }

    public abstract void update(float delta);

    public void impact(Vector2 position, float radius) {
        if (!impactedByExplosions()) return;

        PhysicsUtils.applyBlastImpact(getBody(), position, radius, blastImpactStrengthFactor);
    }

    public void setTransform(Vector2 position, float angle) {
        body.setTransform(position, angle);
    }

    public void setLinearVelocity(Vector2 velocity) {
        body.setLinearVelocity(velocity);
    }

    public void beginContact(Fixture otherFixture) {
        contactCounter++;
    }

    public void endContact(Fixture otherFixture) {
        contactCounter--;
    }

    public boolean hasContact() {
        return contactCounter > 0;
    }

    public abstract boolean doExplode();

    public boolean impactedByExplosions() {
        return true;
    }

    public abstract boolean isFlashing();

    public abstract boolean isTicking();

    public abstract boolean isSticky();

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

    protected Body getBody() {
        return body;
    }

    public float getBodyRadius() {
        return bodyRadius;
    }

    public int getBodySegments() {
        return bodySegments;
    }

    public float getDetonationRadius() {
        return detonationRadius;
    }

    public Vector2 getPosition() {
        return getBody().getPosition();
    }

    public Vector2 getLinearVelocity() {
        return getBody().getLinearVelocity();
    }

    public float getRotation() {
        return getBody().getAngle();
    }
}
