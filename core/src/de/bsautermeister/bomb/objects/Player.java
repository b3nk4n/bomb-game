package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.Bits;

public class Player {
    private final World world;
    private Body body;

    private final Vector2 startPosition;
    private final float radius;

    private float lifeRatio;
    private int score;

    private static final float BLOCK_JUMP_TIME = 1f;
    private float blockJumpTimer;
    private int groundContacts;

    public Player(World world, Vector2 startPosition, float radius) {
        this.world = world;
        this.startPosition = startPosition;
        this.radius = radius;
        this.body = createBody(radius);
        reset();
    }

    public void reset() {
        lifeRatio = 1f;
        body.setTransform(startPosition, 0);
        body.setActive(true);
    }

    private Body createBody(float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.8f;
        fixtureDef.density = 10.0f;
        fixtureDef.filter.categoryBits = Bits.BALL;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.GROUND;
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        return body;
    }

    public void control(boolean up, boolean left, boolean right) {
        if (right) {
            body.applyForceToCenter(25f, 0, true);
        }
        if (left) {
            body.applyForceToCenter(-25f, 0, true);
        }
        if (up && hasGroundContact() && blockJumpTimer < 0) {
            blockJumpTimer = BLOCK_JUMP_TIME;
            body.applyLinearImpulse(0f, 8f, body.getWorldCenter().x, body.getWorldCenter().y, true);
        }
    }

    public void update(float delta) {
        if (!isDead()) {
            lifeRatio = Math.min(1f, lifeRatio + Cfg.PLAYER_SELF_HEALING_PER_SECOND * delta);

            blockJumpTimer -= delta;

            float lowestPositionY = -body.getPosition().y - radius;
            score = Math.max(score, (int)(lowestPositionY * 10));
        }
    }

    private static final Vector2 blastImpactDirection = new Vector2();
    private static final Circle impactCircle = new Circle();
    private static final Circle playerCircle = new Circle();
    public boolean impact(Vector2 position, float radius) {
        Vector2 bodyPosition = body.getPosition();
        impactCircle.set(position, radius);
        playerCircle.set(bodyPosition, getRadius());
        if (Intersector.overlaps(impactCircle, playerCircle)) {
            lifeRatio = Math.max(0f, lifeRatio - .2f); // TODO reduce life dependent on distance
        }

        // blast impact
        blastImpactDirection.set(bodyPosition).sub(position).scl(1f);
        float blastDistance = blastImpactDirection.len();
        final float maxBlast = 3 * radius;
        if (blastDistance < maxBlast) {
            blastImpactDirection.nor().scl((maxBlast - blastDistance));
            body.applyLinearImpulse(blastImpactDirection, bodyPosition, true);
        }

        if (isDead()) {
            body.setActive(false);
            return true;
        }

        return false;
    }

    public void beginGroundContact() {
        groundContacts++;
    }

    public void endGroundContact() {
        groundContacts--;
    }

    public boolean hasGroundContact() {
        return groundContacts > 0;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getRotation() {
        return body.getAngle() * MathUtils.radiansToDegrees;
    }

    public float getRadius() {
        return radius;
    }

    public float getLifeRatio() {
        return lifeRatio;
    }

    public boolean isDead() {
        return lifeRatio <= 0f;
    }

    public int getScore() {
        return score;
    }
}
