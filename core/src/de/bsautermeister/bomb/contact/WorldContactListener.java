package de.bsautermeister.bomb.contact;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Logger;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Player;
import de.bsautermeister.bomb.objects.StickyBomb;

public class WorldContactListener implements ContactListener {

    private static final Logger LOG = new Logger(WorldContactListener.class.getSimpleName(), Cfg.LOG_LEVEL);

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Player player;
        Bomb bomb;

        int collisionDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        switch (collisionDef) {
            case Bits.BALL_SENSOR | Bits.GROUND:
                player = (Player) resolve(fixtureA, fixtureB, Bits.BALL_SENSOR).getUserData();
                player.beginGroundContact();
                break;
            case Bits.BOMB | Bits.GROUND:
            case Bits.BOMB | Bits.BALL:
                bomb = (Bomb) resolve(fixtureA, fixtureB, Bits.BOMB).getUserData();
                Fixture otherFixture = resolve(fixtureA, fixtureB, ~Bits.BOMB);
                bomb.beginContact(otherFixture);

                if (bomb instanceof StickyBomb) {
                    Body otherBody = resolve(fixtureA, fixtureB, ~Bits.BOMB).getBody();
                    ((StickyBomb) bomb).stick(otherBody);
                }
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Player player;
        Bomb bomb;
        int collisionDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        switch (collisionDef) {
            case Bits.BALL_SENSOR | Bits.GROUND:
                player = (Player) resolve(fixtureA, fixtureB, Bits.BALL_SENSOR).getUserData();
                player.endGroundContact();
                break;
            case Bits.BOMB | Bits.GROUND:
            case Bits.BOMB | Bits.BALL:
                bomb = (Bomb) resolve(fixtureA, fixtureB, Bits.BOMB).getUserData();
                Fixture otherFixture = resolve(fixtureA, fixtureB, ~Bits.BOMB);
                bomb.endContact(otherFixture);
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private Fixture resolve(Fixture fixtureA, Fixture fixtureB, int categoryBits) {
        return ((fixtureA.getFilterData().categoryBits & categoryBits) != 0)
                ? fixtureA : fixtureB;
    }
}
