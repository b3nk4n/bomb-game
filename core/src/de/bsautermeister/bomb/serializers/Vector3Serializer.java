package de.bsautermeister.bomb.serializers;

import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Vector3Serializer extends Serializer<Vector3> {
    @Override
    public void write(Kryo kryo, Output output, Vector3 object) {
        output.writeFloat(object.x);
        output.writeFloat(object.y);
        output.writeFloat(object.z);
    }

    @Override
    public Vector3 read(Kryo kryo, Input input, Class<Vector3> type) {
        return new Vector3(input.readFloat(), input.readFloat(), input.readFloat());
    }

    @Override
    public Vector3 copy (Kryo kryo, Vector3 original) {
        return new Vector3(original);
    }
}
