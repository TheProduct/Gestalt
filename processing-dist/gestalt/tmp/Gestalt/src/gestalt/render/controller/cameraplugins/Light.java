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


package gestalt.render.controller.cameraplugins;

import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.shape.AbstractDrawable;

import mathematik.Vector3f;

import javax.media.opengl.GL;


public class Light
        extends AbstractDrawable
        implements CameraPlugin {

    public static float[] global_ambient;

    public Color ambient;

    public Color diffuse;

    public Color specular;

    public Vector3f direction;

    public float intesity;

    public boolean enable;

    protected Vector3f position;

    protected float _myLightType;

    protected int _myLightID;

    private final float[] _myTempArray;

    public Light() {
        global_ambient = new float[] {0, 0, 0, 1};
        ambient = new Color(0, 0, 0, 0);
        diffuse = new Color(1, 1, 1, 1);
        specular = new Color(1, 1, 1, 1);
        position = new Vector3f(0, 0, 0);
        direction = new Vector3f(0, 0, -1);
        intesity = 1;
        enable = false;
        _myLightType = 0f; // directional for w = 0f OR positional for w > 0f
        _myLightID = GL.GL_LIGHT0;
        _myTempArray = new float[4];
    }

    public Vector3f position() {
        return position;
    }

    public void setPositionRef(Vector3f thePositionRef) {
        position = thePositionRef;
    }

    public void setType(float theType) {
        _myLightType = theType;
    }

    public int getID() {
        return _myLightID;
    }

    public String toString() {
        return "light" + "\n" + "position  : " + position + "\n" + "direction : " + direction + "\n" + "diffuse   : "
                + diffuse + "\n" + "ambient   : " + ambient + "\n" + "specular  : " + specular + "\n" + "intesity  : "
                + intesity + "\n" + "type      : " + _myLightType + "\n" + "id        : " + _myLightID + "\n";
    }

    public void begin(final GLContext theRenderContext) {
        draw(theRenderContext);
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        if (enable) {
            /** @todo JSR-231 performance hit! */
            gl.glLightModelfv(GL.GL_LIGHT_MODEL_AMBIENT, global_ambient, 0);
//            gl.glLightModelf(GL.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);
            gl.glEnable(_myLightID);
            gl.glLightfv(_myLightID, GL.GL_AMBIENT, ambient.toArray(), 0);
            gl.glLightfv(_myLightID, GL.GL_DIFFUSE, diffuse.toArray(), 0);
            gl.glLightfv(_myLightID, GL.GL_SPECULAR, specular.toArray(), 0);
            _myTempArray[0] = position.x;
            _myTempArray[1] = position.y;
            _myTempArray[2] = position.z;
            _myTempArray[3] = _myLightType;
            gl.glLightfv(_myLightID, GL.GL_POSITION, _myTempArray, 0);

            /* set materials to a default state */
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient.toArray(), 0);
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuse.toArray(), 0);
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, new float[] {0, 0, 0, 0}, 0);
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, new float[] {0, 0, 0, 0}, 0);
            gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, new float[] {0}, 0);
        } else {
            gl.glDisable(_myLightID);
        }
    }

    public void end(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        gl.glDisable(_myLightID);
    }
}

