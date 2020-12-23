package de.bsautermeister.bomb.core.graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;

public class ExtendedShapeRenderer extends ShapeRenderer {
    private final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    public void polygon(float[] vertices, int offset, int count) {
        ShapeType shapeType = getCurrentType();
        ImmediateModeRenderer renderer = getRenderer();
        Color color = getColor();

        if (shapeType != ShapeType.Filled && shapeType != ShapeType.Line)
            throw new GdxRuntimeException("Must call begin(ShapeType.Filled) or begin(ShapeType.Line)");
        if (count < 6)
            throw new IllegalArgumentException("Polygons must contain at least 3 points.");
        if (count % 2 != 0)
            throw new IllegalArgumentException("Polygons must have an even number of vertices.");

        //check(shapeType, null, count);

        final float firstX = vertices[0];
        final float firstY = vertices[1];
        if (shapeType == ShapeType.Line) {
            for (int i = offset, n = offset + count; i < n; i += 2) {
                final float x1 = vertices[i];
                final float y1 = vertices[i + 1];

                final float x2;
                final float y2;

                if (i + 2 >= count) {
                    x2 = firstX;
                    y2 = firstY;
                } else {
                    x2 = vertices[i + 2];
                    y2 = vertices[i + 3];
                }

                renderer.color(color);
                renderer.vertex(x1, y1, 0);
                renderer.color(color);
                renderer.vertex(x2, y2, 0);

            }
        } else {
            ShortArray triangles = triangulator.computeTriangles(vertices, offset, count);

            for (int i = 0; i < triangles.size; i += 3) {
                int p1 = triangles.get(i) * 2;
                int p2 = triangles.get(i + 1) * 2;
                int p3 = triangles.get(i + 2) * 2;

                float a1 = vertices[p1];
                float a2 = vertices[p1 + 1];
                float b1 = vertices[p2];
                float b2 = vertices[p2 + 1];
                float c1 = vertices[p3];
                float c2 = vertices[p3 + 1];

                triangle(a1, a2, b1, b2, c1, c2);
            }
        }
    }
}
