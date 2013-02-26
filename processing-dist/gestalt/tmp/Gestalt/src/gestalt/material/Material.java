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


package gestalt.material;

import gestalt.context.GLContext;
import gestalt.material.texture.Bitmap;

import java.io.Serializable;
import java.util.Vector;

import javax.media.opengl.GL;

import static gestalt.Gestalt.*;


public class Material
        implements Serializable {

    private static final long serialVersionUID = -1907066044097932895L;

    public static final int NORMALIZE_MODE = MATERIAL_NORMAL_NORMALIZE;

    public static boolean VERBOSE = true;

    public boolean enabled;

    private Color mColor;

    /** @todo maybe move this to texture. */
    public boolean wireframe;

    public int blendmode;

    public boolean depthtest;

    public boolean depthmask;

    public boolean colormasking;

    public boolean RED = true;

    public boolean GREEN = true;

    public boolean BLUE = true;

    public boolean ALPHA = true;

    public boolean transparent;

    public boolean smoothshading;

    public boolean normalizenormals;

    public boolean disableTextureCoordinates;

    public Color ambient;

    public Color diffuse;

    public Color specular;

    public Color emission;

    public float shininess;

    public boolean lit;

    public boolean ignoreplugins = false;

    protected int _mySourceBlendFunction;

    protected int _myDestinationBlendFunction;

    protected Vector<MaterialPlugin> _myPlugins;

    private GL gl;

    private static final int _myFace = GL.GL_FRONT;

    public Material() {
        enabled = true;
        mColor = new Color();
        wireframe = false;
        depthtest = true;
        depthmask = true;
        colormasking = false;
        transparent = true;
        smoothshading = true;
        normalizenormals = true;
        disableTextureCoordinates = false;
        blendmode = MATERIAL_BLEND_ALPHA;

        /* lighting */
        lit = false;
        /*
         * materials must be initialized on the client side as long as 'diffuse'
         * is null material attempts to use the vertex color4f as the diffuse
         * property, the state of the other properties is undefined.
         *
         * default opengl values
         *
         * ambient = new Color(0.2f, 0.2f, 0.2f, 1.0f); diffuse = new
         * Color(0.8f, 0.8f, 0.8f, 1.0f); specular = new Color(0.0f, 0.0f, 0.0f,
         * 1.0f); emission = new Color(0.0f, 0.0f, 0.0f, 1.0f);
         *
         */

        /* values smaller than 0 ommit setting the shiniess. */
        shininess = -1;

        /* plugins */
        _myPlugins = new Vector<MaterialPlugin>(10);
    }


    /* accessing fields */
    public Vector<MaterialPlugin> plugins() {
        return _myPlugins;
    }

    public Color getColor() {
        return color4f();
    }


    /* texture */
    /**
     * search for the texture plugin in the plugins container.<br/> returns
     * 'null' if no texture plugin is available.<br/> it is adviced to get the
     * texture once and than cache the reference, as each call to this method
     * invokes a search through the plugin container.<br/>
     *
     * <pre>
     * TexturePlugin myTexture = myShape.material.texture();
     * myTexture.load(...
     * </pre>
     *
     * @return TexturePlugin
     */
    public TexturePlugin texture() {
        TexturePlugin myTexturePlugin = null;
        for (int i = 0; i < _myPlugins.size(); i++) {
            if (_myPlugins.get(i) instanceof TexturePlugin) {
                return (TexturePlugin)_myPlugins.get(i);
            }
        }
        return myTexturePlugin;
    }

    /**
     * this method does essentially the same as 'addPlugin'. it is just there
     * for semantic reasons.
     *
     * @param theTexture
     * TexturePlugin
     */
    public void addTexture(TexturePlugin theTexture) {
        _myPlugins.add(theTexture);
    }

    /**
     * this method does essentially the same as 'removePlugin'. it is just there
     * for semantic reasons.
     */
    public boolean removeTexture() {
        final TexturePlugin myTexture = texture();
        if (myTexture != null) {
            return removePlugin(myTexture);
        }
        return false;
    }

    /**
     *
     * @param theTexture TexturePlugin
     */
    public void replaceTexture(TexturePlugin theTexture) {
        final TexturePlugin myTexture = texture();
        if (myTexture != null) {
            replacePlugin(myTexture, theTexture);
//            removeTexture();
//            addTexture(theTexture);
        }
    }

    /**
     *
     * @param theBitmap
     * Bitmap
     * @return TexturePlugin
     */
    public TexturePlugin createTexture(Bitmap theBitmap) {
        TexturePlugin myTexture = new TexturePlugin(true);
        myTexture.load(theBitmap);
        addPlugin(myTexture);
        return myTexture;
    }

    /**
     *
     * @return TexturePlugin
     */
    public TexturePlugin addTexture() {
        TexturePlugin myTexture = new TexturePlugin(true);
        _myPlugins.add(myTexture);
        return myTexture;
    }

    /**
     * used in conjunction with blendmode 'MATERIAL_BLEND_CUSTOM'
     *
     * @param theSource
     * int
     * @param theDestination
     * int
     */
    public void setCustomBlendFunction(int theSource, int theDestination) {
        _mySourceBlendFunction = theSource;
        _myDestinationBlendFunction = theDestination;
    }


    /* plugins */
    public void addPlugin(MaterialPlugin thePlugin) {
        _myPlugins.add(thePlugin);
    }

    public boolean replacePlugin(MaterialPlugin theOldPlugin, MaterialPlugin theNewPlugin) {
        for (int i = 0; i < _myPlugins.size(); i++) {
            if (_myPlugins.get(i).equals(theOldPlugin)) {
                _myPlugins.add(i, theNewPlugin);
                _myPlugins.remove(i + 1);
                return true;
            }
        }
        return false;
    }

    public boolean removePlugin(MaterialPlugin thePlugin) {
        return _myPlugins.remove(thePlugin);
    }

    public void begin(final GLContext theRenderContext) {
        if (enabled) {
            gl = theRenderContext.gl;

            beginWireframe();
            beginColor();
            beginBlendmode();
            beginDepthtesting();
            beginDepthmasking();
            beginColormasking();
            beginLight();
            beginShadingmodel();

            /* plugins */
            if (!ignoreplugins) {
                beginPlugins(theRenderContext);
            }
        }
    }

    public void end(final GLContext theRenderContext) {
        if (enabled) {

            endWireframe();
            endColormasking();
            endMaterial();

            /* plugins */
            if (!ignoreplugins) {
                endPlugins(theRenderContext);
            }

            gl = null;
        }
    }

    private void beginPlugins(final GLContext theRenderContext) {
        for (int i = 0; i < _myPlugins.size(); i++) {
            if (_myPlugins.get(i) != null) {
                _myPlugins.get(i).begin(theRenderContext, this);
            }
        }
    }

    private void endPlugins(final GLContext theRenderContext) {
        for (int i = _myPlugins.size() - 1; i >= 0; i--) {
            if (_myPlugins.get(i) != null) {
                _myPlugins.get(i).end(theRenderContext, this);
            }
        }
    }

    private void beginColormasking() {
        if (colormasking) {
            gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
            gl.glColorMask(RED, GREEN, BLUE, ALPHA);
        }
    }

    private void endColormasking() {
        if (colormasking) {
            gl.glPopAttrib();
        }
    }

    private void beginShadingmodel() {
        if (smoothshading) {
            gl.glShadeModel(GL.GL_SMOOTH);
        } else {
            gl.glShadeModel(GL.GL_FLAT);
        }
    }

    private void beginWireframe() {
        if (wireframe) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
        }
    }

    private float[] _myTempShininess = new float[1];

    private void material() {
        /* normals */
        if (normalizenormals) {
            if (NORMALIZE_MODE == MATERIAL_NORMAL_NORMALIZE) {
                gl.glEnable(GL.GL_NORMALIZE);
            } else {
                gl.glEnable(GL.GL_RESCALE_NORMAL);
            }
        } else {
            gl.glDisable(GL.GL_NORMALIZE);
            gl.glDisable(GL.GL_RESCALE_NORMAL);
        }
        /* material */

        /** @todo we just use GL_FRONT not GL_BACK nor GL_FRONT_AND_BACK. */
        if (ambient != null) {
            /** @todo JSR-231 performance hit! */
            gl.glMaterialfv(_myFace, GL.GL_AMBIENT, ambient.toArray(), 0);
        }

        if (diffuse != null) {
            /** @todo JSR-231 performance hit! */
            gl.glMaterialfv(_myFace, GL.GL_DIFFUSE, diffuse.toArray(), 0);
        } else {
            gl.glEnable(GL.GL_COLOR_MATERIAL);
            gl.glColorMaterial(_myFace, GL.GL_DIFFUSE);
        }

        if (specular != null) {
            /** @todo JSR-231 performance hit! */
            gl.glMaterialfv(_myFace, GL.GL_SPECULAR, specular.toArray(), 0);
        }

        if (emission != null) {
            /** @todo JSR-231 performance hit! */
            gl.glMaterialfv(_myFace, GL.GL_EMISSION, emission.toArray(), 0);
        }

        if (shininess >= 0) {
            /** @todo JSR-231 performance hit! */
            _myTempShininess[0] = shininess;
            gl.glMaterialfv(_myFace, GL.GL_SHININESS, _myTempShininess, 0);
        }
    }

    private void endMaterial() {
        if (diffuse == null) {
            gl.glEnable(GL.GL_COLOR_MATERIAL);
        }
    }


    /*
     * GL_AMBIENT params contains four integer or floating-point val- ues that
     * specify the ambient RGBA reflectance of the material. Integer values are
     * mapped linearly such that the most positive representable value maps to
     * 1.0, and the most negative representable value maps to -1.0.
     * Floating-point values are mapped directly. Neither integer nor floating-
     * point values are clamped. The initial ambient reflectance for both front-
     * and back-facing materi- als is (0.2, 0.2, 0.2, 1.0).
     *
     * GL_DIFFUSE params contains four integer or floating-point val- ues that
     * specify the diffuse RGBA reflectance of the material. Integer values are
     * mapped linearly such that the most positive representable value maps to
     * 1.0, and the most negative representable value maps to -1.0.
     * Floating-point values are mapped directly. Neither integer nor floating-
     * point values are clamped. The initial diffuse reflectance for both front-
     * and back-facing materi- als is (0.8, 0.8, 0.8, 1.0).
     *
     * GL_SPECULAR params contains four integer or floating-point val- ues that
     * specify the specular RGBA reflectance of the material. Integer values are
     * mapped linearly such that the most positive representable value maps to
     * 1.0, and the most negative representable value maps to -1.0.
     * Floating-point values are mapped directly. Neither integer nor floating-
     * point values are clamped. The initial specular reflectance for both
     * front- and back-facing materi- als is (0, 0, 0, 1).
     *
     * GL_EMISSION params contains four integer or floating-point val- ues that
     * specify the RGBA emitted light intensity of the material. Integer values
     * are mapped lin- early such that the most positive representable value
     * maps to 1.0, and the most negative repre- sentable value maps to -1.0.
     * Floating-point values are mapped directly. Neither integer nor floating-
     * point values are clamped. The initial emission intensity for both front-
     * and back-facing materials is (0, 0, 0, 1).
     *
     * GL_SHININESS params is a single integer or floating-point value that
     * specifies the RGBA specular exponent of the material. Integer and
     * floating-point values are mapped directly. Only values in the range
     * [0,128] are accepted. The initial specular exponent for both front- and
     * back-facing materials is 0.
     *
     */

    /*
     * NAME glColorMaterial - cause a material color4f to track the current color4f
     *
     * C SPECIFICATION void glColorMaterial( GLenum face, GLenum mode )
     *
     * PARAMETERS face Specifies whether front, back, or both front and back
     * material parameters should track the current color4f. Accepted values are
     * GL_FRONT, GL_BACK, and GL_FRONT_AND_BACK. The initial value is
     * GL_FRONT_AND_BACK.
     *
     * mode Specifies which of several material parameters track the current
     * color4f. Accepted values are GL_EMISSION, GL_AMBIENT, GL_DIFFUSE,
     * GL_SPECULAR, and GL_AMBIENT_AND_DIFFUSE. The initial value is
     * GL_AMBIENT_AND_DIFFUSE.
     *
     * DESCRIPTION glColorMaterial specifies which material parameters track the
     * current color4f. When GL_COLOR_MATERIAL is enabled, the material parameter
     * or parameters specified by mode, of the material or materials specified
     * by face, track the current color4f at all times.
     *
     * To enable and disable GL_COLOR_MATERIAL, call glEnable and glDisable with
     * argument GL_COLOR_MATERIAL. GL_COLOR_MATERIAL is initially dis- abled.
     *
     * NOTES glColorMaterial makes it possible to change a subset of material
     * param- eters for each vertex using only the glColor command, without
     * calling glMaterial. If only such a subset of parameters is to be
     * specified for each vertex, calling glColorMaterial is preferable to
     * calling glMaterial.
     *
     * Call glColorMaterial before enabling GL_COLOR_MATERIAL.
     *
     * Calling glDrawElements, glDrawArrays, or glDrawRangeElements may leave
     * the current color4f indeterminate, if the color4f array is enabled. If
     * glColorMaterial is enabled while the current color4f is indeterminate, the
     * lighting material state specified by face and mode is also indeter-
     * minate.
     *
     * If the GL version is 1.1 or greater, and GL_COLOR_MATERIAL is enabled,
     * evaluated color4f values affect the results of the lighting equation as if
     * the current color4f were being modified, but no change is made to the
     * tracking lighting parameter of the current color4f.
     *
     */
    private void beginDepthtesting() {
        if (depthtest) {
            gl.glEnable(GL.GL_DEPTH_TEST);
        } else {
            gl.glDisable(GL.GL_DEPTH_TEST);
        }
    }

    private void beginDepthmasking() {
        gl.glDepthMask(depthmask);
    }

    private void beginColor() {
        gl.glColor4f(color4f().r, color4f().g, color4f().b, color4f().a);
    }

    private void beginLight() {
        if (lit) {
            gl.glEnable(GL.GL_LIGHTING);
            material();
        } else {
            gl.glDisable(GL.GL_LIGHTING);
        }
    }

    private void beginBlendmode() {
        if (transparent) {
            /* select blendmode */
            switch (blendmode) {
                case UNDEFINED:
                    break;
                case MATERIAL_BLEND_CUSTOM:
                    gl.glBlendFunc(_mySourceBlendFunction, _myDestinationBlendFunction);
                    break;
                case MATERIAL_BLEND_ALPHA:
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
                    break;
                case MATERIAL_BLEND_INVERS_MULTIPLY:
                    gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
                    break;
                case MATERIAL_BLEND_MULTIPLY:
                    gl.glBlendFunc(GL.GL_ZERO, GL.GL_SRC_COLOR);
                    break;
                case MATERIAL_BLEND_WHITE_INVERT:
                    gl.glBlendFunc(GL.GL_ONE_MINUS_DST_COLOR, GL.GL_ONE_MINUS_SRC_COLOR);
                    break;
                case MATERIAL_BLEND_BRIGHTER:
                    gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_ONE);
                    break;
            }
            /* turn on blending */
            gl.glEnable(GL.GL_BLEND);
        } else {
            gl.glDisable(GL.GL_BLEND);
        }
    }

    private void endWireframe() {
        if (wireframe) {
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        }
    }

    public Color color4f() {
        return mColor;
    }
}
