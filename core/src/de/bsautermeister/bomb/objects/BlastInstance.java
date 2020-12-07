package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class BlastInstance {
    private final Vector2 position;
    private final float radius;
    private final float initialTtl;
    private float ttl;

    public BlastInstance(Vector2 position, float radius, float ttl) {
        this.position = new Vector2(position);
        this.radius = radius;
        this.initialTtl = ttl;
        this.ttl = ttl;
    }

    public void update(float delta) {
        ttl -= delta;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getProgress() {
        return MathUtils.clamp((initialTtl - ttl) / initialTtl, 0f, 1f);
    }

    public float getRadius() {
        return radius;
    }

    public boolean isExpired() {
        return ttl <= 0;
    }

    public static class KryoSerializer extends Serializer<BlastInstance> {
        @Override
        public void write(Kryo kryo, Output output, BlastInstance object) {
            kryo.writeObject(output, object.position);
            output.writeFloat(object.radius);
            output.writeFloat(object.initialTtl);
            output.writeFloat(object.ttl);
        }

        @Override
        public BlastInstance read(Kryo kryo, Input input, Class<? extends BlastInstance> type) {
            BlastInstance blastInstance = new BlastInstance(
                    kryo.readObject(input, Vector2.class),
                    input.readFloat(),
                    input.readFloat()
            );
            blastInstance.ttl = input.readFloat();
            return blastInstance;
        }
    }
}
