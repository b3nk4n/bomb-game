package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class Ground {

    private final World world;

    /**
     * All fragments in shape [row, cols].
     */
    private final Array<Array<Fragment>> fragments;

    private int lowestRowImpacted;

    private final float size;
    private final int numCols;
    private final int numCompleteRows;

    public Ground(World world, int numCols, int numCompleteRows, float size) {
        this.world = world;
        this.size = size;
        this.numCols = numCols;
        this.numCompleteRows = numCompleteRows;
        this.fragments = new Array<>(1024);
        for (int r = 0; r < numCompleteRows; ++r) {
            if (r == 2 || r == 3) continue;
            fragments.add(createRow(world, numCols, r, size));
        }
    }

    public Ground(World world, int numCols, int numCompleteRows, float size,
                  Array<Array<Fragment>> fragments) {
        this.world = world;
        this.size = size;
        this.numCols = numCols;
        this.numCompleteRows = numCompleteRows;
        this.fragments = fragments;
    }

    public int impact(float[] outRemovedVertices, Vector2 position, float radius) {
        int count = 0;
        for (int row = 0; row < fragments.size; ++row) {
            Array<Fragment> fragmentRow = fragments.get(row);
            for (int col = fragmentRow.size - 1; col >= 0; --col) {
                Fragment fragment = fragmentRow.get(col);
                int removed = fragment.impact(outRemovedVertices, 2 * count, position, radius);
                if (removed > 0) {
                    if (fragment.isEmpty()) {
                        fragmentRow.removeValue(fragment, true);
                    }
                    lowestRowImpacted = Math.max(lowestRowImpacted, row);
                    count += removed;
                }
            }
        }

        return count;
    }

    public void update() {
        updateRows();
    }

    private void updateRows() {
        int missingRows = numCompleteRows - (fragments.size - lowestRowImpacted) + 1;
        for (int i = 0; i < missingRows; ++i) {
            fragments.add(createRow(world, numCols, fragments.size, size));
        }
    }

    private static Array<Fragment> createRow(World world, int numCols, int rowIdx, float size) {
        Array<Fragment> row = new Array<>(numCols);
        for (int col = 0; col < numCols; ++col) {
            float posX = col * size;
            float posY = -(rowIdx + 1) * size;
            row.add(new Fragment(world, posX, posY, size));
        }
        return row;
    }

    public Array<Array<Fragment>> getFragments() {
        return fragments;
    }

    public static class KryoSerializer extends Serializer<Ground> {

        private final World world;

        public KryoSerializer(World world) {
            this.world = world;
        }

        @Override
        public void write(Kryo kryo, Output output, Ground object) {
            output.writeInt(object.numCols);
            output.writeInt(object.numCompleteRows);
            output.writeFloat(object.size);
            kryo.writeObject(output, object.getFragments());
        }

        @Override
        @SuppressWarnings("unchecked")
        public Ground read(Kryo kryo, Input input, Class<Ground> type) {
            return new Ground(
                    world,
                    input.readInt(),
                    input.readInt(),
                    input.readFloat(),
                    kryo.readObject(input, Array.class));
        }
    }
}
