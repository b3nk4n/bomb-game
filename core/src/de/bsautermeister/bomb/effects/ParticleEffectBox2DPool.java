package de.bsautermeister.bomb.effects;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class ParticleEffectBox2DPool extends Pool<ParticleEffectBox2DPool.PooledBox2DEffect> {
    private final ParticleEffectBox2D effect;

    public ParticleEffectBox2DPool (ParticleEffectBox2D effect, int initialCapacity, int max) {
        super(initialCapacity, max);
        this.effect = effect;
    }

    protected PooledBox2DEffect newObject () {
        PooledBox2DEffect pooledEffect = new PooledBox2DEffect(effect);
        pooledEffect.start();
        return pooledEffect;
    }

    public void free (PooledBox2DEffect effect) {
        super.free(effect);

        effect.reset(false); // copy parameters exactly to avoid introducing error
        if (effect.getXSizeScale() != this.effect.getXSizeScale() || effect.getYSizeScale() != this.effect.getYSizeScale() || effect.getMotionScale() != this.effect.getMotionScale()){
            Array<ParticleEmitter> emitters = effect.getEmitters();
            Array<ParticleEmitter> templateEmitters = this.effect.getEmitters();
            for (int i=0; i<emitters.size; i++){
                ParticleEmitter emitter = emitters.get(i);
                ParticleEmitter templateEmitter = templateEmitters.get(i);
                emitter.matchSize(templateEmitter);
                emitter.matchMotion(templateEmitter);
            }
            effect.setXSizeScale(this.effect.getXSizeScale());
            effect.setYSizeScale(this.effect.getYSizeScale());
            effect.setMotionScale(this.effect.getMotionScale());
        }
    }

    public class PooledBox2DEffect extends ParticleEffectBox2D {
        PooledBox2DEffect (ParticleEffectBox2D effect) {
            super(effect);
        }

        public void free () {
            ParticleEffectBox2DPool.this.free(this);
        }
    }
}

