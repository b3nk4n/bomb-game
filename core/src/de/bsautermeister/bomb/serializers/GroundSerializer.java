package de.bsautermeister.bomb.serializers;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.objects.Ground;

public class GroundSerializer extends Serializer<Ground> {

    private final World world;

    public GroundSerializer(World world) {
        this.world = world;
    }

    @Override
    public void write(Kryo kryo, Output output, Ground object) {
        output.writeInt(object.getNumCols());
        output.writeInt(object.getNumCompleteRows());
        output.writeFloat(object.getSize());
        kryo.writeObject(output, object.getFragments());
    }

    @Override
    public Ground read(Kryo kryo, Input input, Class<? extends Ground> type) {
        Ground ground = new Ground(
                world,
                input.readInt(),
                input.readInt(),
                input.readFloat());
        ground.setFragments(kryo.readObject(input, Array.class));
        return ground;
    }
}
