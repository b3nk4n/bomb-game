package de.bsautermeister.bomb.screens.transition;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

import de.bsautermeister.bomb.core.transition.ScreenTransitionBase;
import de.bsautermeister.bomb.utils.GdxUtils;

public class SlideScreenTransition extends ScreenTransitionBase {

    public enum SlideType {
        SLIDE_IN,
        SLIDE_OUT,
        SLIDE_PARALLEL
    }

    private final SlideType slideType;
    private final Direction direction;

    public SlideScreenTransition(float duration, Interpolation interpolation, SlideType slideType, Direction direction) {
        super(duration, interpolation);

        if (direction == null) {
            throw new IllegalArgumentException("Direction is required");
        }

        this.slideType = slideType;
        this.direction = direction;
    }

    @Override
    public void render(SpriteBatch batch, Texture currentScreenTexture, Texture nextScreenTexture, float progress) {
        float percentage = getInterpolatedPercentage(progress);

        float xTop = 0;
        float yTop = 0;
        float xBottom = 0;
        float yBottom = 0;

        // draw order depends on slide type
        Texture topTexture = slideType == SlideType.SLIDE_IN ?
                nextScreenTexture : currentScreenTexture;
        Texture bottomTexture = slideType == SlideType.SLIDE_IN ?
                currentScreenTexture : nextScreenTexture;

        int topTextureWidth = topTexture.getWidth();
        int topTextureHeight = topTexture.getHeight();

        int bottomTextureWidth = bottomTexture.getWidth();
        int bottomTextureHeight = bottomTexture.getHeight();

        // calculate position offset
        if (direction.isHorizontal()) {
            float sign = direction.isLeft() ? -1 : 1;
            xTop = sign * topTextureWidth * percentage;

            if (slideType == SlideType.SLIDE_IN) {
                xTop -= sign * topTextureWidth;
            } else if (slideType == SlideType.SLIDE_PARALLEL) {
                xBottom = xTop - sign * topTextureWidth;
            }
        } else if (direction.isVertical()) {
            float sign = direction.isDown() ? -1 : 1;
            yTop = sign * topTextureHeight * percentage;

            if (slideType == SlideType.SLIDE_IN) {
                yTop -= sign * topTextureHeight;
            } else if (slideType == SlideType.SLIDE_PARALLEL) {
                yBottom = yTop - sign * topTextureHeight;
            }
        }

        // drawing
        GdxUtils.clearScreen();
        batch.begin();

        batch.draw(bottomTexture,
                xBottom, yBottom,
                0, 0,
                bottomTextureWidth, bottomTextureHeight,
                1, 1,
                0,
                0, 0,
                bottomTextureWidth, bottomTextureHeight,
                false, true);

        batch.draw(topTexture,
                xTop, yTop,
                0, 0,
                topTextureWidth, topTextureHeight,
                1, 1,
                0,
                0, 0,
                topTextureWidth, topTextureHeight,
                false, true);

        batch.end();
    }
}
