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


import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.material.TexturePlugin;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.AbstractShape;
import gestalt.material.texture.Bitmaps;

import data.Resource;


/**
 * this demo shows how to use multi-texturing in gestalt
 *
 * 1. create a shape that is able to perform multitexturing
 * 2. check for "GL_multitexture"
 * 3. asign a texture to every texture unit
 * 4. draw your multitextured shape with seperate texturecoordinates for every unit
 * 5. disable all used texture units
 *
 */

public class UsingMultiTexturing
    extends AnimatorRenderer {

    private boolean _myUseMultiTextures = true;

    public void setup() {
        /*
         * create custom made multitextured plane;
         * see also "UsingCustomShapes"
         */
        MultiTexturedPlane myMultiTexturedPlane = new MultiTexturedPlane();

        /* add plane to renderbin */
        bin(BIN_3D).add(myMultiTexturedPlane);

        /* print help */
        printHelp();
    }


    public void loop(float theDeltaTime) {
        if (event().keyPressed) {
            switch (event().key) {
                case 'm':
                    _myUseMultiTextures = false;
                    break;
                case 'M':
                    _myUseMultiTextures = true;
                    break;
                case 'h':
                    printHelp();
                    break;
            }
        }
    }


    private void printHelp() {
        System.out.println("### INFO / How to use the demo:");
        System.out.println("### press 'h' for help");
        System.out.println("### press 'M' to enable multitexturing");
        System.out.println("### press 'm' to disable multitexturing");
    }


    public static void main(String[] args) {
        new UsingMultiTexturing().init();
    }


    /**
     *
     * this class describes a shape that performs all
     * openGL states in its draw() method that are
     * required for multitexturing;
     */
    private class MultiTexturedPlane
        extends AbstractShape {

        private TexturePlugin _myImageTexture;

        private TexturePlugin _myMaskTexture;

        private float _myCounter;

        private boolean myFirstFrame = true;

        public MultiTexturedPlane() {
            /* image texture */
            _myImageTexture = (TexturePlugin) drawablefactory().texture();
            _myImageTexture.setFilterType(TEXTURE_FILTERTYPE_LINEAR);
            _myImageTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/auto.png")));

            /* mask texture */
            _myMaskTexture = (TexturePlugin) drawablefactory().texture();
            _myMaskTexture.setFilterType(TEXTURE_FILTERTYPE_LINEAR);
            _myMaskTexture.load(Bitmaps.getBitmap(Resource.getStream("demo/common/mask256.png")));
        }


        private int getTextureUnits(GL gl) {
            int[] myNumberOfTextureUnits = new int[1];
            /** @todo JSR-231 -- added 0 */
            gl.glGetIntegerv(GL.GL_MAX_TEXTURE_UNITS, myNumberOfTextureUnits, 0);
            return myNumberOfTextureUnits[0];
        }


        public void draw(GLContext theRenderContext) {
            final GL gl = (  theRenderContext).gl;
            final GLU glu = (  theRenderContext).glu;

            /* initialize
             * it s a good idea to check your extensions before you use multitexturing;
             * you will need "GL_multitexture" otherwise multitexturing
             * is not supported.
             * thus you should always have a solution for not supported extensions
             */
            if (myFirstFrame) {
                if (!gl.isExtensionAvailable("GL_multitexture")) {
                    _myUseMultiTextures = false;
                    System.out.println("### INFO @ MultiTexturedPlane / GL_multitexture not supported.");
                }

                /*
                 * check how many texture units you have;
                 * for each unit you can asign one texture
                 */
                int myNumberofTextureUnits = getTextureUnits(gl);
                System.out.println("### INFO @ MultiTexturedPlane / available texture units: " + myNumberofTextureUnits);

                /* update textures */
                _myImageTexture.update(gl, glu);
                _myMaskTexture.update(gl, glu);

                myFirstFrame = false;
            }

            /* set color */
            gl.glColor4f(1f, 1f, 1f, 1f);

            /*
             * get things ready
             * note that blendmodes etc. has to be set for each unit seperately.
             */
            if (_myUseMultiTextures) {
                /*
                 * change active texture unit
                 * you  call GL.GL_TEXTURE0 on every available texture unit
                 * you want to use
                 * texture unit 0: GL.GL_TEXTURE0
                 * texture unit 1: GL.GL_TEXTURE1
                 * ...
                 * here, we only need to set texture unit 0 and 1
                 */
                /** @todo JSR-231 -- added 0 */
                gl.glActiveTexture(GL.GL_TEXTURE0);

                /* enable texturing for unit 0 */
                _myImageTexture.enable(gl);

                /* bind texture */
                _myImageTexture.bind(gl);

                /* now do the same for the second texture unit */
                gl.glActiveTexture(GL.GL_TEXTURE1);

                /* enable texturing for unit 1 */
                _myMaskTexture.enable(gl);

                /* bind texture */
                _myMaskTexture.bind(gl);
            } else {
                /*
                 * if multitexturing is not supported, we only
                 * use the first texture
                 */
                gl.glEnable(_myImageTexture.getTextureTarget());
                _myImageTexture.bind(gl);
            }

            /* draw multitextured plane */
            gl.glPushMatrix();
            gl.glScalef(_myImageTexture.getPixelWidth(),
                        _myImageTexture.getPixelHeight(),
                        1);
            _myCounter += 3.6f;
            gl.glRotatef(_myCounter, 0.2f, 0.5f, 0.3f);
            drawPlane(gl,
                      0,
                      _myImageTexture.nonpoweroftwotexturerescale().x,
                      0,
                      _myImageTexture.nonpoweroftwotexturerescale().y,
                      SHAPE_ORIGIN_CENTERED);
            gl.glPopMatrix();

            /* disable textures for all used texture units
             * it seems, that you have to disable texture units in
             * the reverse order of your asigning action.
             * thus, texture unit 1 is disabled before unit 0
             */
            if (_myUseMultiTextures) {
                gl.glActiveTexture(GL.GL_TEXTURE1);
                gl.glDisable(_myMaskTexture.getTextureTarget());
                gl.glActiveTexture(GL.GL_TEXTURE0);
                gl.glDisable(_myImageTexture.getTextureTarget());
            }
        }


        private void drawPlane(GL gl,
                               float theTextureWidthStart,
                               float theTextureWidthEnd,
                               float theTextureHeightStart,
                               float theTextureHeightEnd,
                               int theOrigin) {
            float x = 0;
            float y = 0;

            switch (theOrigin) {
                case SHAPE_ORIGIN_BOTTOM_LEFT:
                    break;
                case SHAPE_ORIGIN_BOTTOM_RIGHT:
                    x -= 1f;
                    break;
                case SHAPE_ORIGIN_TOP_LEFT:
                    y -= 1f;
                    break;
                case SHAPE_ORIGIN_TOP_RIGHT:
                    x -= 1f;
                    y -= 1f;
                    break;
                case SHAPE_ORIGIN_CENTERED:
                    x -= 1f / 2f;
                    y -= 1f / 2f;
                    break;
            }

            /* set texture coordinates for every unit seperately */
            if (_myUseMultiTextures) {
                gl.glBegin(GL.GL_QUADS);
                gl.glNormal3f(0, 0, 1);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE0, theTextureWidthStart, theTextureHeightEnd);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE1, theTextureWidthStart, theTextureHeightEnd);
                gl.glVertex3f(x, y, 0);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE0, theTextureWidthEnd, theTextureHeightEnd);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE1, theTextureWidthEnd, theTextureHeightEnd);
                gl.glVertex3f(x + 1f, y, 0);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE0, theTextureWidthEnd, theTextureHeightStart);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE1, theTextureWidthEnd, theTextureHeightStart);
                gl.glVertex3f(x + 1f, y + 1f, 0);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE0, theTextureWidthStart, theTextureHeightStart);
                gl.glMultiTexCoord2f(GL.GL_TEXTURE1, theTextureWidthStart, theTextureHeightStart);
                gl.glVertex3f(x, y + 1f, 0);
                gl.glEnd();
            } else {
                gl.glBegin(GL.GL_QUADS);
                gl.glNormal3f(0, 0, 1);
                gl.glTexCoord2f(theTextureWidthStart, theTextureHeightEnd);
                gl.glVertex3f(x, y, 0);
                gl.glTexCoord2f(theTextureWidthEnd, theTextureHeightEnd);
                gl.glVertex3f(x + 1f, y, 0);
                gl.glTexCoord2f(theTextureWidthEnd, theTextureHeightStart);
                gl.glVertex3f(x + 1f, y + 1f, 0);
                gl.glTexCoord2f(theTextureWidthStart, theTextureHeightStart);
                gl.glVertex3f(x, y + 1f, 0);
                gl.glEnd();
            }
        }
    }
}
