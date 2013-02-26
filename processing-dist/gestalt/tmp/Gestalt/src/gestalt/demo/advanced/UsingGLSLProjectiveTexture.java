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

import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.render.controller.Camera;
import gestalt.shape.Sphere;
import gestalt.material.TexturePlugin;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.Camera;
import gestalt.shape.AbstractShape;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.CameraMover;
import gestalt.util.JoglUtil;

import mathematik.Random;

import data.Resource;


public class UsingGLSLProjectiveTexture
    extends AnimatorRenderer {

    private ShaderManager _myShaderManager;

    private ShaderProgram _myProjectiveTextureShader;

    private Camera _myLightCamera;

    private boolean _myToggleCamera;

    private TexturePlugin _myProjectionTexture;

    public void setup() {
        displaycapabilities().backgroundcolor.set(0.9f);

        camera().position().set(200, 200, 500);
        camera().setMode(CAMERA_MODE_LOOK_AT);

        /* shader */
        _myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(_myShaderManager);

        _myProjectiveTextureShader = _myShaderManager.createShaderProgram();
        _myShaderManager.attachVertexShader(_myProjectiveTextureShader,
                                            Resource.getStream("demo/shader/shadow/projectivetexture.vs"));
        _myShaderManager.attachFragmentShader(_myProjectiveTextureShader,
                                              Resource.getStream("demo/shader/shadow/projectivetexture.fs"));

        /* create projector */
        _myProjectionTexture = new TexturePlugin(true);
        final ByteBitmap myBitmap = Bitmaps.getBitmap(Resource.getStream("demo/common/empire-state-human.png"));
        myBitmap.flipY();
        _myProjectionTexture.load(myBitmap);
        _myProjectionTexture.setWrapMode(TEXTURE_WRAPMODE_CLAMP);

        _myLightCamera = new Camera();
        _myLightCamera.set(camera());
        _myLightCamera.viewport().width = _myProjectionTexture.getPixelWidth();
        _myLightCamera.viewport().height = _myProjectionTexture.getPixelHeight();
        light().enable = true;
        light().setPositionRef(_myLightCamera.position());
        bin(BIN_FRAME_SETUP).add(_myLightCamera);

        /* create scene */
        Random r = new Random(0);
        for (int i = 0; i < 50; i++) {
            Sphere mySphere = new Sphere();
            mySphere.scale(50, 50, 50);
            mySphere.position().set(r.getFloat() * 500 - 250,
                                    r.getFloat() * 200,
                                    r.getFloat() * 500 - 250);
            mySphere.material().color4f().set(r.getFloat(0.35f, 0.65f));
            mySphere.material().lit = true;
            mySphere.material().addPlugin(new ProjectiveTextureMaterial(mySphere, _myProjectionTexture));
            bin(BIN_3D).add(mySphere);
        }
    }


    public void loop(final float theDeltaTime) {
        /* toggle camera()s */
        if (event().keyPressed) {
            if (event().key == 'c') {
                System.out.println("toggled camera().");
                _myToggleCamera = !_myToggleCamera;
            }
            if (event().key == ' ') {
                _myLightCamera.set(camera());
                _myLightCamera.viewport().width = _myProjectionTexture.getPixelWidth();
                _myLightCamera.viewport().height = _myProjectionTexture.getPixelHeight();
            }
        }

        final Camera myCamera;
        if (_myToggleCamera) {
            myCamera = _myLightCamera;
        } else {
            myCamera = camera();
        }

        /* move camera */
        CameraMover.handleKeyEvent(myCamera, event(), theDeltaTime);
    }


    private class ProjectiveTextureMaterial
        implements MaterialPlugin {

        private final AbstractShape _myParent;

        private final TexturePlugin _myProjectionTexture;

        public ProjectiveTextureMaterial(AbstractShape theParent, TexturePlugin theProjectionTexture) {
            _myParent = theParent;
            _myProjectionTexture = theProjectionTexture;
        }


        public void begin(GLContext theRenderContext, Material theParent) {
            /* touch texture */
            _myProjectionTexture.begin(theRenderContext, null);
            _myProjectionTexture.end(theRenderContext, null);

            /* enable shader */
            _myShaderManager.enable(_myProjectiveTextureShader);

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myProjectiveTextureShader, "projMap",
                                        JoglUtil.getTextureUnitID(_myProjectionTexture.getTextureUnit()));

            /* tex gen matrix */
            final GL gl = (  theRenderContext).gl;

            gl.glPushAttrib(GL.GL_ALL_ATTRIB_BITS);
            gl.glActiveTexture(GL.GL_TEXTURE1);
            gl.glMatrixMode(GL.GL_TEXTURE);
            gl.glLoadIdentity();

            /* T = bias * Pl * Vl * M */

            /* bias */
            gl.glTranslatef(0.5f, 0.5f, 0.5f);
            gl.glScalef(0.5f, 0.5f, 0.5f);

            /* Pl ( light projection matrix ) */
            gl.glMultMatrixf(_myLightCamera.projectionmatrix(), 0);

            /* Vl ( light view matrix ) */
            gl.glMultMatrixf(_myLightCamera.modelviewmatrix(), 0);

            /* M ( model matrix ) */
            JoglUtil.applyTransform(gl,
                                    _myParent.getTransformMode(),
                                    _myParent.transform(),
                                    _myParent.rotation(),
                                    _myParent.scale());

            gl.glPopAttrib();
        }


        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }


    public static void main(String[] args) {
        new UsingGLSLProjectiveTexture().init();
    }
}
