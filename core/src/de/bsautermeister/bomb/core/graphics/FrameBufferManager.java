package de.bsautermeister.bomb.core.graphics;

import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_BINDING;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;
import java.util.Stack;

public class FrameBufferManager {

    /**
     * Using a stack is helpful when using FBOs within an FBO context.
     * In this game, this is for example the case during screen transitions of the game scene.
     */
    private final Stack<FrameBuffer> fboStack = new Stack<>();

    /**
     * Only strictly needed for iOS, but generally working, there is a bug in LibGdx 1.12.1, that
     * {@link FrameBuffer#end()} might not properly unbind to the screen / default frame buffer
     * handle of the screen. While there is actually special handling for iOS implemented in
     * {@link com.badlogic.gdx.graphics.glutils.GLFrameBuffer}, the implementation to only set a
     * static {@code defaultFramebufferHandle} once for all FrameBuffers seems to be incorrect.
     * Because when using different screens with different back buffers, the value can actually
     * differ within a single game. And therefore should better be set each time.
     */
    private int defaultFboIdx;

    public void begin(FrameBuffer buffer) {
        if (!fboStack.isEmpty()) {
            fboStack.peek().end();
        }

        if (fboStack.isEmpty()) {
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                IntBuffer oldFbo = BufferUtils.newIntBuffer(1);
                Gdx.gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, oldFbo);
                defaultFboIdx = oldFbo.get();
            }
        }

        fboStack.push(buffer).begin();
    }

    public void end() {
        fboStack.pop().end();
        if (!fboStack.isEmpty()) {
            fboStack.peek().begin();
        }

        if (fboStack.isEmpty()) {
            if (Gdx.app.getType() == Application.ApplicationType.iOS) {
                Gdx.gl.glBindFramebuffer(GL_FRAMEBUFFER, defaultFboIdx);
            }
        }
    }
}
