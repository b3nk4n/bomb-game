package de.bsautermeister.bomb.core;

import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import java.util.Stack;

public class FrameBufferManager {
    private Stack<FrameBuffer> stack = new Stack<FrameBuffer>();

    public void begin(FrameBuffer buffer) {
        if (!stack.isEmpty()) {
            stack.peek().end();
        }
        stack.push(buffer).begin();
    }

    public void end() {
        stack.pop().end();
        if (!stack.isEmpty()) {
            stack.peek().begin();
        }
    }
}
