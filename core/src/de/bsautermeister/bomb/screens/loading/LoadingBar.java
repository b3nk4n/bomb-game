package de.bsautermeister.bomb.screens.loading;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LoadingBar extends Actor {
    private final Animation animation;
    private float stateTime;
    private Color tintColor = Color.WHITE;

    public LoadingBar(Animation animation) {
        this.animation = animation;
    }

    @Override
    public void act(float delta) {
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion region = (TextureRegion) animation.getKeyFrame(stateTime);
        float previousColor = batch.getPackedColor();
        batch.setColor(tintColor);
        batch.draw(region, getX(), getY());
        batch.setPackedColor(previousColor);
    }

    public void setTintColor(Color tintColor) {
        this.tintColor = tintColor;
    }
}
