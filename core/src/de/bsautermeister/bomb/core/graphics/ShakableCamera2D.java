package de.bsautermeister.bomb.core.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ShakableCamera2D implements Camera2D {

    private final Camera2D camera;

    private float shakeTimer;
    private float time;

    public ShakableCamera2D(Camera2D camera) {
        this.camera = camera;
    }

    private Vector2 tmpPosition = new Vector2();
    public void update(float delta) {
        time += delta;

        if (shakeTimer <= 0f) {
            camera.update(delta);
            return;
        }

        shakeTimer = Math.max(0f, shakeTimer - delta);
        float shakeStrength = Interpolation.pow3Out.apply(MathUtils.clamp(shakeTimer, 0f, 1f));

        float offsetX = (float) Math.sin(time * 19) * 0.02f * shakeStrength;
        float offsetY = (float) Math.cos(time * 23) * 0.017f * shakeStrength;

        // modify
        tmpPosition.set(camera.getPosition());
        camera.setPosition(tmpPosition.x + offsetX, tmpPosition.y + offsetY);

        // update
        camera.update(delta);
    }

    public void shake(float time) {
        shakeTimer = Math.max(shakeTimer, time);
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
