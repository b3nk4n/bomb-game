package de.bsautermeister.bomb.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ManagedPooledEffect {
    private final ParticleEffectPool effectPool;
    private final Array<ParticleEffectPool.PooledEffect> activeEffects = new Array<>(16);

    public ManagedPooledEffect(ParticleEffect effect) {
        effectPool = new ParticleEffectPool(effect, 8, 16);
    }

    public void update(float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isComplete()) {
                activeEffects.removeIndex(i);
                effect.free();
            }
        }
    }

    public void draw(Batch batch) {
        for (ParticleEffectPool.PooledEffect effect : activeEffects) {
            effect.draw(batch);
        }
    }

    public void emit(Vector2 position) {
        emit(position, 1f);
    }

    public void emit(Vector2 position, float scaleFactor) {
        ParticleEffectPool.PooledEffect effect = effectPool.obtain();
        effect.scaleEffect(scaleFactor);
        effect.setPosition(position.x, position.y);
        effect.start();
        activeEffects.add(effect);
    }

    public Array<ParticleEffectPool.PooledEffect> getActiveEffects() {
        return activeEffects;
    }
}
