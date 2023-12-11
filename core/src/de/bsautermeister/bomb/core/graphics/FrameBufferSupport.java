package de.bsautermeister.bomb.core.graphics;

import static com.badlogic.gdx.Application.ApplicationType.iOS;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER;
import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_BINDING;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.BufferUtils;

import java.nio.IntBuffer;

public class FrameBufferSupport {

    private FrameBuffer fbo;

    /**
     * Only strictly needed for iOS, but generally working, there is a bug in LibGdx 1.12.1, that
     * {@link FrameBuffer#end()} might not properly unbind to the screen / default frame buffer
     * handle of the screen. While there is actually special handling for iOS implemented in
     * {@link com.badlogic.gdx.graphics.glutils.GLFrameBuffer}, the implementation to only set a
     * static {@code defaultFramebufferHandle} once for all FrameBuffers seems to be incorrect.
     * Because when using different screens with different back buffers, the value can actually
     * differ within a single game. And therefore should better be set each time.
     */
    private int previousFboIdx;

    public void begin(FrameBuffer fbo) {
        this.fbo = fbo;

        if (Gdx.app.getType() == iOS) {
            IntBuffer oldFbo = BufferUtils.newIntBuffer(1);
            Gdx.gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, oldFbo);
            previousFboIdx = oldFbo.get();
        }

        fbo.begin();
    }

    public void end() {
        fbo.end();
        fbo = null;

        if (Gdx.app.getType() == iOS) {
            Gdx.gl.glBindFramebuffer(GL_FRAMEBUFFER, previousFboIdx);
        }
    }
}
