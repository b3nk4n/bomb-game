package de.bsautermeister.bomb.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class GdxUtils {

    private GdxUtils() {}

    public static void clearScreen() {
        clearScreen(Color.BLACK);
    }

    public static void clearScreen(Color color) {
        Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    public static ShaderProgram loadCompiledShader(String vertexShaderPath, String fragmentShaderPath) {
        ShaderProgram shader = new ShaderProgram(
                Gdx.files.internal(vertexShaderPath),
                Gdx.files.internal(fragmentShaderPath)
        );

        if (!shader.isCompiled()) {
            Gdx.app.error(GdxUtils.class.getSimpleName(), shader.getLog());
        }

        return shader;
    }
}
