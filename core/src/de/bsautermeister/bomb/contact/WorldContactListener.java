package de.bsautermeister.bomb.contact;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Logger;

import de.bsautermeister.bomb.Cfg;
import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Player;

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
                player = (Player) resolveUserData(fixtureA, fixtureB, Bits.BALL_SENSOR);
                player.beginGroundContact();
                break;
            case Bits.BOMB | Bits.GROUND:
            case Bits.BOMB | Bits.BALL:
                bomb = (Bomb) resolveUserData(fixtureA, fixtureB, Bits.BOMB);
                bomb.contact();
                break;
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Player player;

        int collisionDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;
        switch (collisionDef) {
            case Bits.BALL_SENSOR | Bits.GROUND:
                player = (Player) resolveUserData(fixtureA, fixtureB, Bits.BALL_SENSOR);
                player.endGroundContact();
                break;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private Object resolveUserData(Fixture fixtureA, Fixture fixtureB, int categoryBits) {
        return ((fixtureA.getFilterData().categoryBits & categoryBits) != 0)
                ? fixtureA.getUserData() : fixtureB.getUserData();
    }
}
