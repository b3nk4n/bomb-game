package de.bsautermeister.bomb.serializers;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Serializer based on <a href="https://github.com/libgdx/libgdx/wiki/Saved-game-serialization">LibGDX - Saved game serialization</a>.
 */
public class ArraySerializer extends Serializer<Array> {

    private Class genericType;

    public ArraySerializer() {
        setAcceptsNull(true);
    }

    public void setGenerics (Kryo kryo, Class[] generics) {
        if (generics != null && kryo.isFinal(generics[0])) genericType = generics[0];
        else genericType = null;
    }

    @Override
    public void write (Kryo kryo, Output output, Array array) {
        int length = array.size;
        output.writeInt(length, true);
        if (length == 0) {
            genericType = null;
            return;
        }
        if (genericType != null) {
            Serializer serializer = kryo.getSerializer(genericType);
            genericType = null;
            for (Object element : array)
                kryo.writeObjectOrNull(output, element, serializer);
        } else {
            for (Object element : array)
                kryo.writeClassAndObject(output, element);
        }
    }

    @Override
    public Array read(Kryo kryo, Input input, Class<Array> type) {
        Array array = new Array();
        kryo.reference(array);
        int length = input.readInt(true);
        array.ensureCapacity(length);
        if (genericType != null) {
            Class elementClass = genericType;
            Serializer serializer = kryo.getSerializer(genericType);
            genericType = null;
            for (int i = 0; i < length; i++)
                array.add(kryo.readObjectOrNull(input, elementClass, serializer));
        } else {
            for (int i = 0; i < length; i++)
                array.add(kryo.readClassAndObject(input));
        }
        return array;
    }
}
