package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public final class TextureUtils {

    private TextureUtils() {}

    public static TextureRegion singleColorTexture(int rgba8888) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(rgba8888);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        return new TextureRegion(texture);
    }

    public static TextureRegion load(String internalPath) {
        Texture texture = new Texture(internalPath);
        return new TextureRegion(texture);
    }
}
