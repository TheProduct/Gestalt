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


package gestalt.extension.glsl;

import gestalt.render.controller.cameraplugins.ScreenWorldCoordinates;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.render.AnimatorRenderer;
import gestalt.render.Drawable;
import gestalt.shape.Cuboid;

import mathematik.Plane3f;
import mathematik.Vector2f;
import mathematik.Vector3f;
import mathematik.Vector4f;

import data.Resource;

import javax.media.opengl.GL;


public class TestBumpMappingGLSL
        extends AnimatorRenderer {

    private AttributeDrawable _myCustomShape;

    private float isBumbing = 1;

    private Cuboid _myLightView;

    private ScreenWorldCoordinates _myScreenWorldCoordinates;

    public void setup() {
        cameramover(true);
        camera().setMode(CAMERA_MODE_LOOK_AT);
        camera().position().set(315.4115, 191.87274, 330.59604);

        _myScreenWorldCoordinates = new ScreenWorldCoordinates();
        camera().plugins().add(_myScreenWorldCoordinates);

        light().enable = true;
        light().position().set(camera().position());

        /* glsl shader */
        ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
        bin(BIN_FRAME_SETUP).add(myShaderManager);

        ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
        myShaderManager.attachVertexShader(myShaderProgram, Resource.getStream("demo/shader/normalmap.vsh"));
        myShaderManager.attachFragmentShader(myShaderProgram, Resource.getStream("demo/shader/normalmap.fsh"));

        NormalMapMaterial _myShaderMaterial = new NormalMapMaterial(myShaderManager, myShaderProgram);

        /* create a color4f map */
        TexturePlugin myNormalMap = drawablefactory().texture();
        myNormalMap.setFilterType(TEXTURE_FILTERTYPE_MIPMAP);
        myNormalMap.load(Bitmaps.getBitmap(Resource.getStream("demo/common/normal_map3.png")));
        myNormalMap.setTextureUnit(GL.GL_TEXTURE1);

        /* create a normal map */
        TexturePlugin myColorMap = drawablefactory().texture();
        myColorMap.setFilterType(TEXTURE_FILTERTYPE_MIPMAP);
        myColorMap.load(Bitmaps.getBitmap(Resource.getStream("demo/common/color_map3.png")));
        myColorMap.setTextureUnit(GL.GL_TEXTURE0);

        /* create custom shape and add it to the renderer */
        _myCustomShape = new AttributeDrawable(myShaderProgram);
        _myCustomShape.material().lit = true;
        _myCustomShape.material().shininess = 1.f;
        _myCustomShape.material().specular = new Color(1f, 1.0f, 1.0f, 1f);
        _myCustomShape.material().ambient = new Color(0.1f, 0.1f, 0.1f, 1f);
        _myCustomShape.material().diffuse = new Color(0.8f, 0.8f, 0.8f, 1f);
        _myCustomShape.material().addPlugin(myNormalMap);
        _myCustomShape.material().addPlugin(myColorMap);
        _myCustomShape.material().addPlugin(_myShaderMaterial);

        bin(BIN_3D).add(_myCustomShape);
        _myCustomShape.scale.set(400, 400, 400);
        _myCustomShape.position.set(0, -_myCustomShape.scale.y / 2f, 0);
        _myLightView = drawablefactory().cuboid();
        _myLightView.scale().set(5, 50, 5);
        _myLightView.origin(SHAPE_ORIGIN_TOP_CENTERED);
        _myLightView.setPositionRef(light().position());
        bin(BIN_3D).add(_myLightView);
    }

    public void loop(final float theDeltaTime) {
        Plane3f myPlane = new Plane3f();
        myPlane.origin.set(_myLightView.position());
        myPlane.vectorA.set(1, 0, 0);
        myPlane.vectorB.set(0, 0, 1);
        myPlane.normal = new Vector3f(0, 1, 0);
        Vector3f myResult = new Vector3f();
        boolean mySuccess = _myScreenWorldCoordinates.screenToWorldPosition(event().mouseX, event().mouseY, myPlane, myResult);

        if (mySuccess) {
            light().position().set(myResult.x, 50, myResult.z);
        }

        if (event().keyPressed) {
            if (event().key == 'B') {
                isBumbing = 1;
            }
            if (event().key == 'b') {
                isBumbing = 0;
            }
            if (event().key == '+') {
                _myCustomShape.material().shininess += 0.1f;
                _myCustomShape.material().shininess = Math.min(_myCustomShape.material().shininess, 1);
            }
            if (event().key == '-') {
                _myCustomShape.material().shininess -= 0.1f;
                _myCustomShape.material().shininess = Math.max(_myCustomShape.material().shininess, 0);
            }
        }
    }

    public class NormalMapMaterial
            implements MaterialPlugin {

        private ShaderManager _myShaderManager;

        private ShaderProgram _myShaderProgram;

        public NormalMapMaterial(ShaderManager theShaderManager,
                                 ShaderProgram theShaderProgram) {
            _myShaderManager = theShaderManager;
            _myShaderProgram = theShaderProgram;
        }

        public void begin(GLContext theRenderContext, Material theParent) {
            /* enable shader */
            _myShaderManager.enable(_myShaderProgram);

            /* set uniform variables in shader */
            _myShaderManager.setUniform(_myShaderProgram, "normalMap", 1);
            _myShaderManager.setUniform(_myShaderProgram, "colorMap", 0);
            _myShaderManager.setUniform(_myShaderProgram, "invRadius", 0.0f);
            _myShaderManager.setUniform(_myShaderProgram, "bumping", isBumbing);
            _myShaderManager.setUniform(_myShaderProgram, "LightPosition", light().position());

        }

        public void end(GLContext theRenderContext, Material theParent) {
            _myShaderManager.disable();
        }
    }

    public class AttributeDrawable
            implements Drawable {

        public Vector3f position;

        public Vector3f rotation;

        public Vector3f scale;

        private float _mySortValue;

        private Material _myMaterial;

        private ShaderProgram _myShaderProgram;

        public AttributeDrawable(ShaderProgram theShaderProgram) {
            position = new Vector3f();
            rotation = new Vector3f();
            scale = new Vector3f(1, 1, 1);
            _myMaterial = new Material();
            _myShaderProgram = theShaderProgram;
        }

        public void draw(GLContext theRenderContext) {
            final GL gl = (theRenderContext).gl;

            _myMaterial.begin(theRenderContext);
            int myColorAttributeLoc = gl.glGetAttribLocationARB(_myShaderProgram.getOpenGLID(), "Tangent");

            gl.glPushMatrix();

            gl.glTranslatef(position.x, position.y, position.z);

            gl.glRotatef(rotation.x, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(rotation.y, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(rotation.z, 0.0f, 0.0f, 1.0f);

            gl.glScalef(scale.x, scale.y, scale.z);

            gl.glBegin(GL.GL_QUADS);

//            // Front Face
//            Vector3f myNormal = new Vector3f();
//            mathematik.Util.calculateNormal(new Vector3f( -0.5f, -0.5f, 0.5f),
//                                            new Vector3f(0.5f, -0.5f, 0.5f),
//                                            new Vector3f(-0.5f, 0.5f, 0.5f),
//                                            myNormal);
//            Vector4f myTangent = new Vector4f();
//            calculateTangentVector(new Vector3f( -0.5f, -0.5f, 0.5f),
//                                                   new Vector3f(0.5f, -0.5f, 0.5f),
//                                                   new Vector3f(-0.5f, 0.5f, 0.5f),
//                                                   new Vector2f(0, 0),
//                                                   new Vector2f(1, 0),
//                                                   new Vector2f(0, 1),
//                                                   myNormal,
//                                                   myTangent);
//            gl.glVertexAttrib3f(myColorAttributeLoc, myTangent.x, myTangent.y, myTangent.z);
//            gl.glNormal3f(myNormal.x, myNormal.y, myNormal.z);
//            gl.glTexCoord2f(0, 0);
//            gl.glVertex3f( -0.5f, -0.5f, 0.5f);
//            gl.glTexCoord2f(1, 0);
//            gl.glVertex3f(0.5f, -0.5f, 0.5f);
//            gl.glTexCoord2f(1, 1);
//            gl.glVertex3f(0.5f, 0.5f, 0.5f);
//            gl.glTexCoord2f(0, 1);
//            gl.glVertex3f( -0.5f, 0.5f, 0.5f);

            // Top Face
            Vector3f myNormal = new Vector3f();
            mathematik.Util.calculateNormal(new Vector3f(-0.5f, 0.5f, -0.5f),
                                            new Vector3f(-0.5f, 0.5f, 0.5f),
                                            new Vector3f(0.5f, 0.5f, -0.5f),
                                            myNormal);
            Vector4f myTangent = new Vector4f();
            calculateTangentVector(new Vector3f(-0.5f, 0.5f, -0.5f),
                                   new Vector3f(-0.5f, 0.5f, 0.5f),
                                   new Vector3f(0.5f, 0.5f, -0.5f),
                                   new Vector2f(0, 1),
                                   new Vector2f(0, 0),
                                   new Vector2f(1, 1),
                                   myNormal,
                                   myTangent);
            gl.glVertexAttrib3f(myColorAttributeLoc, myTangent.x, myTangent.y, myTangent.z);
            gl.glNormal3f(myNormal.x, myNormal.y, myNormal.z);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-0.5f, 0.5f, -0.5f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-0.5f, 0.5f, 0.5f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0.5f, 0.5f, 0.5f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0.5f, 0.5f, -0.5f);

            gl.glEnd();

            gl.glPopMatrix();

            _myMaterial.end(theRenderContext);
        }

        public void add(Drawable theDrawable) {
            /* shape doesn t accept children. */
        }

        public boolean isActive() {
            return true;
        }

        public float getSortValue() {
            return _mySortValue;
        }

        public void setSortValue(float theSortValue) {
            _mySortValue = theSortValue;
        }

        public float[] getSortData() {
            return position.toArray();
        }

        public boolean isSortable() {
            return true;
        }

        public Material material() {
            return _myMaterial;
        }
    }

    public static void calculateTangentVector(Vector3f theVertex1,
                                              Vector3f theVertex2,
                                              Vector3f theVertex3,
                                              Vector2f theTexCoord1,
                                              Vector2f theTexCoord2,
                                              Vector2f theTexCoord3,
                                              Vector3f theNormal,
                                              Vector4f theTangent) {
        // Given the 3 vertices (position and texture coordinates) of a triangle
        // calculate and return the triangle's tangent vector.

        // Create 2 vectors in object space.
        //
        // edge1 is the vector from vertex positions pos1 to pos2.
        // edge2 is the vector from vertex positions pos1 to pos3.
        Vector3f myEdge1 = new Vector3f(theVertex2);
        myEdge1.sub(theVertex1);
        myEdge1.normalize();
        Vector3f myEdge2 = new Vector3f(theVertex3);
        myEdge2.sub(theVertex1);
        myEdge2.normalize();

        // Create 2 vectors in tangent (texture) space that point in the same
        // direction as edge1 and edge2 (in object space).
        //
        // texEdge1 is the vector from texture coordinates texCoord1 to texCoord2.
        // texEdge2 is the vector from texture coordinates texCoord1 to texCoord3.
        Vector2f myTexEdge1 = new Vector2f(theTexCoord2);
        myTexEdge1.sub(theTexCoord1);
        myTexEdge1.normalize();

        Vector2f myTexEdge2 = new Vector2f(theTexCoord3);
        myTexEdge2.sub(theTexCoord1);
        myTexEdge2.normalize();

        // These 2 sets of vectors form the following system of equations:
        //
        //  edge1 = (texEdge1.x * tangent) + (texEdge1.y * bitangent)
        //  edge2 = (texEdge2.x * tangent) + (texEdge2.y * bitangent)
        //
        // Using matrix notation this system looks like:
        //
        //  [ edge1 ]     [ texEdge1.x  texEdge1.y ]  [ tangent   ]
        //  [       ]  =  [                        ]  [           ]
        //  [ edge2 ]     [ texEdge2.x  texEdge2.y ]  [ bitangent ]
        //
        // The solution is:
        //
        //  [ tangent   ]        1     [ texEdge2.y  -texEdge1.y ]  [ edge1 ]
        //  [           ]  =  -------  [                         ]  [       ]
        //  [ bitangent ]      det A   [-texEdge2.x   texEdge1.x ]  [ edge2 ]
        //
        //  where:
        //        [ texEdge1.x  texEdge1.y ]
        //    A = [                        ]
        //        [ texEdge2.x  texEdge2.y ]
        //
        //    det A = (texEdge1.x * texEdge2.y) - (texEdge1.y * texEdge2.x)
        //
        // From this solution the tangent space basis vectors are:
        //
        //    tangent = (1 / det A) * ( texEdge2.y * edge1 - texEdge1.y * edge2)
        //  bitangent = (1 / det A) * (-texEdge2.x * edge1 + texEdge1.x * edge2)
        //     normal = cross(tangent, bitangent)

        Vector3f t = new Vector3f();
        Vector3f b = new Vector3f();
        Vector3f n = new Vector3f(theNormal);

        float det = (myTexEdge1.x * myTexEdge2.y) - (myTexEdge1.y * myTexEdge2.x);

//        if (Math : : closeEnough(det, 0.0f)) {
//            t.set(1.0f, 0.0f, 0.0f);
//            b.set(0.0f, 1.0f, 0.0f);
//        } else {
        det = 1.0f / det;

        t.x = (myTexEdge2.y * myEdge1.x - myTexEdge1.y * myEdge2.x) * det;
        t.y = (myTexEdge2.y * myEdge1.y - myTexEdge1.y * myEdge2.y) * det;
        t.z = (myTexEdge2.y * myEdge1.z - myTexEdge1.y * myEdge2.z) * det;

        b.x = (-myTexEdge2.x * myEdge1.x + myTexEdge1.x * myEdge2.x) * det;
        b.y = (-myTexEdge2.x * myEdge1.y + myTexEdge1.x * myEdge2.y) * det;
        b.z = (-myTexEdge2.x * myEdge1.z + myTexEdge1.x * myEdge2.z) * det;

        t.normalize();
        b.normalize();
//        }

        // Calculate the handedness of the local tangent space.
        // The bitangent vector is the cross product between the triangle face
        // normal vector and the calculated tangent vector. The resulting bitangent
        // vector should be the same as the bitangent vector calculated from the
        // set of linear equations above. If they point in different directions
        // then we need to invert the cross product calculated bitangent vector. We
        // store this scalar multiplier in the tangent vector's 'w' component so
        // that the correct bitangent vector can be generated in the normal mapping
        // shader's vertex shader.

        Vector3f myBiTangent = new Vector3f(n);
        myBiTangent.cross(t);

        float myDot = myBiTangent.dot(b);
        float handedness = 1f;
        if (myDot < 0.0f) {
            handedness = -1f;
        }

        theTangent.x = t.x;
        theTangent.y = t.y;
        theTangent.z = t.z;
        theTangent.w = handedness;
    }

    public static void main(String[] arg) {
        DisplayCapabilities dc = new DisplayCapabilities();
        dc.width = 640;
        dc.height = 480;
        dc.antialiasinglevel = 4;
        new TestBumpMappingGLSL().init(dc);
    }
}
