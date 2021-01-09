package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Vector2;

public class AirStrikeTargetMarker {
    private final Vector2 position;
    private float ttl;
    private final float totalTime;

    public AirStrikeTargetMarker(Vector2 position, float ttl) {
        this.position = new Vector2(position);
        this.ttl = ttl;
        this.totalTime = ttl;
    }

    public void update(float delta) {
        ttl -= delta;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getProgress() {
        return 1f - Math.max(0f, ttl / totalTime);
    }

    public boolean isReady() {
        return ttl <= 0f;
    }
}
