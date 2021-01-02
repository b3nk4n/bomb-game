package de.bsautermeister.bomb.core.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public abstract class Camera2D {
    public abstract void update(float delta);

    public abstract Camera getGdxCamera();

    public abstract Vector2 getPosition();

    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    public abstract void setPosition(float x, float y);

    public boolean isInView(Vector2 position) {
        return getGdxCamera().frustum.pointInFrustum(position.x, position.y, 0f);
    }
}
