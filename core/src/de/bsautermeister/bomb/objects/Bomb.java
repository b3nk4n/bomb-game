package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import de.bsautermeister.bomb.contact.Bits;

public class Bomb implements Disposable {
    private final World world;

    private boolean groundTouched;
    private float ttl;
    private Body body;
    private final float bodyRadius;
    private final float detonationRadius;

    public Bomb(World world, float x, float y, float ttl, float bodyRadius, float detonationRadius) {
        this.world = world;
        this.ttl = ttl;
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
        if (groundTouched) {
            ttl -= delta;
        }
    }

    public void touchGround() {
        groundTouched = true;
    }

    public boolean doExplode() {
        return ttl < 0;
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
        return body.getAngle();
    }
}
