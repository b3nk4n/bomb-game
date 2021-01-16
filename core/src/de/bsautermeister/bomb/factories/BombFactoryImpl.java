package de.bsautermeister.bomb.factories;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.objects.AirStrikeBomb;
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
        if (rnd < 0.4f) {
            return createTimedBomb();
        }
        if (rnd < 0.55f) {
            return createStickyBomb();
        }
        if (rnd < 0.75f) {
            return createBounceStickyBomb();
        }
        return createClusterBomb();
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

    @Override
    public Bomb createAirStrikeBomb() {
        float bodyRadius = 0.4f  / Cfg.World.PPM;
        float detonationRadius = bodyRadius * 10;
        return new AirStrikeBomb(world, bodyRadius, detonationRadius);
    }
}
