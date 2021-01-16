package de.bsautermeister.bomb.screens.loading;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LoadingBar extends Actor {
    private final TextureRegion textureRegion;
    private Color tintColor = Color.WHITE;

    public LoadingBar(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float previousColor = batch.getPackedColor();
        batch.setColor(tintColor);
        batch.draw(textureRegion, getX(), getY());
        batch.setPackedColor(previousColor);
    }

    public void setTintColor(Color tintColor) {
        this.tintColor = tintColor;
    }
}
