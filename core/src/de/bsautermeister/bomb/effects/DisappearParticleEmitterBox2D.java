package de.bsautermeister.bomb.effects;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.io.BufferedReader;
import java.io.IOException;

public class DisappearParticleEmitterBox2D extends ParticleEmitter {
    final World world;
    final Vector2 startPoint = new Vector2();
    final Vector2 endPoint = new Vector2();
    /** collision flag */
    boolean particleCollided;
    float normalAngle;
    /** If velocities squared is shorter than this it could lead 0 length rayCast that cause c++ assertion at box2d */
    private final static float EPSILON = 0.001f;

    /** default visibility to prevent synthetic accessor creation */
    final RayCastCallback rayCastCallback = new RayCastCallback() {
        @Override
        public float reportRayFixture (Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            DisappearParticleEmitterBox2D.this.particleCollided = true;
            DisappearParticleEmitterBox2D.this.normalAngle = MathUtils.atan2(normal.y, normal.x) * MathUtils.radiansToDegrees;
            return fraction;
        }
    };

    /** /**Constructs ParticleEmitterBox2D using bufferedReader. Box2d World is used for rayCasting. Assumes that particles use same
     * unit system that box2d world does.
     *
     * @param world
     * @param reader
     * @throws IOException */
    public DisappearParticleEmitterBox2D (World world, BufferedReader reader) throws IOException {
        super(reader);
        this.world = world;
    }

    /** Constructs ParticleEmitterBox2D fully copying given emitter attributes. Box2d World is used for rayCasting. Assumes that
     * particles use same unit system that box2d world does.
     *
     * @param world
     * @param emitter */
    public DisappearParticleEmitterBox2D (World world, ParticleEmitter emitter) {
        super(emitter);
        this.world = world;
    }

    @Override
    protected Particle newParticle (Sprite sprite) {
        return new ParticleBox2D(sprite);
    }

    /** Particle that can collide to box2d fixtures */
    private class ParticleBox2D extends Particle {
        private boolean untouched = true;

        public ParticleBox2D (Sprite sprite) {
            super(sprite);
        }

        /** translate particle given amount. Continuous collision detection achieved by using RayCast from oldPos to newPos.
         *
         * @param velocityX
         * @param velocityY */
        @Override
        public void translate (float velocityX, float velocityY) {
            /** If velocities squares summed is shorter than Epsilon it could lead ~0 length rayCast that cause nasty c++ assertion
             * inside box2d. This is so short distance that moving particle has no effect so this return early. */
            if ((velocityX * velocityX + velocityY * velocityY) < EPSILON) return;

            /** Position offset is half of sprite texture size. */
            final float x = getX() + getWidth() / 2f;
            final float y = getY() + getHeight() / 2f;

            /** collision flag to false */
            particleCollided = false;
            startPoint.set(x, y);
            endPoint.set(x + velocityX, y + velocityY);
            if (world != null) world.rayCast(rayCastCallback, startPoint, endPoint);

            /** If ray collided boolean has set to true at rayCallBack */
            if (particleCollided) {
                if (untouched) {
                    // perfect reflection
                    angle = 2f * normalAngle - angle - 180f;
                    angleCos = MathUtils.cosDeg(angle);
                    angleSin = MathUtils.sinDeg(angle);
                    velocityX *= angleCos;
                    velocityY *= angleSin;

                    // only reflect once
                    untouched = false;
                } else {
                    currentLife = 0;
                }
            }

            super.translate(velocityX, velocityY);
        }
    }

    public World getWorld() {
        return world;
    }
}
