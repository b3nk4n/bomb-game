package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public final class PolygonUtils {

    private PolygonUtils() { }

    public static float[] createPolygon(float radius, int segments) {
        float[] vertices = new float[segments * 2];
        polygon(vertices, radius, segments, Vector2.Zero, 0f);
        return vertices;
    }

    public static int polygon(float[] vertices, float radius, int segments, Vector2 position, float rotation) {
        float radianStep = MathUtils.PI2 / segments;
        for (int i = 0; i < segments; ++i) {
            float radians = rotation + i * radianStep;
            vertices[2 * i] = position.x + MathUtils.cos(radians) * radius;
            vertices[2 * i + 1] = position.y + MathUtils.sin(radians) * radius;
        }
        return segments * 2;
    }

    public static int spikes(float[] vertices, float innerRadius, float outerRadius, int count, Vector2 position, float rotation) {
        float radianStep = MathUtils.PI2 / (count * 2);
        for (int i = 0; i < count * 2; ++i) {
            float radians = rotation + i * radianStep;
            float radius = i % 2 == 0 ? innerRadius : outerRadius;
            vertices[2 * i] = position.x + MathUtils.cos(radians) * radius;
            vertices[2 * i + 1] = position.y + MathUtils.sin(radians) * radius;
        }
        return count * 4;
    }
}
