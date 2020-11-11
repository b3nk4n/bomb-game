package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import de.bsautermeister.bomb.contact.Bits;

public class Player {
    private final World world;
    private Body body;

    private float radius;

    public Player(World world, Vector2 startPosition, float radius) {
        this.world = world;
        this.radius = radius;
        this.body = createBody(startPosition, radius);
    }

    private Body createBody(Vector2 position, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = false;

        Body body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
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
        if (up) {
            body.applyLinearImpulse(0f, 1f, body.getWorldCenter().x, body.getWorldCenter().y, true);
        }
    }

    public void update(float delta) {

    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public float getRadius() {
        return radius;
    }
}
