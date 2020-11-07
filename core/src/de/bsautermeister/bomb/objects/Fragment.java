package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.contact.Bits;

public class Fragment {
    private static final int RESOLUTION = 10;

    private final World world;
    private final Array<Body> bodies;

    private final FragmentData fragmentData;

    public Fragment(World world, float x, float y, float size) {
        this.world = world;
        this.fragmentData = new FragmentData(RESOLUTION, x, y, size);

        this.bodies = new Array<>(4);
        this.bodies.add(createBody(x, y, size));
    }

    private Body createBody(float x, float y, float size) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        //shape.set();
        shape.setAsBox(size / 2f, size / 2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Bits.GROUND;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.BALL;
        fixtureDef.shape = shape;
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);
        shape.dispose();

        return body;
    }

    public void impact(Circle circle) {
        boolean updated = fragmentData.remove(circle);
        if (updated) {
            // TODO find clusters: using simple fill algorithm?
            // TODO cleanup: remove nodes that have only 0 or 1 neighbor
            // TODO update bodies: destroy and create new bodies for each cluster: clockwise polygon path?
        }
    }

    public Array<Body> getBodies() {
        return bodies;
    }
}
