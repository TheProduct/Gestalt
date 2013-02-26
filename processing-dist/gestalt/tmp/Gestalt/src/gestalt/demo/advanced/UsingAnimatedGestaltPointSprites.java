/*
 * Gestalt
 *
 * Copyright (C) 2012 Patrick Kochlik + Dennis Paul
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt.demo.advanced;


import gestalt.material.PointSprite;
import gestalt.render.AnimatorRenderer;
import gestalt.material.Color;
import gestalt.shape.Mesh;
import gestalt.material.texture.Bitmaps;

import mathematik.Random;
import mathematik.Vector3f;

import data.Resource;


/**
 * this demo shows how to use gestalt point sprites in gestalt
 */

public class UsingAnimatedGestaltPointSprites
    extends AnimatorRenderer {

    private ParticleManager _myParticleManager;

    private Mesh _myMesh;

    private PointSprite _myPointSprites;

    public void setup() {

        /* particles manager */
        _myParticleManager = new ParticleManager();

        /* create backup mesh */
        _myMesh = drawablefactory().mesh(false,
                                         _myParticleManager.positionbackuparray, 3,
                                         _myParticleManager.colorbackuparray, 4,
                                         null, 0,
                                         null,
                                         MESH_POINTS);

        /* create texture */
        _myPointSprites = new PointSprite();
        _myPointSprites.load(Bitmaps.getBitmap(Resource.getStream("demo/common/flower-particle.png"), "flower"));
        _myPointSprites.quadric = new float[] {
                                  0.001f,
                                  0.000002f,
                                  0.00001f};
        _myPointSprites.pointsize = 50;
        _myPointSprites.minpointsize = 10;
        _myPointSprites.maxpointsize = 250;

        _myMesh.material().addPlugin(_myPointSprites);
        _myMesh.material().depthtest = false;
        _myMesh.material().transparent = true;

        /* add to renderer */
        bin(BIN_3D).add(_myMesh);

        /* set framerate */
        framerate(UNDEFINED);
    }


    public void loop(final float theDeltaTime) {
        _myParticleManager.loop();
    }


    private class ParticleManager {

        public final float[] positionbackuparray;

        public final float[] colorbackuparray;

        private final Particle[] particles;

        private final Random _myRandom = new Random();

        private static final int NUMBER_OF_PARTICLES = 4000;

        private static final float SPAWN_DEPTH = -400;

        public ParticleManager() {

            positionbackuparray = new float[NUMBER_OF_PARTICLES * 3];
            colorbackuparray = new float[NUMBER_OF_PARTICLES * 4];

            /* create particles */
            particles = new Particle[NUMBER_OF_PARTICLES];
            for (int i = 0; i < particles.length; i++) {
                particles[i] = new Particle();
                particles[i].color.set(1, 0f);
                particles[i].speed = (float) Math.random() * 0.4f + 0.1f;
            }
        }


        public void loop() {
            for (int i = 0; i < particles.length; i++) {
                /* check if particle is active.
                 * if so, animate the particle, if not, reset the particle
                 * to a random start position */
                if (particles[i].isActive) {
                    Vector3f myDirection = new Vector3f(event().mouseX, event().mouseY, 0);
                    myDirection.sub(particles[i].position);
                    final float myDistance = myDirection.length();
                    myDirection.scale(particles[i].speed / myDistance);
                    particles[i].position.add(myDirection);

                    particles[i].color.a = 1 - myDistance / ( -1.5f * SPAWN_DEPTH);

                    if (myDistance < 1) {
                        particles[i].isActive = false;
                    }
                } else {
                    particles[i].isActive = true;
                    particles[i].position.set(_myRandom.getFloat( -640, 640),
                                              _myRandom.getFloat( -480, 480),
                                              SPAWN_DEPTH);
                    particles[i].color.a = 0;
                }

                /* map position to backup array */
                positionbackuparray[i * 3 + 0] = particles[i].position.x;
                positionbackuparray[i * 3 + 1] = particles[i].position.y;
                positionbackuparray[i * 3 + 2] = particles[i].position.z;

                /* map color4f to backup array */
                colorbackuparray[i * 4 + 0] = particles[i].color.r;
                colorbackuparray[i * 4 + 1] = particles[i].color.g;
                colorbackuparray[i * 4 + 2] = particles[i].color.b;
                colorbackuparray[i * 4 + 3] = particles[i].color.a;
            }
        }


        private class Particle {
            Color color = new Color();

            Vector3f position = new Vector3f();

            float speed = 1;

            boolean isActive = false;
        }

    }


    public static void main(String[] args) {
        new UsingAnimatedGestaltPointSprites().init();
    }
}
