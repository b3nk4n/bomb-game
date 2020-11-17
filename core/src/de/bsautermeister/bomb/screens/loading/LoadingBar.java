package de.bsautermeister.bomb.screens.loading;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LoadingBar extends Actor {

    private final Animation animation;
    private float stateTime;

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
        batch.draw(region, getX(), getY());
    }
}
