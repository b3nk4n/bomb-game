package de.bsautermeister.bomb.core.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

public class BoundedCamera2D implements Camera2D {

    private final float boundsLeft;
    private final float boundsRight;
    private final float boundsTop;
    private final float boundsBottom;

    private final Camera2D camera;

    public BoundedCamera2D(Camera2D camera,
                           float boundsLeft, float boundsRight, float boundsTop, float boundsBottom) {
        this.camera = camera;
        this.boundsLeft = boundsLeft;
        this.boundsRight = boundsRight;
        this.boundsTop = boundsTop;
        this.boundsBottom = boundsBottom;
    }

    final Vector2 tmpPosition = new Vector2();
    @Override
    public void update(float delta) {
        // modify
        tmpPosition.set(camera.getPosition());
        if (tmpPosition.x < boundsLeft) {
            tmpPosition.x = boundsLeft;
        } else if (tmpPosition.x > boundsRight) {
            tmpPosition.x = boundsRight;
        }
        if (tmpPosition.y < boundsBottom) {
            tmpPosition.y = boundsBottom;
        } else if (tmpPosition.y > boundsTop) {
            tmpPosition.y = boundsTop;
        }
        setPosition(tmpPosition);

        // update
        camera.update(delta);
    }

    @Override
    public Camera getGdxCamera() {
        return camera.getGdxCamera();
    }

    @Override
    public Vector2 getPosition() {
        return camera.getPosition();
    }

    @Override
    public void setPosition(Vector2 position) {
        camera.setPosition(position);
    }

    @Override
    public void setPosition(float x, float y) {
        camera.setPosition(x, y);
    }
}
