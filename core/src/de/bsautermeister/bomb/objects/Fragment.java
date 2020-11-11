package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import java.util.Arrays;

import de.bsautermeister.bomb.contact.Bits;

public class Fragment {
    private static final int RESOLUTION = 8;

    private final World world;
    private Body body;

    private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();

    private final FragmentData fragmentData;

    public Fragment(World world, float x, float y, float size) {
        this.world = world;
        this.fragmentData = new FragmentData(RESOLUTION, x - size / 2f, y - size / 2f, size);

        Array<float[]> polygonOutlines = fragmentData.computeOutlines();
        this.body = createBody(x, y, polygonOutlines);
    }

    public void impact(Circle circle) {
        boolean updated = fragmentData.remove(circle);
        if (updated) {
            float x = body.getPosition().x;
            float y = body.getPosition().y;
            world.destroyBody(body);
            body = null;
            Array<float[]> polygonOutlines = fragmentData.computeOutlines();
            System.out.println(polygonOutlines);
            if (polygonOutlines.notEmpty()) {
                this.body = createBody(x, y, polygonOutlines);
            }
        }
    }

    private Body createBody(float x, float y, Array<float[]> polygonOutlines) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.filter.categoryBits = Bits.GROUND;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.BALL;
        fixtureDef.shape = shape;
        for (float[] polygonOutline : polygonOutlines) {
            ShortArray triangles = TRIANGULATOR.computeTriangles(polygonOutline);
            System.out.println(triangles.size);
            for (int i = 0; i < triangles.size; i += 3) {
                int p1 = triangles.get(i) * 2;
                int p2 = triangles.get(i + 1) * 2;
                int p3 = triangles.get(i + 2) * 2;
                if (polygonOutline[p1] == polygonOutline[p2] && polygonOutline[p2] == polygonOutline[p3]
                        || polygonOutline[p1 + 1] == polygonOutline[p2 + 1] && polygonOutline[p2 + 1] == polygonOutline[p3 + 1]) {
                    System.out.println(Arrays.toString(new float[]{
                            polygonOutline[p1], polygonOutline[p1 + 1],
                            polygonOutline[p2], polygonOutline[p2 + 1],
                            polygonOutline[p3], polygonOutline[p3 + 1]
                    }));
                    System.out.println("skip");
                    // TODO why does the triangulator inlude invalid triangles that are in a straight line?
                    continue;
                }
                shape.set(new float[]{
                        polygonOutline[p1], polygonOutline[p1 + 1],
                        polygonOutline[p2], polygonOutline[p2 + 1],
                        polygonOutline[p3], polygonOutline[p3 + 1]
                });
                Fixture fixture = body.createFixture(fixtureDef);
                fixture.setUserData(this);
            }
        }
        shape.dispose();
        return body;
    }

    public Body getBody() {
        return body;
    }
}
