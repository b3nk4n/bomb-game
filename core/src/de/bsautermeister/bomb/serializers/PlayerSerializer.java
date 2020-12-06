package de.bsautermeister.bomb.serializers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.objects.Player;

public class PlayerSerializer extends Serializer<Player> {

    private final World world;

    public PlayerSerializer(World world) {
        this.world = world;
    }

    @Override
    public void write(Kryo kryo, Output output, Player object) {
        kryo.writeObject(output, object.getPosition());
        output.writeFloat(object.getRadius());
    }

    @Override
    public Player read(Kryo kryo, Input input, Class<? extends Player> type) {
        Vector2 position = kryo.readObject(input, Vector2.class);
        float radius = input.readFloat();
        return new Player(world, position, radius);
    }
}
