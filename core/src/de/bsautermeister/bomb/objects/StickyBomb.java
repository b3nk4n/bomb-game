package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJoint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;

import de.bsautermeister.bomb.contact.Bits;

public class StickyBomb extends Bomb {
    private boolean ticking;
    private final float initialTickingTime;
    private float tickingTimer;

    private JointDef stickyJointDef;
    private Joint stickyJoint;

    public StickyBomb(World world, float x, float y, float tickingTime, float bodyRadius, float detonationRadius) {
        super(world, bodyRadius, detonationRadius, 1f);
        this.initialTickingTime = tickingTime;
        this.tickingTimer = initialTickingTime;
        getBody().setTransform(x, y, 0f);
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

        if (isTicking()) {
            tickingTimer = Math.max(0f, tickingTimer - delta);
        }

        if (stickyJointDef != null) {
            getWorld().createJoint(stickyJointDef);
            stickyJointDef = null;
        }
    }

    public void stick(Body otherBody) {
        if (stickyJoint != null) return;

        WeldJointDef jointDef = new WeldJointDef();
        jointDef.initialize(getBody(), otherBody, otherBody.getPosition());
        stickyJointDef = jointDef;
    }

    @Override
    public boolean doExplode() {
        return tickingTimer <= 0;
    }

    public void beginContact(Fixture otherFixture) {
        ticking = true;
    }

    @Override
    public void endContact(Fixture otherFixture) {
        if (stickyJoint == null) return;

        Body otherBody = otherFixture.getBody();
        if (stickyJoint.getBodyA() == otherBody || stickyJoint.getBodyB() == otherBody) {
            // AFAIK Box2D internally takes care of destroying the joint. Here we just want to get
            // get rid of the dead reference.
            stickyJoint = null;
        }
    }

    public boolean isTicking() {
        return ticking;
    }

    public float getTickingProgress() {
        return 1f - tickingTimer / initialTickingTime;
    }

    @Override
    public boolean isFlashing() {
        float progress = getTickingProgress();
        return progress > 0.20f && progress <= 0.30f
                || progress > 0.5f && progress <= 0.60f
                || progress > 0.8f && progress <= 1f;
    }
}