/*
 * GLLIGHTMODEL(3G) GLLIGHTMODEL(3G)
 *
 * NAME
 * glLightModelf, glLightModeli, glLightModelfv, glLightModeliv - set the
 * lighting model parameters
 *
 * C SPECIFICATION
 * void glLightModelf( GLenum pname,
 * GLfloat param )
 * void glLightModeli( GLenum pname,
 * GLint param )
 *
 * PARAMETERS
 * pname Specifies a single-valued lighting model parameter.
 * GL_LIGHT_MODEL_LOCAL_VIEWER, GL_LIGHT_MODEL_COLOR_CONTROL, and
 * GL_LIGHT_MODEL_TWO_SIDE are accepted.
 *
 * param Specifies the value that param will be set to.
 *
 * C SPECIFICATION
 * void glLightModelfv( GLenum pname,
 * const GLfloat *params )
 * void glLightModeliv( GLenum pname,
 * const GLint *params )
 *
 * PARAMETERS
 * pname Specifies a lighting model parameter. GL_LIGHT_MODEL_AMBIENT,
 * GL_LIGHT_MODEL_COLOR_CONTROL, GL_LIGHT_MODEL_LOCAL_VIEWER, and
 * GL_LIGHT_MODEL_TWO_SIDE are accepted.
 *
 * params Specifies a pointer to the value or values that params will be
 * set to.
 *
 * DESCRIPTION
 * glLightModel sets the lighting model parameter. pname names a parame-
 * ter and params gives the new value. There are three lighting model
 * parameters:
 *
 * GL_LIGHT_MODEL_AMBIENT
 * params contains four integer or floating-point values that
 * specify the ambient RGBA intensity of the entire scene.
 * Integer values are mapped linearly such that the most posi-
 * tive representable value maps to 1.0, and the most negative
 * representable value maps to -1.0. Floating-point values are
 * mapped directly. Neither integer nor floating-point values
 * are clamped. The initial ambient scene intensity is (0.2,
 * 0.2, 0.2, 1.0).
 *
 * GL_LIGHT_MODEL_COLOR_CONTROL
 * params must be either GL_SEPARATE_SPECULAR_COLOR or
 * GL_SINGLE_COLOR. GL_SINGLE_COLOR specifies that a single
 * color is generated from the lighting computation for a ver-
 * tex. GL_SEPARATE_SPECULAR_COLOR specifies that the specular
 * color computation of lighting be stored separately from the
 * remainder of the lighting computation. The specular color is
 * summed into the generated fragment's color after the applica-
 * tion of texture mapping (if enabled). The initial value is
 * GL_SINGLE_COLOR.
 *
 * GL_LIGHT_MODEL_LOCAL_VIEWER
 * params is a single integer or floating-point value that spec-
 * ifies how specular reflection angles are computed. If params
 * is 0 (or 0.0), specular reflection angles take the view
 * direction to be parallel to and in the direction of the -z
 * axis, regardless of the location of the vertex in eye coordi-
 * nates. Otherwise, specular reflections are computed from the
 * origin of the eye coordinate system. The initial value is 0.
 *
 * GL_LIGHT_MODEL_TWO_SIDE
 * params is a single integer or floating-point value that spec-
 * ifies whether one- or two-sided lighting calculations are
 * done for polygons. It has no effect on the lighting calcula-
 * tions for points, lines, or bitmaps. If params is 0 (or
 * 0.0), one-sided lighting is specified, and only the front
 * material parameters are used in the lighting equation. Oth-
 * erwise, two-sided lighting is specified. In this case, ver-
 * tices of back-facing polygons are lighted using the back
 * material parameters, and have their normals reversed before
 * the lighting equation is evaluated. Vertices of front-facing
 * polygons are always lighted using the front material parame-
 * ters, with no change to their normals. The initial value is
 * 0.
 *
 * In RGBA mode, the lighted color of a vertex is the sum of the material
 * emission intensity, the product of the material ambient reflectance and
 * the lighting model full-scene ambient intensity, and the contribution
 * of each enabled light source. Each light source contributes the sum of
 * three terms: ambient, diffuse, and specular. The ambient light source
 * contribution is the product of the material ambient reflectance and the
 * light's ambient intensity. The diffuse light source contribution is
 * the product of the material diffuse reflectance, the light's diffuse
 * intensity, and the dot product of the vertex's normal with the normal-
 * ized vector from the vertex to the light source. The specular light
 * source contribution is the product of the material specular
 * reflectance, the light's specular intensity, and the dot product of the
 * normalized vertex-to-eye and vertex-to-light vectors, raised to the
 * power of the shininess of the material. All three light source contri-
 * butions are attenuated equally based on the distance from the vertex to
 * the light source and on light source direction, spread exponent, and
 * spread cutoff angle. All dot products are replaced with 0 if they
 * evaluate to a negative value.
 *
 * The alpha component of the resulting lighted color is set to the alpha
 * value of the material diffuse reflectance.
 *
 * In color index mode, the value of the lighted index of a vertex ranges
 * from the ambient to the specular values passed to glMaterial using
 * GL_COLOR_INDEXES. Diffuse and specular coefficients, computed with a
 * (.30, .59, .11) weighting of the lights' colors, the shininess of the
 * material, and the same reflection and attenuation equations as in the
 * RGBA case, determine how much above ambient the resulting index is.
 *
 * NOTES
 * GL_LIGHT_MODEL_COLOR_CONTROL is available only if the GL version is 1.2
 * or greater.
 *
 */
