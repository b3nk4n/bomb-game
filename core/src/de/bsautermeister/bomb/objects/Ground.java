package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

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
        this.fragments = new Array<>(1024);
        this.size = size;
        this.numCols = numCols;
        this.numCompleteRows = numCompleteRows;

        for (int r = 0; r < numCompleteRows; ++r) {
            addRow();
        }
    }

    public void impact(Vector2 position, float radius) {
        for (int row = 0; row < fragments.size; ++row) {
            Array<Fragment> fragmentRow = fragments.get(row);
            for (int col = fragmentRow.size - 1; col >= 0; --col) {
                Fragment fragment = fragmentRow.get(col);
                if (fragment.impact(position, radius)) {
                    if (fragment.isEmpty()) {
                        fragmentRow.removeValue(fragment, true);
                    }
                    lowestRowImpacted = Math.max(lowestRowImpacted, row);
                }
            }
        }
    }

    public void update(float delta) {
        updateRows();
    }

    private void updateRows() {
        int missingRows = numCompleteRows - (fragments.size - lowestRowImpacted) + 1;
        for (int i = 0; i < missingRows; ++i) {
            addRow();
        }
    }

    private void addRow() {
        Array<Fragment> row = new Array<>(numCols);
        for (int col = 0; col < numCols; ++col) {
            float posX = col * size;
            float posY = -(fragments.size + 1) * size;
            row.add(new Fragment(world, posX, posY, size));
        }
        fragments.add(row);
    }

    public Array<Array<Fragment>> getFragments() {
        return fragments;
    }
}
