package de.bsautermeister.bomb.core;

public class GameObjectState<T extends Enum<T>> {

    public interface StateCallback<T> {
        void changed(T previousState, T newState);
    }

    private T current;
    private T previous;
    private float stateTimer;
    private StateCallback<T> stateCallback;
    private boolean frozen;

    public GameObjectState() {
        // for Kryo
    }

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
}
