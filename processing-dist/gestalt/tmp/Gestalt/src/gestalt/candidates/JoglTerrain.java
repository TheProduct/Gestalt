

package gestalt.candidates;

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.shape.AbstractShape;

import mathematik.TransformMatrix4f;
import mathematik.Util;
import mathematik.Vector3f;

import werkzeug.interpolation.InterpolateSmoothstep;
import werkzeug.interpolation.Interpolator;

import javax.media.opengl.GL;


public class JoglTerrain
        extends AbstractShape {

    public boolean BORDERS_AT_SEA_LEVEL;

    public float BORDER_RANGE = 0.2f;

    public Interpolator BORDER_INTERPOLATOR;

    public boolean INVERT_DEFORMATION = false;

    private TransformMatrix4f _myTransform;

    private Vector3f[][] _myVertices;

    private Vector3f[][] _myNormals;

    private int _myQuadsX;

    private int _myQuadsY;

    private Vector3f _myScale;

    public JoglTerrain(int theQUADSX, int theQUADSY, Vector3f theScale) {
        _myQuadsX = theQUADSX;
        _myQuadsY = theQUADSY;
        _myScale = theScale;

        BORDER_INTERPOLATOR = new Interpolator(new InterpolateSmoothstep(0.1f, 0.9f));

        _myTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);

        material = new Material();

        _myVertices = new Vector3f[theQUADSX][theQUADSY];
        float myCellWidth = 1f / (theQUADSX - 1);
        float myCellHeight = 1f / (theQUADSY - 1);
        for (int x = 0; x < theQUADSX; x++) {
            for (int y = 0; y < theQUADSY; y++) {
                Vector3f myVertex = new Vector3f();
                myVertex.x = myCellWidth * x;
                myVertex.y = myCellHeight * y;
                _myVertices[x][y] = myVertex;
            }
        }

        _myNormals = new Vector3f[theQUADSX][theQUADSY];
        for (int x = 0; x < theQUADSX; x++) {
            for (int y = 0; y < theQUADSY; y++) {
                Vector3f myVertex = new Vector3f(0, 0, 1);
                _myNormals[x][y] = myVertex;
            }
        }
    }

    public void setData(Vector3f[][] theVertices) {
        _myVertices = new Vector3f[theVertices.length][theVertices[0].length];
        for (int x = 0; x < _myVertices.length; x++) {
            for (int y = 0; y < _myVertices[x].length; y++) {
                _myVertices[x][y] = new Vector3f(theVertices[x][y]);
            }
        }
        _myNormals = new Vector3f[theVertices.length][theVertices[0].length];
        for (int x = 0; x < _myVertices.length; x++) {
            for (int y = 0; y < _myVertices[x].length; y++) {
                Vector3f myVertex = new Vector3f(0, 0, 1);
                _myNormals[x][y] = myVertex;
            }
        }
    }

    public float getHeightAtPosition(final Vector3f thePosition) {
        final Vector3f myPosition = new Vector3f(thePosition);
        /* transform into local space */
        myPosition.sub(position());
        _myTransform.rotation.transform(myPosition);

        /* convert position to array index */
        myPosition.scale((float)(getQuadsX() - 1) / scale().x,
                         (float)(getQuadsY() - 1) / scale().y,
                         1);

        int myX = (int)Math.floor(myPosition.x);
        int myY = (int)Math.floor(myPosition.y);

        if (myX < 0) {
            myX = 0;
        }
        if (myX > getQuadsX() - 2) {
            myX = getQuadsX() - 2;
        }
        if (myY < 0) {
            myY = 0;
        }
        if (myY > getQuadsY() - 2) {
            myY = getQuadsY() - 2;
        }

        final float myRatioX = myPosition.x - myX;
        final float myRatioY = myPosition.y - myY;
        final int myNextX = myX + 1;
        final int myNextY = myY + 1;
        final float q00 = vertices()[myX][myY].z;
        final float q10 = vertices()[myNextX][myY].z;
        final float q11 = vertices()[myNextX][myNextY].z;
        final float q01 = vertices()[myX][myNextY].z;
        final float myZ = Util.bilinearInterp(myRatioX, myRatioY, q00, q10, q01, q11);
        return myZ * scale().z;

//        final int myX = (int) Math.floor(myPosition.x);
//        final int myY = (int) Math.floor(myPosition.y);
//
//        if (myX >= 0 &&
//            myY >= 0 &&
//            myX < getQuadsX() - 1 &&
//            myY < getQuadsY() - 1) {
//            final float myRatioX = myPosition.x - myX;
//            final float myRatioY = myPosition.y - myY;
//            final int myNextX = myX + 1;
//            final int myNextY = myY + 1;
//            final float q00 = vertices()[myX][myY].z;
//            final float q10 = vertices()[myNextX][myY].z;
//            final float q11 = vertices()[myNextX][myNextY].z;
//            final float q01 = vertices()[myX][myNextY].z;
//            final float myZ = Util.bilinearInterp(myRatioX, myRatioY, q00, q10, q01, q11);
//            return myZ * scale().z;
//        } else {
//            return thePosition.z;
//        }
    }

    public Vector3f position() {
        return _myTransform.translation;
    }

    public TransformMatrix4f transform() {
        return _myTransform;
    }

    public void update() {
        for (int x = 0; x < _myVertices.length - 1; x++) {
            for (int y = 0; y < _myVertices[x].length - 1; y++) {
                Vector3f myNormal = new Vector3f();
                Util.calculateNormal(_myVertices[x][y],
                                     _myVertices[x + 1][y],
                                     _myVertices[x + 1][y + 1],
                                     myNormal);
                _myNormals[x][y] = myNormal;
            }
        }
    }

    public void normalizeVertices() {
        for (int x = 0; x < _myVertices.length - 1; x++) {
            for (int y = 0; y < _myVertices[x].length - 1; y++) {
                _myVertices[x][y].z = _myVertices[x][y].z / scale().z;
            }
        }
    }

    public void deform(ByteBitmap theBitmap) {
        for (int x = 0; x < _myQuadsX; x++) {
            for (int y = 0; y < _myQuadsY; y++) {
                Color myColor = new Color();
                int myPixelX = (int)((float)x / _myQuadsX * theBitmap.getWidth());
                int myPixelY = (int)((float)y / _myQuadsY * theBitmap.getHeight());
                theBitmap.getPixel(myPixelX, theBitmap.getHeight() - myPixelY - 1,
                                   myColor);
                float myElevation = (myColor.r + myColor.g + myColor.b) / 3f;
                if (INVERT_DEFORMATION) {
                    _myVertices[x][y].z = -myElevation;
                } else {
                    _myVertices[x][y].z = myElevation;
                }
            }
        }
        if (BORDERS_AT_SEA_LEVEL) {
            fadeBorderToSeaLevel();
        }
        update();
    }

    public void deform(ByteBitmap theBitmap, int theColorChannel) {
        for (int x = 0; x < _myQuadsX; x++) {
            for (int y = 0; y < _myQuadsY; y++) {
                Color myColor = new Color();
                int myPixelX = (int)((float)x / _myQuadsX * theBitmap.getWidth());
                int myPixelY = (int)((float)y / _myQuadsY * theBitmap.getHeight());
                theBitmap.getPixel(myPixelX, theBitmap.getHeight() - myPixelY - 1,
                                   myColor);
                switch (theColorChannel) {
                    case Gestalt.RED:
                        if (INVERT_DEFORMATION) {
                            _myVertices[x][y].z = -myColor.r;
                        } else {
                            _myVertices[x][y].z = myColor.r;
                        }
                        break;
                    case Gestalt.GREEN:
                        if (INVERT_DEFORMATION) {
                            _myVertices[x][y].z = -myColor.g;
                        } else {
                            _myVertices[x][y].z = myColor.g;
                        }
                        break;
                    case Gestalt.BLUE:
                        if (INVERT_DEFORMATION) {
                            _myVertices[x][y].z = -myColor.b;
                        } else {
                            _myVertices[x][y].z = myColor.b;
                        }
                        break;
                }
            }
        }
        if (BORDERS_AT_SEA_LEVEL) {
            fadeBorderToSeaLevel();
        }
        update();
    }

    public void deformNormalized(ByteBitmap theBitmap) {
        float myMinBrightness = 255;
        float myMaxBrigthness = 0;
        float myStepX = (float)theBitmap.getWidth() / _myQuadsX;
        float myStepY = (float)theBitmap.getHeight() / _myQuadsY;
        if (myStepX < 1) {
            myStepX = 1;
        }
        if (myStepY < 1) {
            myStepY = 1;
        }
        for (int x = 0; x < theBitmap.getWidth(); x += myStepX) {
            for (int y = 0; y < theBitmap.getHeight(); y += myStepY) {
                Color myColor = new Color();
                theBitmap.getPixel(x, y, myColor);

                float myBrightness = (myColor.r + myColor.g + myColor.b) / 3f;
                if (myBrightness > myMaxBrigthness) {
                    myMaxBrigthness = myBrightness;
                }
                if (myBrightness < myMinBrightness) {
                    myMinBrightness = myBrightness;
                }
            }
        }

        if (myMaxBrigthness == myMinBrightness) {
            myMaxBrigthness = myMinBrightness + 1;
        }
        for (int x = 0; x < _myQuadsX; x++) {
            for (int y = 0; y < _myQuadsY; y++) {
                Color myColor = new Color();
                int myPixelX = (int)((float)x / _myQuadsX * theBitmap.getWidth());
                int myPixelY = (int)((float)y / _myQuadsY * theBitmap.getHeight());
                theBitmap.getPixel(myPixelX, theBitmap.getHeight() - myPixelY - 1,
                                   myColor);
                float myBrightness = (myColor.r + myColor.g + myColor.b) / 3f;
                float myElevation = (myBrightness - myMinBrightness) / (myMaxBrigthness - myMinBrightness);
                if (INVERT_DEFORMATION) {
                    _myVertices[x][y].z = -myElevation;
                } else {
                    _myVertices[x][y].z = myElevation;
                }
            }
        }
        if (BORDERS_AT_SEA_LEVEL) {
            fadeBorderToSeaLevel();
        }
        update();
    }

    private void fadeBorderToSeaLevel() {
        for (int x = 0; x < _myQuadsX; x++) {
            int myNumX = (int)(_myQuadsX * BORDER_RANGE);
            float myElevationRatioX = 1f;
            if (x <= myNumX) {
                myElevationRatioX = x / (float)myNumX;
            } else if (x >= _myQuadsX - myNumX) {
                myElevationRatioX = ((_myQuadsX - x - 1) / (float)myNumX);
            }
            myElevationRatioX = BORDER_INTERPOLATOR.get(myElevationRatioX);
            for (int y = 0; y < _myQuadsY; y++) {
                int myNumY = (int)(_myQuadsY * BORDER_RANGE);
                float myElevationRatioY = 1f;
                if (y <= myNumY) {
                    myElevationRatioY = y / (float)myNumY;
                } else if (y >= _myQuadsY - myNumY) {
                    myElevationRatioY = ((_myQuadsY - y - 1) / (float)myNumY);
                }
                myElevationRatioY = BORDER_INTERPOLATOR.get(myElevationRatioY);
                float myElevationRatio = myElevationRatioX * myElevationRatioY;
                _myVertices[x][y].z *= myElevationRatio;
            }
        }
    }

    public int getQuadsX() {
        return _myQuadsX;
    }

    public int getQuadsY() {
        return _myQuadsY;
    }

    public Vector3f scale() {
        return _myScale;
    }

    public Vector3f[][] vertices() {
        return _myVertices;
    }

    public Vector3f[][] normals() {
        return _myNormals;
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = (theRenderContext).gl;

        /* begin material */
        material.begin(theRenderContext);

        /* matrix */
        gl.glPushMatrix();
        /* rotation + translation */
        gl.glMultMatrixf(_myTransform.toArray(), 0);
        gl.glScalef(_myScale.x, _myScale.y, _myScale.z);

        /* draw */
        for (int x = 0; x < _myVertices.length - 1; x++) {
            for (int y = 0; y < _myVertices[x].length - 1; y++) {
                /* scale */
                gl.glBegin(GL.GL_QUADS);

                gl.glNormal3f(_myNormals[x][y].x,
                              _myNormals[x][y].y,
                              _myNormals[x][y].z);
                gl.glTexCoord2f((float)x / (_myVertices.length - 1),
                                (float)y / (_myVertices[x].length - 1));
                gl.glVertex3f(_myVertices[x][y].x,
                              _myVertices[x][y].y,
                              _myVertices[x][y].z);

                gl.glNormal3f(_myNormals[x + 1][y].x,
                              _myNormals[x + 1][y].y,
                              _myNormals[x + 1][y].z);
                gl.glTexCoord2f((float)(x + 1) / (_myVertices.length - 1),
                                (float)y / (_myVertices[x].length - 1));
                gl.glVertex3f(_myVertices[x + 1][y].x,
                              _myVertices[x + 1][y].y,
                              _myVertices[x + 1][y].z);

                gl.glNormal3f(_myNormals[x + 1][y + 1].x,
                              _myNormals[x + 1][y + 1].y,
                              _myNormals[x + 1][y + 1].z);
                gl.glTexCoord2f((float)(x + 1) / (_myVertices.length - 1),
                                (float)(y + 1) / (_myVertices[x].length - 1));
                gl.glVertex3f(_myVertices[x + 1][y + 1].x,
                              _myVertices[x + 1][y + 1].y,
                              _myVertices[x + 1][y + 1].z);

                gl.glNormal3f(_myNormals[x][y + 1].x,
                              _myNormals[x][y + 1].y,
                              _myNormals[x][y + 1].z);
                gl.glTexCoord2f((float)x / (_myVertices.length - 1),
                                (float)(y + 1) / (_myVertices[x].length - 1));
                gl.glVertex3f(_myVertices[x][y + 1].x,
                              _myVertices[x][y + 1].y,
                              _myVertices[x][y + 1].z);

                gl.glEnd();
            }
        }

        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);
    }
}
