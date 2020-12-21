package de.bsautermeister.bomb.core.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

public class OrthographicCamera2D implements Camera2D {

    private final OrthographicCamera camera = new OrthographicCamera();

    @Override
    public void update(float delta) {
        camera.update();
    }

    @Override
    public Camera getGdxCamera() {
        return camera;
    }

    private final Vector2 outPosition = new Vector2();
    @Override
    public Vector2 getPosition() {
        return outPosition.set(camera.position.x, camera.position.y);
    }

    @Override
    public void setPosition(Vector2 position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        camera.position.set(x, y, camera.position.z);
    }
}
