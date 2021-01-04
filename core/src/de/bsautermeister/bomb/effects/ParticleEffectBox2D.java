package de.bsautermeister.bomb.effects;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.physics.box2d.World;

import java.io.BufferedReader;
import java.io.IOException;

public class ParticleEffectBox2D extends ParticleEffect {

    private final World world;

    public ParticleEffectBox2D(World world) {
        super();
        this.world = world;
    }

    public ParticleEffectBox2D(ParticleEffectBox2D effect) {
        super(effect);
        this.world = effect.world;
    }

    @Override
    protected ParticleEmitter newEmitter(BufferedReader reader) throws IOException {
        return new DisappearParticleEmitterBox2D(world, reader);
    }

    @Override
    protected ParticleEmitter newEmitter(ParticleEmitter emitter) {
        DisappearParticleEmitterBox2D emitterBox2D = (DisappearParticleEmitterBox2D) emitter;
        return new DisappearParticleEmitterBox2D(emitterBox2D.getWorld(), emitter);
    }

    public float getXSizeScale() {
        return xSizeScale;
    }

    public float getYSizeScale() {
        return ySizeScale;
    }

    public float getMotionScale() {
        return motionScale;
    }

    public void setXSizeScale(float value) {
        this.xSizeScale = value;
    }

    public void setYSizeScale(float value) {
        this.ySizeScale = value;
    }

    public void setMotionScale(float value) {
        this.motionScale = value;
    }
}
