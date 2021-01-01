package de.bsautermeister.bomb.factories;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.BounceStickyBomb;
import de.bsautermeister.bomb.objects.ClusterBomb;
import de.bsautermeister.bomb.objects.StickyBomb;
import de.bsautermeister.bomb.objects.TimedBomb;

public class BombFactoryImpl implements BombFactory {

    private final World world;

    public BombFactoryImpl(World world) {
        this.world = world;
    }

    @Override
    public Bomb createRandomBomb() {
        float rnd = MathUtils.random();
        if (rnd < 0.20f) {
            return createTimedBomb();
        }
        if (rnd < 0.40f) {
            return createStickyBomb();
        }
        if (rnd < 0.6f) {
            return createClusterBomb();
        }
        if (rnd < 0.8f) {
            return createStickyBomb();
        }
        return createBounceStickyBomb();
    }

    @Override
    public Bomb createTimedBomb() {
        float bodyRadius = MathUtils.random(0.5f, 1.0f);
        float detonationRadius = bodyRadius * 15;
        float tickingTime = MathUtils.random(2f, 5f);

        return new TimedBomb(world, tickingTime, bodyRadius / Cfg.World.PPM, detonationRadius / Cfg.World.PPM);
    }

    @Override
    public Bomb createClusterBomb() {
        float bodyRadius = MathUtils.random(0.5f, 1.0f);
        float detonationRadius = bodyRadius * 15;
        float tickingTime = MathUtils.random(2f, 5f);

        return new ClusterBomb(world, tickingTime, bodyRadius / Cfg.World.PPM, detonationRadius / Cfg.World.PPM);
    }

    @Override
    public Bomb createStickyBomb() {
        float bodyRadius = MathUtils.random(0.5f, 1.0f);
        float detonationRadius = bodyRadius * 15;
        float tickingTime = MathUtils.random(2f, 5f);

        return new StickyBomb(world, tickingTime, bodyRadius / Cfg.World.PPM, detonationRadius / Cfg.World.PPM);
    }

    @Override
    public Bomb createBounceStickyBomb() {
        float bodyRadius = MathUtils.random(0.5f, 1.0f);
        float detonationRadius = bodyRadius * 15;
        float tickingTime = MathUtils.random(2f, 5f);

        return new BounceStickyBomb(world, tickingTime, bodyRadius / Cfg.World.PPM, detonationRadius / Cfg.World.PPM);
    }
}
