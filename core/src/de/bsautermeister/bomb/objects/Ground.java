package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.contact.Bits;

public class Ground {
    private final World world;

    private Array<Body> bodies;

    public Ground(World world, int numX, int numY) {
        this.world = world;
        this.bodies = new Array<>(4 * numX * numY);

        float width = 5f / Cfg.PPM;
        float height = 5f / Cfg.PPM;
        float offsetX = -numX * width / 2f;
        float offsetY = -3f;
        for (int y = 0; y < numY; ++y) {
            for (int x = 0; x < numX; ++x) {
                float posX = offsetX + x * width;
                float posY = offsetY + y * height;
                bodies.add(createBody(posX, posY, width, height));
            }
        }
    }

    private Body createBody(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

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

    public Array<Body> getBodies() {
        return bodies;
    }
}
