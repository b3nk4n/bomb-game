package de.bsautermeister.bomb.serializers;

import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.objects.Fragment;
import de.bsautermeister.bomb.objects.FragmentData;

public class FragmentSerializer extends Serializer<Fragment> {

    private final World world;

    public FragmentSerializer(World world) {
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
        Fragment fragment = new Fragment(world, input.readFloat(), input.readFloat(), input.readFloat());
        fragment.setFragmentData(kryo.readObject(input, FragmentData.class));
        return fragment;
    }
}
