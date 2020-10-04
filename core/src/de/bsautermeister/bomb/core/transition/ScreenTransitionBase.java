package de.bsautermeister.bomb.core.transition;

import com.badlogic.gdx.math.Interpolation;

public abstract class ScreenTransitionBase implements ScreenTransition {
    private final float duration;
    private final Interpolation interpolation;

    public ScreenTransitionBase(float duration, Interpolation interpolation) {
        this.duration = duration;
        this.interpolation = interpolation;
    }

    @Override
    public float getDuration() {
        return duration;
    }

    @Override
    public float getInterpolatedPercentage(float progress) {
        return interpolation.apply(progress);
    }
}
