package de.bsautermeister.bomb.screens.transition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

import de.bsautermeister.bomb.core.transition.ScreenTransitionBase;
import de.bsautermeister.bomb.utils.GdxUtils;

public class SlideScreenTransition extends ScreenTransitionBase {
    private boolean slideIn;
    private Direction direction;

    public SlideScreenTransition(float duration, Interpolation interpolation, boolean slideIn, Direction direction) {
        super(duration, interpolation);

        if (direction == null) {
            throw new IllegalArgumentException("Direction is required");
        }

        this.slideIn = slideIn;
        this.direction = direction;
    }

    @Override
    public void render(SpriteBatch batch, Texture currentScreenTexture, Texture nextScreenTexture, float progress) {
        float percentage = getInterpolatedPercentage(progress);

        float x = 0;
        float y = 0;

        // draw oder depends on slide type
        Texture topTexture = slideIn ? nextScreenTexture : currentScreenTexture;
        Texture bottomTexture = slideIn ? currentScreenTexture : nextScreenTexture;

        int topTextureWidth = topTexture.getWidth();
        int topTextureHeight = topTexture.getHeight();

        int bottomTextureWidth = bottomTexture.getWidth();
        int bottomTextureHeight = bottomTexture.getHeight();

        // calculate position offset
        if (direction.isHorizontal()) {
            float sign = direction.isLeft() ? -1 : 1;
            x = sign * topTextureWidth * percentage;

            if (slideIn) {
                sign = -sign;
                x += sign * topTextureWidth;
            }
        } else if (direction.isVertical()) {
            float sign = direction.isDown() ? -1 : 1;
            y = sign * topTextureHeight * percentage;

            if (slideIn) {
                sign = -sign;
                y += sign * topTextureHeight;
            }
        }

        // drawing
        GdxUtils.clearScreen();
        batch.begin();

        batch.draw(bottomTexture,
                0, 0,
                0, 0,
                bottomTextureWidth, bottomTextureHeight,
                1, 1,
                0,
                0, 0,
                bottomTextureWidth, bottomTextureHeight,
                false, true);

        batch.draw(topTexture,
                x, y,
                0, 0,
                bottomTextureWidth, bottomTextureHeight,
                1, 1,
                0,
                0, 0,
                bottomTextureWidth, bottomTextureHeight,
                false, true);

        batch.end();
    }
}
