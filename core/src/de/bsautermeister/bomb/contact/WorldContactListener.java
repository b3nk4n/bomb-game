package de.bsautermeister.bomb.contact;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.bsautermeister.bomb.objects.Bomb;
import de.bsautermeister.bomb.objects.Player;

public class WorldContactListener implements ContactListener {

    public interface Callbacks {
        void hitGround(Bomb bomb, float strength);
    }

    private final Callbacks callbacks;

    public WorldContactListener(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

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
                boolean hasContactBefore = bomb.hasContact();
                bomb.beginContact(otherFixture);

                if (!hasContactBefore && bomb.hasContact()) {
                    float strength = hitStrength(bomb.getLinearVelocity());
                    callbacks.hitGround(bomb, strength);
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

    private static float hitStrength(Vector2 objectVelocity) {
        System.out.println(objectVelocity.len() + "  " + (objectVelocity.len() - 1) / 4f);
        return MathUtils.clamp((objectVelocity.len() - 1) / 4f, 0f, 1f);
    }
}
