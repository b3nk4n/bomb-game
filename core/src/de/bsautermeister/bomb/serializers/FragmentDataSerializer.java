package de.bsautermeister.bomb.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.objects.FragmentData;

public class FragmentDataSerializer extends Serializer<FragmentData> {
    @Override
    public void write(Kryo kryo, Output output, FragmentData object) {
        output.writeFloat(object.getSize());
        kryo.writeObject(output, object.getGridData());
    }

    @Override
    public FragmentData read(Kryo kryo, Input input, Class<? extends FragmentData> type) {
        return new FragmentData(input.readFloat(), kryo.readObject(input, boolean[][].class));
    }
}