/*
 * C SPECIFICATION
 * void glLightf( GLenum light,
 * GLenum pname,
 * GLfloat param )
 * void glLighti( GLenum light,
 * GLenum pname,
 * GLint param )
 *
 * PARAMETERS
 * light Specifies a light. The number of lights depends on the imple-
 * mentation, but at least eight lights are supported. They are
 * identified by symbolic names of the form GL_LIGHTi where 0 <= i
 * < GL_MAX_LIGHTS.
 *
 * pname Specifies a single-valued light source parameter for light.
 * GL_SPOT_EXPONENT, GL_SPOT_CUTOFF, GL_CONSTANT_ATTENUATION,
 * GL_LINEAR_ATTENUATION, and GL_QUADRATIC_ATTENUATION are
 * accepted.
 *
 * param Specifies the value that parameter pname of light source light
 * will be set to.
 *
 * C SPECIFICATION
 * void glLightfv( GLenum light,
 * GLenum pname,
 * const GLfloat *params )
 * void glLightiv( GLenum light,
 * GLenum pname,
 * const GLint *params )
 *
 * PARAMETERS
 * light Specifies a light. The number of lights depends on the imple-
 * mentation, but at least eight lights are supported. They are
 * identified by symbolic names of the form GL_LIGHTi where 0 <= i
 * < GL_MAX_LIGHTS.
 *
 * pname Specifies a light source parameter for light. GL_AMBIENT,
 * GL_DIFFUSE, GL_SPECULAR, GL_POSITION, GL_SPOT_CUTOFF,
 * GL_SPOT_DIRECTION, GL_SPOT_EXPONENT, GL_CONSTANT_ATTENUATION,
 * GL_LINEAR_ATTENUATION, and GL_QUADRATIC_ATTENUATION are
 * accepted.
 *
 * params Specifies a pointer to the value or values that parameter pname
 * of light source light will be set to.
 *
 * DESCRIPTION
 * glLight sets the values of individual light source parameters. light
 * names the light and is a symbolic name of the form GL_LIGHTi, where 0
 * <= i < GL_MAX_LIGHTS. pname specifies one of ten light source parame-
 * ters, again by symbolic name. params is either a single value or a
 * pointer to an array that contains the new values.
 *
 * To enable and disable lighting calculation, call glEnable and glDisable
 * with argument GL_LIGHTING. Lighting is initially disabled. When it is
 * enabled, light sources that are enabled contribute to the lighting cal-
 * culation. Light source i is enabled and disabled using glEnable and
 * glDisable with argument GL_LIGHTi.
 *
 * The ten light parameters are as follows:
 *
 * GL_AMBIENT params contains four integer or floating-point val-
 * ues that specify the ambient RGBA intensity of the
 * light. Integer values are mapped linearly such
 * that the most positive representable value maps to
 * 1.0, and the most negative representable value maps
 * to -1.0. Floating-point values are mapped
 * directly. Neither integer nor floating-point val-
 * ues are clamped. The initial ambient light inten-
 * sity is (0, 0, 0, 1).
 *
 * GL_DIFFUSE params contains four integer or floating-point val-
 * ues that specify the diffuse RGBA intensity of the
 * light. Integer values are mapped linearly such
 * that the most positive representable value maps to
 * 1.0, and the most negative representable value maps
 * to -1.0. Floating-point values are mapped
 * directly. Neither integer nor floating-point val-
 * ues are clamped. The initial value for GL_LIGHT0
 * is (1, 1, 1, 1); for other lights, the initial
 * value is (0, 0, 0, 0).
 *
 * GL_SPECULAR params contains four integer or floating-point val-
 * ues that specify the specular RGBA intensity of the
 * light. Integer values are mapped linearly such
 * that the most positive representable value maps to
 * 1.0, and the most negative representable value maps
 * to -1.0. Floating-point values are mapped
 * directly. Neither integer nor floating-point val-
 * ues are clamped. The initial value for GL_LIGHT0
 * is (1, 1, 1, 1); for other lights, the initial
 * value is (0, 0, 0, 0).
 *
 * GL_POSITION params contains four integer or floating-point val-
 * ues that specify the position of the light in homo-
 * geneous object coordinates. Both integer and
 * floating-point values are mapped directly. Neither
 * integer nor floating-point values are clamped.
 *
 * The position is transformed by the modelview matrix
 * when glLight is called (just as if it were a
 * point), and it is stored in eye coordinates. If
 * the w component of the position is 0, the light is
 * treated as a directional source. Diffuse and spec-
 * ular lighting calculations take the light's direc-
 * tion, but not its actual position, into account,
 * and attenuation is disabled. Otherwise, diffuse
 * and specular lighting calculations are based on the
 * actual location of the light in eye coordinates,
 * and attenuation is enabled. The initial position
 * is (0, 0, 1, 0); thus, the initial light source is
 * directional, parallel to, and in the direction of
 * the -z axis.
 *
 * GL_SPOT_DIRECTION params contains three integer or floating-point
 * values that specify the direction of the light in
 * homogeneous object coordinates. Both integer and
 * floating-point values are mapped directly. Neither
 * integer nor floating-point values are clamped.
 *
 * The spot direction is transformed by the inverse of
 * the modelview matrix when glLight is called (just
 * as if it were a normal), and it is stored in eye
 * coordinates. It is significant only when
 * GL_SPOT_CUTOFF is not 180, which it is initially.
 * The initial direction is (0, 0, -1).
 *
 * GL_SPOT_EXPONENT params is a single integer or floating-point value
 * that specifies the intensity distribution of the
 * light. Integer and floating-point values are
 * mapped directly. Only values in the range [0,128]
 * are accepted.
 *
 * Effective light intensity is attenuated by the
 * cosine of the angle between the direction of the
 * light and the direction from the light to the ver-
 * tex being lighted, raised to the power of the spot
 * exponent. Thus, higher spot exponents result in a
 * more focused light source, regardless of the spot
 * cutoff angle (see GL_SPOT_CUTOFF, next paragraph).
 * The initial spot exponent is 0, resulting in uni-
 * form light distribution.
 *
 * GL_SPOT_CUTOFF params is a single integer or floating-point value
 * that specifies the maximum spread angle of a light
 * source. Integer and floating-point values are
 * mapped directly. Only values in the range [0,90]
 * and the special value 180 are accepted. If the
 * angle between the direction of the light and the
 * direction from the light to the vertex being
 * lighted is greater than the spot cutoff angle, the
 * light is completely masked. Otherwise, its inten-
 * sity is controlled by the spot exponent and the
 * attenuation factors. The initial spot cutoff is
 * 180, resulting in uniform light distribution.
 *
 * GL_CONSTANT_ATTENUATION
 *
 * GL_LINEAR_ATTENUATION
 *
 * GL_QUADRATIC_ATTENUATION
 * params is a single integer or floating-point value
 * that specifies one of the three light attenuation
 * factors. Integer and floating-point values are
 * mapped directly. Only nonnegative values are
 * accepted. If the light is positional, rather than
 * directional, its intensity is attenuated by the
 * reciprocal of the sum of the constant factor, the
 * linear factor times the distance between the light
 * and the vertex being lighted, and the quadratic
 * factor times the square of the same distance. The
 * initial attenuation factors are (1, 0, 0), result-
 * ing in no attenuation.
 *
 * NOTES
 * It is always the case that GL_LIGHTi = GL_LIGHT0 + i.
 *
 */
