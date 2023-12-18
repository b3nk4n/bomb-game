package de.bsautermeister.bomb.screens.transition;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

import de.bsautermeister.bomb.core.transition.ScreenTransitionBase;
import de.bsautermeister.bomb.utils.GdxUtils;

public class FadeScreenTransition extends ScreenTransitionBase {

    public FadeScreenTransition(float duration, Interpolation interpolation) {
        super(duration, interpolation);
    }

    @Override
    public void render(SpriteBatch batch, Texture currentScreenTexture, Texture nextScreenTexture, float progress) {
        float percentage = getInterpolatedPercentage(progress);

        int currentScreenWidth = currentScreenTexture.getWidth();
        int currentScreenHeight = currentScreenTexture.getHeight();

        int nextScreenWidth = nextScreenTexture.getWidth();
        int nextScreenHeight = nextScreenTexture.getHeight();

        GdxUtils.clearScreen();

        Color oldColor = batch.getColor().cpy();

        batch.begin();

        // draw current screen
        batch.setColor(1, 1, 1, 1f - percentage);
        batch.draw(currentScreenTexture,
                0, 0,
                0, 0,
                currentScreenWidth, currentScreenHeight,
                1, 1,
                0,
                0, 0,
                currentScreenWidth, currentScreenHeight,
                false, true); // flip y-axis because buffer is y-axis is downward

        // draw next screen
        batch.setColor(1, 1, 1, percentage);
        batch.draw(nextScreenTexture,
                0, 0,
                0, 0,
                nextScreenWidth, nextScreenHeight,
                1, 1,
                0,
                0, 0,
                nextScreenWidth, nextScreenHeight,
                false, true);

        batch.setColor(oldColor);

        batch.end();
    }
}
