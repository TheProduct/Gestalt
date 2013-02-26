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


package gestalt.candidates;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.extension.framebufferobject.JoglFBODataReader;
import gestalt.extension.framebufferobject.JoglFrameBufferObject;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.PointSprite;
import gestalt.material.texture.Bitmap;
import gestalt.render.bin.Bin;
import gestalt.render.bin.RenderBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.OrthoSetup;
import gestalt.shape.Plane;
import gestalt.shape.PointSpriteCloud;

import mathematik.Vector3f;

import java.util.Vector;


public class JoglLEDDisplayCPU
        extends JoglLEDDisplay {

    private final JoglFrameBufferObject _myFBO;

    private final JoglFBODataReader _myDataReader;

    private final PointSpriteCloud _myLEDs;

    private final Plane _myMask;

    private final RenderBin _myBin;

    public JoglLEDDisplayCPU(final int theWidth, final int theHeight,
                             final Bin theFBOBin,
                             final Bitmap thePointSprite) {

        /* create FBO as brightness source */
        _myFBO = JoglFrameBufferObject.createRectangular(theWidth, theHeight);

        /* create fbo data reader */
        _myDataReader = new JoglFBODataReader(_myFBO);

        /* create pointsprite */
        _myLEDs = new PointSpriteCloud();
        _myLEDs.loadBitmap(thePointSprite);
        _myLEDs.ATTENUATION_CONSTANT = 10;
        _myLEDs.ATTENUATION_LINEAR = 0.002f;
        _myLEDs.ATTENUATION_QUAD = 0.000001f;
        _myLEDs.POINT_SIZE = 16;
        _myLEDs.MIN_POINT_SIZE = 8;
        _myLEDs.MAX_POINT_SIZE = 128;

        for (int i = 0; i < theWidth * theHeight; i++) {
            final Vector3f v = new Vector3f();
            v.x = i % theWidth - theWidth / 2;
            v.y = i / theWidth - theHeight / 2;
            v.z = 0;
            _myLEDs.add(v, new Color(1, 1, 1, 1));
        }
        _myLEDs.material().blendmode = Gestalt.MATERIAL_BLEND_INVERS_MULTIPLY;
        _myLEDs.material().depthtest = false;

        /* setup bin */
        _myBin = new RenderBin();

        /* setup mask */
        _myMask = new Plane();
        _myMask.material().depthtest = false;
        _myMask.setActive(false);
        _myMask.scale(theWidth, theHeight);


        _myFBO.bin().add(_myBin);
        _myFBO.add(new OrthoSetup());
        _myFBO.bin().add(_myMask);

        theFBOBin.add(_myFBO);
        theFBOBin.add(_myDataReader);
    }

    private void update() {
        final float[] myData = _myDataReader.getDataRef();
        for (int i = 0; i < myData.length; i += 4) {
            final Color c = _myLEDs.colors().get(i / 4);
            c.r = myData[i + 0];
            c.g = myData[i + 1];
            c.b = myData[i + 2];
            c.a = myData[i + 3];
        }
    }

    /* Drawable */
    public void draw(GLContext theRenderContext) {
        update();
        _myLEDs.draw(theRenderContext);
    }

    /* JoglLEDDisplay */
    public void addMask(Bitmap theBitmap) {
        _myMask.setActive(true);
        _myMask.material().addTexture().load(theBitmap);
    }

    public Vector3f scale() {
        return _myLEDs.scale();
    }

    public Color backgroundcolor() {
        return _myFBO.backgroundcolor();
    }

    public RenderBin bin() {
        return _myBin;
    }

    public Camera camera() {
        return _myFBO.camera();
    }

    public PointSprite getPointSpriteMaterialPlugin() {
        return _myLEDs.getPointSpriteMaterial();
    }

    public Material material() {
        return _myLEDs.material();
    }

    public Vector<Vector3f> points() {
        return _myLEDs.vertices();
    }

    public JoglFrameBufferObject fbo() {
        return _myFBO;
    }
}
