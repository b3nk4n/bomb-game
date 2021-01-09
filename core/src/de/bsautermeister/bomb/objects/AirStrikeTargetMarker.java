package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class AirStrikeTargetMarker {
    private final Vector2 position;
    private final float totalTime;
    private float ttl;

    public AirStrikeTargetMarker(Vector2 position, float totalTime) {
        this.position = new Vector2(position);
        this.totalTime = totalTime;
        this.ttl = totalTime;
    }

    public void update(float delta) {
        ttl -= delta;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getProgress() {
        return 1f - Math.max(0f, ttl / totalTime);
    }

    public boolean isReady() {
        return ttl <= 0f;
    }

    public static class KryoSerializer extends Serializer<AirStrikeTargetMarker> {
        @Override
        public void write(Kryo kryo, Output output, AirStrikeTargetMarker object) {
            kryo.writeObject(output, object.position);
            output.writeFloat(object.totalTime);
            output.writeFloat(object.ttl);
        }

        @Override
        public AirStrikeTargetMarker read(Kryo kryo, Input input, Class<AirStrikeTargetMarker> type) {
            AirStrikeTargetMarker targetMarker = new AirStrikeTargetMarker(
                    kryo.readObject(input, Vector2.class),
                    input.readFloat()
            );
            targetMarker.ttl = input.readFloat();
            return targetMarker;
        }
    }
}
