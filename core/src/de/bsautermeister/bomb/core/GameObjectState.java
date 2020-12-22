package de.bsautermeister.bomb.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import de.bsautermeister.bomb.screens.game.GameState;

public class GameObjectState<T extends Enum<T>> {

    public interface StateCallback<T> {
        void changed(T previousState, T newState);
    }

    private T current;
    private T previous;
    private float stateTimer;
    private StateCallback<T> stateCallback;
    private boolean frozen;

    public GameObjectState(T initialState) {
        current = initialState;
        previous = initialState;
        resetTimer();
    }

    public void setStateCallback(StateCallback<T> stateCallback) {
        this.stateCallback = stateCallback;
    }

    public void update(float delta) {
        if (frozen) {
            return;
        }

        stateTimer += delta;
    }

    public void set(T state) {
        if (current == state) {
            return;
        }

        previous = current;
        current = state;
        resetTimer();

        frozen = false;

        if (stateCallback != null) {
            stateCallback.changed(previous, current);
        }
    }

    public boolean is(T state) {
        return current == state;
    }

    public boolean was(T state) {
        return previous == state;
    }

    public void resetTimer() {
        stateTimer = 0;
    }

    public boolean changed() {
        return current != previous;
    }

    public T current() {
        return current;
    }

    public T previous() {
        return previous;
    }

    public float timer() {
        return stateTimer;
    }

    public void setTimer(float value) {
        stateTimer = value;
    }

    public void freeze() {
        frozen = true;
    }

    public void unfreeze() {
        frozen = false;
    }

    public boolean isFrozen() {
        return frozen;
    }

    @Override
    public String toString() {
        return "GameObjectState{" +
                "current=" + current +
                ", previous=" + previous +
                ", stateTimer=" + stateTimer +
                ", stateCallback=" + stateCallback +
                ", frozen=" + frozen +
                '}';
    }

    public static class KryoSerializer extends Serializer<GameObjectState> {
        @Override
        public void write(Kryo kryo, Output output, GameObjectState object) {
            output.writeString(object.current.name());
            output.writeString(object.previous.name());
            output.writeFloat(object.stateTimer);
            output.writeBoolean(object.frozen);
        }

        @Override
        public GameObjectState read(Kryo kryo, Input input, Class<GameObjectState> type) {
            GameState currentState = GameState.valueOf(input.readString());
            GameObjectState state = new GameObjectState<>(currentState);
            state.previous = GameState.valueOf(input.readString());
            state.stateTimer = input.readFloat();
            state.frozen = input.readBoolean();
            return state;
        }
    }
}
