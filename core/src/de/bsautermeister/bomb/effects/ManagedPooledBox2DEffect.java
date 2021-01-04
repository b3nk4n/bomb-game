package de.bsautermeister.bomb.effects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class ManagedPooledBox2DEffect {
    private final ParticleEffectBox2DPool effectPool;
    private final Array<ParticleEffectBox2DPool.PooledBox2DEffect> activeEffects = new Array<>(16);

    public ManagedPooledBox2DEffect(ParticleEffectBox2D effect) {
        effectPool = new ParticleEffectBox2DPool(effect, 16, 32);
    }

    public void update(float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            ParticleEffectBox2DPool.PooledBox2DEffect effect = activeEffects.get(i);
            effect.update(delta);
            effect.getEmitters().get(0);
            if (effect.isComplete()) {
                activeEffects.removeIndex(i);
                effect.free();
            }
        }
    }

    public void draw(Batch batch) {
        for (ParticleEffectBox2DPool.PooledBox2DEffect effect : activeEffects) {
            effect.draw(batch);
        }
    }

    public void emit(Vector2 position) {
        emit(position, 1f);
    }

    public void emit(Vector2 position, float scaleFactor) {
        emit(position.x, position.y, scaleFactor);
    }

    public void emit(float x, float y, float scaleFactor) {
        ParticleEffectBox2DPool.PooledBox2DEffect effect = effectPool.obtain();
        effect.scaleEffect(scaleFactor);
        effect.setPosition(x, y);
        effect.start();
        activeEffects.add(effect);
    }

    public Array<ParticleEffectBox2DPool.PooledBox2DEffect> getActiveEffects() {
        return activeEffects;
    }
}
