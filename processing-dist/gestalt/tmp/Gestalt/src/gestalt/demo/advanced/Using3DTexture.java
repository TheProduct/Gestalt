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


import gestalt.extension.materialplugin.ByteBitmap3D;
import gestalt.extension.materialplugin.JoglMaterialPluginTexture3D;
import gestalt.shape.Plane;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.MovieProducer;
import gestalt.material.texture.bitmap.ByteBitmap;

import data.Resource;


/**
 * this demo shows how to use 3D textures. 3D textures are basically stacked
 * 2D textures, where different layers can be accessed through texture
 * coordinates. if using 3D textures, make sure that 3Dimensional texture
 * coordinates are supplied for the vertices.
 */
public class Using3DTexture
        extends AnimatorRenderer {

    private Mesh _myShape;

    private JoglMaterialPluginTexture3D _myImageTexture;

    private float _myCounter;

    private static final int NUMBER_OF_FRAMES = 16;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.2f);

        /* create shape */
        createMesh();

        Plane myPlane = new Plane();
        myPlane.material().addTexture().load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));
        bin(BIN_3D).add(myPlane);

        /* create texture and store its ID. */
        _myImageTexture = new JoglMaterialPluginTexture3D(true);

        /* load movie */
        MovieProducer myMovieProducer = Bitmaps.getMovieProducer(Resource.getPath("demo/common/subway.mov"));

        /* load movie into texture */
        final int myDepth = NUMBER_OF_FRAMES;
        ByteBitmap3D my3DBitmap = ByteBitmap3D.getDefaultImageBitmap(myMovieProducer.getBitmap().getWidth(),
                                                                     myMovieProducer.getBitmap().getHeight(),
                                                                     myDepth,
                                                                     BITMAP_COMPONENT_ORDER_BGRA);
        for (int i = 0; i < myDepth; i++) {
            ByteBitmap myBitmap = (ByteBitmap)(myMovieProducer.getBitmap());
            myMovieProducer.frame(i);
            myMovieProducer.update();
            /* there is something buggly about the movie producer */
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            my3DBitmap.copyByteBitmap(myBitmap, i);
        }
        _myImageTexture.load(my3DBitmap);

        /* set the texture in the material of your shape */
        _myShape.material().addPlugin(_myImageTexture);
    }

    private void createMesh() {
        /* data to be uploaded */
        float[] myVertices = new float[] {
            displaycapabilities().width / -2, displaycapabilities().height / -2, 0,
            displaycapabilities().width / 2, displaycapabilities().height / -2, 0,
            displaycapabilities().width / 2, displaycapabilities().height / 2, 0,
            displaycapabilities().width / -2, displaycapabilities().height / 2, 0};

        float[] myTexCoords = new float[] {
            0, 0, 0,
            1, 0, 0,
            1, 1, 0,
            0, 1, 0};

        /* create mesh */
        _myShape = drawablefactory().mesh(false,
                                          myVertices, 3,
                                          null, 3,
                                          myTexCoords, 3,
                                          null,
                                          MESH_QUADS);

        /* add mesh to renderer */
        bin(BIN_3D).add(_myShape);
    }

    public void loop(final float theDeltaTime) {
        _myCounter += theDeltaTime * 0.5f;

        final float myModifier = 1 - 2 / (float)NUMBER_OF_FRAMES;

        _myShape.texcoords()[2] = Math.abs((float)Math.sin(_myCounter * 0.3f)) * myModifier;
        _myShape.texcoords()[5] = Math.abs((float)Math.sin(_myCounter * 0.3f)) * myModifier;
        _myShape.texcoords()[8] = Math.abs((float)Math.sin(_myCounter * 0.33f)) * myModifier;
        _myShape.texcoords()[11] = Math.abs((float)Math.sin(_myCounter * 0.33f)) * myModifier;
    }

    public static void main(String[] args) {
        new Using3DTexture().init();
    }
}
