package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.contact.Bits;

public class Fragment {

    private static final float EPSILON = 1e-5f;
    private static final int RESOLUTION = 8;
    private static final EarClippingTriangulator TRIANGULATOR = new EarClippingTriangulator();

    private final World world;
    private final Rectangle bounds;
    private final FragmentData fragmentData;

    private Body body;

    public Fragment(World world, float leftX, float bottomY, float size) {
        this(world, leftX, bottomY, size, new FragmentData(RESOLUTION, size));
    }

    public Fragment(World world, float leftX, float bottomY, float size, FragmentData fragmentData) {
        this.world = world;
        this.bounds = new Rectangle(leftX, bottomY, size, size);
        this.fragmentData = fragmentData;
        updateBody();
    }

    private static final Circle tmpImpactCircle = new Circle();
    public boolean impact(Vector2 position, float radius) {
        tmpImpactCircle.set(position.x, position.y, radius);
        if (!Intersector.overlaps(tmpImpactCircle, bounds)) {
            // early stop: don't check each single fragment grid position when the impact was
            //             outside of the fragments bounds
            return false;
        }

        float leftX = getLeftX();
        float bottomY = getBottomY();
        // change to relative position used in the fragment data
        tmpImpactCircle.set(position.x - leftX, position.y - bottomY, radius);

        boolean updated = fragmentData.remove(tmpImpactCircle);
        if (updated) {
            world.destroyBody(body);
            updateBody();
        }
        return updated;
    }

    private void updateBody() {
        Array<float[]> polygonOutlines = fragmentData.computeOutlines();
        if (polygonOutlines.notEmpty()) {
            body = createBody(getLeftX(), getBottomY(), polygonOutlines);
        } else  {
            // mark empty
            body = null;
        }
    }

    private Body createBody(float leftX, float bottomY, Array<float[]> polygonOutlines) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(leftX, bottomY);
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.9f;
        fixtureDef.filter.categoryBits = Bits.GROUND;
        fixtureDef.filter.groupIndex = 1;
        fixtureDef.filter.maskBits = Bits.BALL | Bits.BALL_SENSOR;
        fixtureDef.shape = shape;
        for (float[] polygonOutline : polygonOutlines) {
            ShortArray triangles = TRIANGULATOR.computeTriangles(polygonOutline);
            for (int i = 0; i < triangles.size; i += 3) {
                int p1 = triangles.get(i) * 2;
                int p2 = triangles.get(i + 1) * 2;
                int p3 = triangles.get(i + 2) * 2;

                float a1 = polygonOutline[p1];
                float a2 = polygonOutline[p1 + 1];
                float b1 = polygonOutline[p2];
                float b2 = polygonOutline[p2 + 1];
                float c1 = polygonOutline[p3];
                float c2 = polygonOutline[p3 + 1];

                boolean hasRedundantVertices = a1 == b1 && a2 == b2
                        || a1 == c1 && a2 == c2
                        || b1 == c1 && b2 == c2;

                float pointLineDistance = Intersector.distanceLinePoint(a1, a2, b1, b2, c1, c2);

                if (pointLineDistance < EPSILON || hasRedundantVertices) {
                    // skip because triangulator returns degenerate polygon (at least two vertices on same position),
                    // which Box2D cannot handle, and that would not be visible anyways
                    continue;
                }
                shape.set(new float[]{ a1, a2, b1, b2, c1, c2 });
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

    public float getLeftX() {
        return bounds.x;
    }

    public float getBottomY() {
        return bounds.y;
    }

    public float getSize() {
        return bounds.width;
    }

    public boolean isEmpty() {
        return body == null;
    }

    public FragmentData getFragmentData() {
        return fragmentData;
    }

    public static class KryoSerializer extends Serializer<Fragment> {

        private final World world;

        public KryoSerializer(World world) {
            this.world = world;
        }

        @Override
        public void write(Kryo kryo, Output output, Fragment object) {
            output.writeFloat(object.getLeftX());
            output.writeFloat(object.getBottomY());
            output.writeFloat(object.getSize());
            kryo.writeObject(output, object.getFragmentData());
        }

        @Override
        public Fragment read(Kryo kryo, Input input, Class<? extends Fragment> type) {
            return new Fragment(
                    world,
                    input.readFloat(),
                    input.readFloat(),
                    input.readFloat(),
                    kryo.readObject(input, FragmentData.class));
        }
    }
}
