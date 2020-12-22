package de.bsautermeister.bomb.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public interface KryoExternalSerializer {
    void write(Kryo kryo, Output output);
    void read(Kryo kryo, Input input);
}
