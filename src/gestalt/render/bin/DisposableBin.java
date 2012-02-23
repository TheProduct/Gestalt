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


package gestalt.render.bin;

import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.material.Material;
import gestalt.material.texture.Bitmap;
import gestalt.render.Drawable;
import gestalt.shape.FastBitmapFont;
import gestalt.shape.Plane;
import gestalt.util.JoglUtil;

import mathematik.Vector2f;
import mathematik.Vector3f;

import java.util.Vector;

import javax.media.opengl.GL;


public class DisposableBin
        implements Drawable {

    public Vector3f scale;

    public Vector3f position;

    public boolean active;

    public boolean autoclear;

    public boolean interpret_linewidth = true;

    public boolean wireframe = false;

    public boolean fill = true;

    public final Material material;

    public final Color wireframe_color = new Color(1, 1);

    private final Color _myCurrentColor;

    private float mCurrentLineWidth;

    private final FastBitmapFont _myTextDrawer;

    private int _myCurrentFont;

    private int _myCurrentTextAlign;

    private final Vector<Point> _myPoints;

    private final Vector<Line> mLines;

    private final Vector<Circle> _myCircles;

    private final Vector<Drawable> _myDrawables;

    private final Vector<Text> _myText;

    private final Vector<Triangle> _myTriangles;

    private final Vector<Quad> _myQuads;

    private final Vector<Cross> _myCrosses;

    private final Vector<Box> _myBoxes;

    private final Vector<PrvPlane> _myCustomPlanes;

    private final Plane _myCustomPlane;

    private final Vector<Vector> _myAutoClearContainer = new Vector<Vector>();

    public DisposableBin() {
        _myPoints = new Vector<Point>();
        mLines = new Vector<Line>();
        _myCircles = new Vector<Circle>();
        _myDrawables = new Vector<Drawable>();
        _myText = new Vector<Text>();
        _myTriangles = new Vector<Triangle>();
        _myQuads = new Vector<Quad>();
        _myCrosses = new Vector<Cross>();
        _myBoxes = new Vector<Box>();
        _myCustomPlanes = new Vector<PrvPlane>();

        _myAutoClearContainer.add(_myPoints);
        _myAutoClearContainer.add(mLines);
        _myAutoClearContainer.add(_myCircles);
        _myAutoClearContainer.add(_myDrawables);
        _myAutoClearContainer.add(_myText);
        _myAutoClearContainer.add(_myTriangles);
        _myAutoClearContainer.add(_myQuads);
        _myAutoClearContainer.add(_myCrosses);
        _myAutoClearContainer.add(_myBoxes);
        _myAutoClearContainer.add(_myCustomPlanes);

        scale = new Vector3f(1, 1, 1);
        position = new Vector3f();
        _myCurrentColor = new Color(1, 1);
        active = true;
        autoclear = true;
        material = new Material();
        _myTextDrawer = new FastBitmapFont();
        _myCurrentFont = _myTextDrawer.font;
        _myCurrentTextAlign = FastBitmapFont.LEFT;
        _myCustomPlane = new Plane();
        mCurrentLineWidth = 1.0f;
    }

    public Plane plane_primitive() {
        return _myCustomPlane;
    }

    public void addTextureToPlanePrimitve(Bitmap theBitmap) {
        plane_primitive().material().addTexture().load(theBitmap);
    }

    public void draw(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;

        /* end material */
        material.begin(theRenderContext);

        /* --- */
        gl.glPushMatrix();
        gl.glTranslatef(position.x, position.y, position.z);
        gl.glScalef(scale.x, scale.y, scale.z);

        if (fill) {
            drawPrimitives(gl, false);
        }

        if (wireframe) {
            gl.glColor4f(wireframe_color.r,
                         wireframe_color.g,
                         wireframe_color.b,
                         wireframe_color.a);
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
            drawPrimitives(gl, true);
            gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
        }

        /* text */
        for (final Text myText : _myText) {
            _myTextDrawer.text = myText.text;
            _myTextDrawer.position.set(myText.x, myText.y, myText.z);
            _myTextDrawer.font = myText.font;
            _myTextDrawer.color.set(myText.color);
            _myTextDrawer.align = myText.align;
            _myTextDrawer.draw(theRenderContext);
        }

        /* drawables */
        for (int i = 0; i < _myDrawables.size(); i++) {
            final Drawable myDrawable = (Drawable)_myDrawables.get(i);
            myDrawable.draw(theRenderContext);
        }

        /* --- */
        gl.glPopMatrix();

        /* end material */
        material.end(theRenderContext);

        /* plane */
        for (final PrvPlane myPlane : _myCustomPlanes) {
            _myCustomPlane.position().set(myPlane.position);
            _myCustomPlane.scale().set(myPlane.scale);
            _myCustomPlane.rotation().set(myPlane.rotation);
            _myCustomPlane.material().color4f().set(myPlane.color);
            _myCustomPlane.draw(theRenderContext);
        }

        /* clean up */
        if (autoclear) {
            clear();
        }
    }

    public void textalign(int theTextAlign) {
        _myCurrentTextAlign = theTextAlign;
    }

    public void color(final float r, final float g, final float b, final float a) {
        _myCurrentColor.set(r, g, b, a);
    }

    public void color(final float g, final float a) {
        _myCurrentColor.set(g, a);
    }

    public void color(final float g) {
        _myCurrentColor.set(g);
    }

    public void color(final Color theColor) {
        _myCurrentColor.set(theColor);
    }

    public Color color() {
        return _myCurrentColor;
    }

    public void linewidth(final float pLineWidth) {
        mCurrentLineWidth = pLineWidth;
    }

    public void clear() {
        for (final Vector v : _myAutoClearContainer) {
            v.clear();
        }
    }

    public void add(Drawable theDrawable) {
        _myDrawables.add(theDrawable);
    }

    public boolean isActive() {
        return active;
    }

    public float getSortValue() {
        return 0.0F;
    }

    public void setSortValue(float theSortValue) {
    }

    public float[] getSortData() {
        return null;
    }

    public boolean isSortable() {
        return false;
    }


    /* point */
    public void point(final float x, final float y) {
        if (active) {
            _myPoints.add(new Point(x, y, 0));
        }
    }

    public void point(final float x, final float y, final float z) {
        if (active) {
            _myPoints.add(new Point(x, y, z));
        }
    }

    public void point(final Vector3f thePoint) {
        if (active) {
            _myPoints.add(new Point(thePoint.x, thePoint.y, thePoint.z));
        }
    }

    private void _color(GL gl, Color c) {
        if (fill) {
            gl.glColor4f(c.r, c.g, c.b, c.a);
        }
    }

    private void drawPrimitives(final GL gl, boolean theWireFrame) {
        /* points */
        gl.glBegin(GL.GL_POINTS);
        for (int i = 0; i < _myPoints.size(); i++) {
            final Point myPoint = _myPoints.get(i);
            if (!theWireFrame) {
                _color(gl, myPoint.color);
            }
            gl.glVertex3f(myPoint.x, myPoint.y, myPoint.z);
        }
        gl.glEnd();
        /* lines */
        if (interpret_linewidth) {
            for (int i = 0; i < mLines.size(); i++) {
                final Line myLine = mLines.get(i);
                gl.glLineWidth(myLine.width);
                gl.glBegin(GL.GL_LINES);
                if (!theWireFrame) {
                    _color(gl, myLine.color);
                }
                gl.glVertex3f(myLine.startX, myLine.startY, myLine.startZ);
                gl.glVertex3f(myLine.endX, myLine.endY, myLine.endZ);
                gl.glEnd();
            }
        } else {
            gl.glBegin(GL.GL_LINES);
            for (int i = 0; i < mLines.size(); i++) {
                final Line myLine = mLines.get(i);
                if (!theWireFrame) {
                    _color(gl, myLine.color);
                }
                gl.glVertex3f(myLine.startX, myLine.startY, myLine.startZ);
                gl.glVertex3f(myLine.endX, myLine.endY, myLine.endZ);
            }
            gl.glEnd();
        }
        /* crosses */
        gl.glBegin(GL.GL_LINES);
        for (int i = 0; i < _myCrosses.size(); i++) {
            final Cross myCross = _myCrosses.get(i);
            if (!theWireFrame) {
                _color(gl, myCross.color);
            }
            gl.glVertex3f(myCross.x - myCross.d, myCross.y, myCross.z);
            gl.glVertex3f(myCross.x + myCross.d, myCross.y, myCross.z);
            gl.glVertex3f(myCross.x, myCross.y - myCross.d, myCross.z);
            gl.glVertex3f(myCross.x, myCross.y + myCross.d, myCross.z);
            gl.glVertex3f(myCross.x, myCross.y, myCross.z - myCross.d);
            gl.glVertex3f(myCross.x, myCross.y, myCross.z + myCross.d);
        }
        gl.glEnd();
        /* circles */
        for (int i = 0; i < _myCircles.size(); i++) {
            final Circle myCircle = _myCircles.get(i);
            if (!theWireFrame) {
                _color(gl, myCircle.color);
            }
            gl.glPushMatrix();
            gl.glTranslatef(myCircle.position.x, myCircle.position.y, myCircle.position.z);
            if (myCircle._myPlane == PLANE_XZ) {
                gl.glRotatef(90, 1, 0, 0);
            } else if (myCircle._myPlane == PLANE_YZ) {
                gl.glRotatef(90, 0, 1, 0);
            }
            JoglUtil.circle(gl, myCircle.radius);
            gl.glPopMatrix();
        }
        /* triangles */
        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < _myTriangles.size(); i++) {
            final Triangle myTriangle = _myTriangles.get(i);
            if (!theWireFrame) {
                _color(gl, myTriangle.color);
            }
            gl.glVertex3f(myTriangle.a.x, myTriangle.a.y, myTriangle.a.z);
            gl.glVertex3f(myTriangle.b.x, myTriangle.b.y, myTriangle.b.z);
            gl.glVertex3f(myTriangle.c.x, myTriangle.c.y, myTriangle.c.z);
        }
        gl.glEnd();
        /* quads */
        gl.glBegin(GL.GL_QUADS);
        for (int i = 0; i < _myQuads.size(); i++) {
            final Quad myQuad = _myQuads.get(i);
            if (!theWireFrame) {
                _color(gl, myQuad.color);
            }
            gl.glVertex3f(myQuad.a.x, myQuad.a.y, myQuad.a.z);
            gl.glVertex3f(myQuad.b.x, myQuad.b.y, myQuad.b.z);
            gl.glVertex3f(myQuad.c.x, myQuad.c.y, myQuad.c.z);
            gl.glVertex3f(myQuad.d.x, myQuad.d.y, myQuad.d.z);
        }
        gl.glEnd();
        /* boxes */
        for (Box myBox : _myBoxes) {
            if (!theWireFrame) {
                _color(gl, myBox.color);
            }
            gl.glPushMatrix();
            gl.glTranslated(myBox.position.x, myBox.position.y, myBox.position.z);
            gl.glScalef(myBox.scale.x, myBox.scale.y, myBox.scale.z);
            gl.glBegin(GL.GL_QUADS);
            // Front Face
            gl.glNormal3f(0, 0, 1);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0f, 0f, 0f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(1f, 0f, 0f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(1f, 1f, 0f);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0f, 1f, 0f);
            // Back Face
            gl.glNormal3f(0, 0, -1);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0f, 0f, 1f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0f, 1f, 1f);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(1f, 1f, 1f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(1f, 0f, 1f);
            // Top Face
            gl.glNormal3f(0, 1, 0);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0f, 1f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0f, 1f, 1f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(1f, 1f, 1f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(1f, 1f, 0f);
            // Bottom Face
            gl.glNormal3f(0, -1, 0);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0f, 0f, 1f);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(1f, 0f, 1f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(1f, 0f, 0f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0f, 0f, 0f);
            // Right face
            gl.glNormal3f(1, 0, 0);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(1f, 0f, 1f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(1f, 1f, 1f);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(1f, 1f, 0f);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(1f, 0f, 0f);
            // Left Face
            gl.glNormal3f(-1, 0, 0);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0f, 0f, 1f);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(0f, 0f, 0f);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(0f, 1f, 0f);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0f, 1f, 1f);
            gl.glEnd();
            gl.glPopMatrix();
        }
    }

    private class Point {

        private final float x;

        private final float y;

        private final float z;

        private final Color color;

        private Point(final float theX, final float theY, final float theZ) {
            x = theX;
            y = theY;
            z = theZ;
            color = new Color(_myCurrentColor);
        }
    }


    /* text */
    public void text(String theText, final float x, final float y, final float z) {
        if (active) {
            _myText.add(new Text(theText, x, y, z));
        }
    }

    public void text(String theText, final Vector3f v) {
        if (active) {
            _myText.add(new Text(theText, v.x, v.y, v.z));
        }
    }

    public void text(String theText, final float x, final float y) {
        if (active) {
            _myText.add(new Text(theText, x, y, 0));
        }
    }

    public void text(float theValue, final float x, final float y, final float z) {
        if (active) {
            _myText.add(new Text(Float.toString(theValue), x, y, z));
        }
    }

    public void text(float theValue, final Vector3f v) {
        if (active) {
            _myText.add(new Text(Float.toString(theValue), v.x, v.y, v.z));
        }
    }

    public void text(float theValue, final float x, final float y) {
        if (active) {
            _myText.add(new Text(Float.toString(theValue), x, y, 0));
        }
    }

    public void font(int theFont) {
        _myCurrentFont = theFont;
    }

    private class Text {

        private String text;

        private float x;

        private float y;

        private float z;

        private int font;

        private final Color color;

        private final int align;

        private Text(String theText, final float theX, final float theY, final float theZ) {
            text = theText;
            x = theX;
            y = theY;
            z = theZ;
            color = new Color(_myCurrentColor);
            font = _myCurrentFont;
            align = _myCurrentTextAlign;
        }
    }


    /* line */
    public void line(final float startX, final float startY,
                     final float endX, final float endY) {
        if (active) {
            mLines.add(new Line(startX, startY, 0, endX, endY, 0, mCurrentLineWidth));
        }
    }

    public void line(final float startX, final float startY, final float startZ,
                     final float endX, final float endY, final float endZ) {
        if (active) {
            mLines.add(new Line(startX, startY, startZ,
                                endX, endY, endZ,
                                mCurrentLineWidth));
        }
    }

    public void line(final Vector3f theStart, final Vector3f theEnd) {
        if (active) {
            mLines.add(new Line(theStart.x, theStart.y, theStart.z,
                                theEnd.x, theEnd.y, theEnd.z,
                                mCurrentLineWidth));
        }
    }

    public void line(final Vector2f theStart, final Vector2f theEnd) {
        if (active) {
            mLines.add(new Line(theStart.x, theStart.y, 0.0f,
                                theEnd.x, theEnd.y, 0.0f,
                                mCurrentLineWidth));
        }
    }

    public void lineto(final Vector3f theStart, final Vector3f theEnd) {
        if (active) {
            mLines.add(new Line(theStart.x,
                                theStart.y,
                                theStart.z,
                                theStart.x + theEnd.x,
                                theStart.y + theEnd.y,
                                theStart.z + theEnd.z,
                                mCurrentLineWidth));
        }
    }

    public void lineto(final Vector2f theStart, final Vector2f theEnd) {
        if (active) {
            mLines.add(new Line(theStart.x,
                                theStart.y,
                                0.0f,
                                theStart.x + theEnd.x,
                                theStart.y + theEnd.y,
                                0.0f,
                                mCurrentLineWidth));
        }
    }

    public class Line {

        public final float startX;

        public final float startY;

        public final float startZ;

        public final float endX;

        public final float endY;

        public final float endZ;

        public final Color color;

        public final float width;

        private Line(final float theStartX, final float theStartY, final float theStartZ,
                     final float theEndX, final float theEndY, final float theEndZ, final float pWidth) {
            startX = theStartX;
            startY = theStartY;
            startZ = theStartZ;
            endX = theEndX;
            endY = theEndY;
            endZ = theEndZ;
            color = new Color(_myCurrentColor);
            width = pWidth;
        }
    }

    public void collectLineData(Vector<Vector3f> pVertexData, Vector<Color> pColorData) {
        for (int i = 0; i < mLines.size(); i++) {
            final Line mLine = mLines.get(i);
            if (pColorData != null) {
                pColorData.add(new Color(mLine.color));
            }
            pVertexData.add(new Vector3f(mLine.startX, mLine.startY, mLine.startZ));
            pVertexData.add(new Vector3f(mLine.endX, mLine.endY, mLine.endZ));
        }
    }

    public Vector<Line> collectLines() {
        return mLines;
    }

    /* cross */
    public void cross(final float theX, final float theY, final float theZ, final float theD) {
        if (active) {
            _myCrosses.add(new Cross(theX, theY, theZ, theD));
        }
    }

    public void cross(final Vector3f theV, final float theD) {
        if (active) {
            _myCrosses.add(new Cross(theV.x, theV.y, theV.z, theD));
        }
    }

    private class Cross {

        private final float x;

        private final float y;

        private final float z;

        private final float d;

        private final Color color;

        private Cross(final float theX, final float theY, final float theZ, final float theD) {
            x = theX;
            y = theY;
            z = theZ;
            d = theD;
            color = new Color(_myCurrentColor);
        }
    }


    /* cirlce */
    public void circle(final Vector3f theCenter, final float theRadius) {
        if (active) {
            _myCircles.add(new Circle(theCenter, theRadius));
        }
    }

    public void circle(final float theX, final float theY, final float theZ, final float theRadius) {
        if (active) {
            _myCircles.add(new Circle(theX, theY, theZ, theRadius));
        }
    }

    public void circle(final Vector3f theCenter, final float theRadius, final int thePlane) {
        if (active) {
            _myCircles.add(new Circle(theCenter, theRadius, thePlane));
        }
    }

    public void circle(final float theX, final float theY, final float theZ, final float theRadius, final int thePlane) {
        if (active) {
            _myCircles.add(new Circle(theX, theY, theZ, theRadius, thePlane));
        }
    }

    public void quicksphere(final Vector3f theCenter, final float theRadius) {
        circle(theCenter, theRadius, PLANE_XY);
        circle(theCenter, theRadius, PLANE_XZ);
        circle(theCenter, theRadius, PLANE_YZ);
    }

    public static final int PLANE_XY = 0;

    public static final int PLANE_XZ = 1;

    public static final int PLANE_YZ = 2;

    private class Circle {

        private final float radius;

        private final Color color;

        private final Vector3f position;

        private final int _myPlane;

        private Circle(final float theX, final float theY, final float theZ, final float theRadius, final int thePlane) {
            position = new Vector3f(theX, theY, theZ);
            radius = theRadius;
            color = new Color(_myCurrentColor);
            _myPlane = thePlane;
        }

        private Circle(final Vector3f thePosition, final float theRadius, final int thePlane) {
            position = thePosition;
            radius = theRadius;
            color = new Color(_myCurrentColor);
            _myPlane = thePlane;

        }

        private Circle(final float theX, final float theY, final float theZ, final float theRadius) {
            this(theX, theY, theZ, theRadius, PLANE_XY);
        }

        private Circle(final Vector3f thePosition, final float theRadius) {
            this(thePosition, theRadius, PLANE_XY);
        }
    }


    /* triangle */
    private class Triangle {

        private final Vector3f a;

        private final Vector3f b;

        private final Vector3f c;

        private final Color color;

        private Triangle(Vector3f theA, Vector3f theB, Vector3f theC) {
            a = theA;
            b = theB;
            c = theC;
            color = new Color(_myCurrentColor);
        }

        private Triangle(float a0, float a1, float a2,
                         float b0, float b1, float b2,
                         float c0, float c1, float c2) {
            this(new Vector3f(a0, a1, a2),
                 new Vector3f(b0, b1, b2),
                 new Vector3f(c0, c1, c2));
        }
    }

    public void triangle(Vector3f a, Vector3f b, Vector3f c) {
        if (active) {
            _myTriangles.add(new Triangle(a, b, c));
        }
    }

    public void triangle(float a0, float a1, float a2,
                         float b0, float b1, float b2,
                         float c0, float c1, float c2) {
        if (active) {
            _myTriangles.add(new Triangle(a0, a1, a2,
                                          b0, b1, b2,
                                          c0, c1, c2));
        }
    }

    public void collectTriangleData(Vector<Vector3f> pVertexData, Vector<Color> pColorData) {
        for (int i = 0; i < _myTriangles.size(); i++) {
            final Triangle myTriangle = _myTriangles.get(i);
            if (pColorData != null) {
                pColorData.add(new Color(myTriangle.color));
            }
            pVertexData.add(new Vector3f(myTriangle.a.x, myTriangle.a.y, myTriangle.a.z));
            pVertexData.add(new Vector3f(myTriangle.b.x, myTriangle.b.y, myTriangle.b.z));
            pVertexData.add(new Vector3f(myTriangle.c.x, myTriangle.c.y, myTriangle.c.z));
        }
    }


    /* box */
    public void box(final Vector3f thePosition, final Vector3f theScale) {
        if (active) {
            _myBoxes.add(new Box(thePosition, theScale));
        }
    }

    private class Box {

        private final Vector3f position;

        private final Vector3f scale;

        private final Color color;

        private Box(Vector3f thePosition, Vector3f theScale) {
            position = new Vector3f(thePosition);
            scale = new Vector3f(theScale);
            color = new Color(_myCurrentColor);
        }
    }


    /* quad */
    public void quad(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
        if (active) {
            _myQuads.add(new Quad(a, b, c, d));
        }
    }

    private class Quad {

        private final Vector3f a;

        private final Vector3f b;

        private final Vector3f c;

        private final Vector3f d;

        private final Color color;

        private Quad(Vector3f theA, Vector3f theB, Vector3f theC, Vector3f theD) {
            a = theA;
            b = theB;
            c = theC;
            d = theD;
            color = new Color(_myCurrentColor);
        }
    }


    /* plane */
    public void plane(final Vector3f thePosition, final Vector3f theScale, final Vector3f theRotation) {
        if (active) {
            _myCustomPlanes.add(new PrvPlane(thePosition, theScale, theRotation));
        }
    }

    public void plane(final Vector3f thePosition, final Vector3f theScale) {
        if (active) {
            _myCustomPlanes.add(new PrvPlane(thePosition, theScale, new Vector3f()));
        }
    }

    public void plane(final Vector3f thePosition, final float theScale) {
        if (active) {
            _myCustomPlanes.add(new PrvPlane(thePosition, new Vector3f(theScale, theScale, 1), new Vector3f()));
        }
    }

    public void plane(final float x, final float y, final float z, final float theScale) {
        if (active) {
            _myCustomPlanes.add(new PrvPlane(new Vector3f(x, y, z), new Vector3f(theScale, theScale, 1), new Vector3f()));
        }
    }

    private class PrvPlane {

        private final Vector3f position;

        private final Vector3f scale;

        private final Vector3f rotation;

        private final Color color;

        private PrvPlane(Vector3f thePosition, Vector3f theScale, Vector3f theRotation) {
            position = new Vector3f(thePosition);
            scale = new Vector3f(theScale);
            rotation = new Vector3f(theRotation);
            color = new Color(_myCurrentColor);
        }
    }
}
