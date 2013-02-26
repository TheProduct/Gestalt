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

import gestalt.material.texture.Bitmaps;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.material.PointSprite;

import mathematik.Vector2f;

import data.Resource;


/**
 *
 * this demo shows how to use a pointsprites.
 */
public class UsingGestaltPointSprites
        extends AnimatorRenderer {

    private static final int NUMBER_OF_VERTICES = 500 * 500;

    private static final boolean USE_VBO = true;

    private Mesh _myMesh;

    private PointSprite _myPointSprites;

    public void setup() {

        cameramover(true);
        fpscounter(true);
        camera().farclipping = 10000;

        /* data to be uploaded */
        float[] myVertices = new float[NUMBER_OF_VERTICES * 3];

        /* assign data to every single vertex */
        final int myEdgeSize = (int)Math.sqrt(NUMBER_OF_VERTICES);
        final Vector2f myScale = new Vector2f(20, 20);
        for (int i = 0; i < NUMBER_OF_VERTICES; i++) {
            final int x = i % myEdgeSize;
            final int y = i / myEdgeSize;
            myVertices[i * 3 + 0] = x * myScale.x - myScale.x * myEdgeSize / 2;
            myVertices[i * 3 + 1] = 0;
            myVertices[i * 3 + 2] = y * myScale.y - myScale.y * myEdgeSize / 2;
        }

        /* create mesh */
        _myMesh = drawablefactory().mesh(USE_VBO,
                                         myVertices, 3,
                                         null, 0,
                                         null, 0,
                                         null,
                                         MESH_POINTS);

        /* create texture */
        _myPointSprites = new PointSprite();
        _myPointSprites.load(Bitmaps.getBitmap(Resource.getStream("demo/common/flower-particle.png"), "flower"));
        _myPointSprites.quadric = new float[] {10, 0.2f, 0.001f};
        _myPointSprites.pointsize = 300;
        _myPointSprites.minpointsize = 4;
        _myPointSprites.maxpointsize = 256;

        _myMesh.material().addPlugin(_myPointSprites);
        _myMesh.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        _myMesh.material().depthtest = false;
        _myMesh.material().transparent = true;

        /* add to renderer */
        bin(BIN_3D).add(_myMesh);

        /* set framerate */
        framerate(UNDEFINED);
    }

    public void loop(float theDeltaTime) {
        camera().upvector().set(camera().getUp());
        if (event().shift) {
            _myPointSprites.position().set(event().normalized_mouseX, event().normalized_mouseY);
        } else {
            _myPointSprites.scale().set(event().normalized_mouseX, event().normalized_mouseY);
        }
    }

    public static void main(String[] args) {
        new UsingGestaltPointSprites().init();
    }
}
