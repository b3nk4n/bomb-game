package de.bsautermeister.bomb.core.transition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface ScreenTransition {
    float getDuration();
    float getInterpolatedPercentage(float progress);

    void render(SpriteBatch batch, Texture currentScreenTexture, Texture nextScreenTexture, float progress);
}
