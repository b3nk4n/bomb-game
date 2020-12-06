package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import de.bsautermeister.bomb.contact.Bits;

public class ClusterFragmentBomb extends Bomb {
    /**
     * Grant initial delay, because otherwise the the fragments are already touching the ground,
     * because the world is updated after the bombs are emitted.
     */
    private float delayToFirstContact = 0.1f;

    private boolean groundContact;

    public ClusterFragmentBomb(World world, float x, float y, float bodyRadius, float detonationRadius, Vector2 velocity) {
        super(world, bodyRadius, detonationRadius, 0.05f);
        getBody().setTransform(x, y, 0f);
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
}
