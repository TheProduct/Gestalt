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


package gestalt.extension.glur;

import gestalt.extension.materialplugin.JoglMaterialPluginGLSLGaussianBlur;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.render.bin.Bin;
import gestalt.render.bin.TwoSidedBin;
import gestalt.render.controller.Camera;
import gestalt.shape.DrawableFactory;
import gestalt.shape.Plane;

import mathematik.Vector2i;

import java.io.InputStream;

import static gestalt.Gestalt.*;


public class OffscreenBlurContext {

    private final OffscreenContext _myContext;

    private JoglMaterialPluginGLSLGaussianBlur _myBlur;

    private Plane _myShapeDisplay;

    private Plane _myBlurDisplay;

    private Plane myBlurPlane1;

    private Plane myBlurPlane2;

    public OffscreenBlurContext(DrawableFactory theFactory,
                                Bin theFboBin,
                                Bin theDisplayBin,
                                Bin theShaderBin,
                                Vector2i theShapeContextDimensions,
                                Vector2i theBlurTextureDimensions,
                                final InputStream theVertexShader,
                                final InputStream theFragmentShader) {
        _myBlur = createBlurShader(theShaderBin,
                                   theFactory,
                                   theBlurTextureDimensions,
                                   theVertexShader,
                                   theFragmentShader);
        _myContext = createOffscreenContext(theFactory,
                                            theFboBin,
                                            theDisplayBin,
                                            theShapeContextDimensions,
                                            theBlurTextureDimensions);
    }

    public OffscreenContext context() {
        return _myContext;
    }

    public Camera camera() {
        return _myContext.fbo().camera();
    }

    public JoglMaterialPluginGLSLGaussianBlur blur() {
        return _myBlur;
    }

    public TwoSidedBin bin() {
        return _myContext.bin();
    }

    public Plane blurdisplay() {
        return _myBlurDisplay;
    }

    public Plane shapedisplay() {
        return _myShapeDisplay;
    }

    private JoglMaterialPluginGLSLGaussianBlur createBlurShader(final Bin theShaderBin,
                                                                final DrawableFactory theFactory,
                                                                final Vector2i theBlurTextureDimensions,
                                                                final InputStream theVertexShader,
                                                                final InputStream theFragmentShader) {
        /* create blur shader */
        ShaderManager myShaderManager = theFactory.extensions().shadermanager();
        theShaderBin.add(myShaderManager);
        ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
        JoglMaterialPluginGLSLGaussianBlur myBlur = new JoglMaterialPluginGLSLGaussianBlur(myShaderManager,
                                                                                           myShaderProgram,
                                                                                           theVertexShader,
                                                                                           theFragmentShader,
                                                                                           theBlurTextureDimensions);

        return myBlur;
    }

    public Plane blurplane1() {
        return myBlurPlane1;
    }

    public Plane blurplane2() {
        return myBlurPlane2;
    }

    private OffscreenContext createOffscreenContext(DrawableFactory theFactory,
                                                    Bin theFboBin,
                                                    Bin theDisplayBin,
                                                    Vector2i theShapeContextDimensions,
                                                    Vector2i theBlurContextDimensions) {
        /* create shape fbo */
        OffscreenContext myShapeOffscreen = new OffscreenContext(theFactory, theShapeContextDimensions);
        theFboBin.add(myShapeOffscreen.fbo());

        /* prevent shader from reading beyond the texture borders */
        myShapeOffscreen.fbo().setWrapMode(TEXTURE_WRAPMODE_CLAMP);

        /* create blur fbo */
        OffscreenContext myBlurOffscreenPass1 = new OffscreenContext(theFactory, theBlurContextDimensions);
        myBlurOffscreenPass1.fbo().setWrapMode(TEXTURE_WRAPMODE_CLAMP);
        theFboBin.add(myBlurOffscreenPass1.fbo());

        /* create blur fbo */
        OffscreenContext myBlurOffscreenPass2 = new OffscreenContext(theFactory, theBlurContextDimensions);
        myBlurOffscreenPass2.fbo().setWrapMode(TEXTURE_WRAPMODE_CLAMP);
        theFboBin.add(myBlurOffscreenPass2.fbo());

        /* add plane to display shape fbo in blur1 fbo */
        myBlurPlane1 = myShapeOffscreen.createDisplay(theFactory);
        myBlurPlane1.scale().set(theBlurContextDimensions);
        myBlurPlane1.origin(SHAPE_ORIGIN_CENTERED);
        myBlurPlane1.material().addPlugin(_myBlur);
        myBlurOffscreenPass1.bin().add(myBlurPlane1);

        /* add plane to display blur1 fbo in blur2 fbo */
        myBlurPlane2 = myBlurOffscreenPass1.createDisplay(theFactory);
        myBlurPlane2.scale().set(theBlurContextDimensions);
        myBlurPlane2.origin(SHAPE_ORIGIN_CENTERED);
        myBlurPlane2.material().addPlugin(_myBlur);
        myBlurOffscreenPass2.bin().add(myBlurPlane2);

        /* display shape fbo in framebuffer */
        _myShapeDisplay = myShapeOffscreen.createDisplay(theFactory);
        _myShapeDisplay.scale().set(theShapeContextDimensions);
        _myShapeDisplay.origin(SHAPE_ORIGIN_CENTERED);
        _myShapeDisplay.material().blendmode = MATERIAL_BLEND_ALPHA;
        theDisplayBin.add(_myShapeDisplay);

        /* display blur fbo in framebuffer */
        _myBlurDisplay = myBlurOffscreenPass2.createDisplay(theFactory);
        _myBlurDisplay.scale().set(theShapeContextDimensions);
        _myBlurDisplay.origin(SHAPE_ORIGIN_CENTERED);
        _myBlurDisplay.material().blendmode = MATERIAL_BLEND_INVERS_MULTIPLY;
        theDisplayBin.add(_myBlurDisplay);

        return myShapeOffscreen;
    }
}
