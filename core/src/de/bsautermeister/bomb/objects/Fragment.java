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
import de.bsautermeister.bomb.utils.ArrayUtils;

public class Fragment {
    private static final int RESOLUTION = 10;

    private final float x;
    private final float y;
    private final float size;

    private final World world;
    private final Array<Body> bodies;

    private final boolean[][] nodeData = new boolean[RESOLUTION][RESOLUTION];

    public Fragment(World world, float x, float y, float size) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.size = size;

        this.bodies = new Array<>(4);
        this.bodies.add(createBody(x, y, size));
        ArrayUtils.fill2D(nodeData, true);
    }

    private Body createBody(float x, float y, float size) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
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
        boolean updated = updateNodeData(circle);
        if (updated) {
            // TODO find clusters: using simple fill algorithm?
            // TODO cleanup: remove nodes that have only 0 or 1 neighbor
            // TODO update bodies: destroy and create new bodies for each cluster: clockwise polygon path?
        }
    }

    private boolean updateNodeData(Circle circle) {
        float delta = (RESOLUTION - 1) / this.size;
        boolean updated = false;
        for (int i = 0; i < nodeData.length; ++i) {
            for (int j = 0; j < nodeData[i].length; ++j) {
                float x = this.x + i * delta;
                float y = this.y + j * delta;
                if (circle.contains(x, y)) {
                    nodeData[i][j] = false;
                    updated = true;
                }
            }
        }
        return updated;
    }

    public Array<Body> getBodies() {
        return bodies;
    }
}
