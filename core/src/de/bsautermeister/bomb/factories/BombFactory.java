package de.bsautermeister.bomb.factories;

import de.bsautermeister.bomb.objects.Bomb;

public interface BombFactory {
    Bomb createRandomBomb();
    Bomb createTimedBomb();
    Bomb createClusterBomb();
    Bomb createStickyBomb();
    Bomb createBounceStickyBomb();
    Bomb createAirStrikeBomb();
}
