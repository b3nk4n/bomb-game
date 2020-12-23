package de.bsautermeister.bomb.contact;

public interface Bits {
    short GROUND = 1;
    short WALL = 1 << 1;
    short BALL = 1 << 2;
    short BALL_SENSOR = 1 << 3;
    short BOMB = 1 << 4;

    short ENVIRONMENT = GROUND | WALL;
    short OBJECTS = BALL | BOMB;
}
