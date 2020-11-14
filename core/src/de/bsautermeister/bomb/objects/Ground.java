package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.bsautermeister.bomb.Cfg;

public class Ground {
    private Array<Fragment> fragments;

    public Ground(World world, int numX, int numY) {
        this.fragments = new Array<>(4 * numX * numY);

        float size = 5f / Cfg.PPM;
        float offsetX = -numX * size / 2f;
        float offsetY = -3f;
        for (int y = 0; y < numY; ++y) {
            for (int x = 0; x < numX; ++x) {
                float posX = offsetX + x * size;
                float posY = offsetY + y * size;
                fragments.add(new Fragment(world, posX, posY, size));
            }
        }
    }

    public void impact(Vector2 position, float radius) {
        for (Fragment fragment : fragments) {
            fragment.impact(position, radius);
            if (fragment.isEmpty()) {
                fragments.removeValue(fragment, true);
            }
        }
    }

    public Array<Fragment> getFragments() {
        return fragments;
    }
}
