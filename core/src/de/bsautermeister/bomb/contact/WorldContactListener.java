package de.bsautermeister.bomb.contact;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Player;

public class WorldContactListener implements ContactListener {

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
