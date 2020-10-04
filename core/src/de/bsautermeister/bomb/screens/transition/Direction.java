package de.bsautermeister.bomb.screens.transition;

public enum Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public boolean isUp() {
        return this == UP;
    }

    public boolean isDown() {
        return this == DOWN;
    }

    public boolean isLeft() {
        return this == LEFT;
    }

    public boolean isRight() {
        return this == RIGHT;
    }

    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    public boolean isVertical() {
        return this == UP || this == DOWN;
    }

    public Direction opposite() {
        if (isLeft())
            return RIGHT;
        if (isRight())
            return LEFT;
        if (isUp())
            return DOWN;
        if (isDown())
            return UP;
        throw new IllegalArgumentException("No opposite of direction=" + this);
    }

    public boolean isOpposite(Direction direction) {
        return this.opposite() == direction;
    }

    public float angle() {
        if (this == RIGHT) {
            return 0f;
        }
        if (this == UP) {
            return 90f;
        }
        if (this == LEFT) {
            return 180f;
        }
        return 270f;
    }
}
