package de.bsautermeister.bomb.core.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public interface Camera2D {
    void update(float delta);
    Camera getGdxCamera();
    Vector2 getPosition();
    void setPosition(Vector2 position);
    void setPosition(float x, float y);
}
