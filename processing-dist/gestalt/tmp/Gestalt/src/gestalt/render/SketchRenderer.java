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


package gestalt.render;

import gestalt.G;
import gestalt.render.bin.DisposableBin;
import gestalt.render.controller.cameraplugins.ArcBall;
import gestalt.material.Color;
import gestalt.shape.FastBitmapFont;

import mathematik.Random;
import mathematik.Vector3f;

import com.sun.opengl.util.GLUT;

import java.util.Vector;


public class SketchRenderer
        extends AnimatorRenderer {

    private final DisposableBin _myG;

    private final DisposableBin _myStatView;

    private final Vector<String> _myStats = new Vector<String>();

    protected final Color statistics_color = new Color(0.5f, 1.0f);

    protected char key;

    protected int keyCode;

    protected int mouseX;

    protected int mouseY;

    protected int mouseButton;

    protected boolean mouseDown;

    protected float frameRate;

    protected int frame;

    public SketchRenderer() {
        super();
        _myG = new DisposableBin();
        _myG.color().set(0.5f);
        _myG.material.depthmask = false;
        _myG.material.depthtest = false;

        _myStatView = new DisposableBin();
        _myStatView.material.depthmask = false;
        _myStatView.material.depthtest = false;

        frame = 0;
    }

    public Color stats_color() {
        return statistics_color;
    }

    public String frame(final int theNumberOfDigits) {
        return werkzeug.Util.formatNumber(frame, theNumberOfDigits);
    }

    public void setupDefaults() {
        camera().plugins().add(new ArcBall());
        bin(BIN_2D_FOREGROUND).add(stats_view());
        bin(BIN_3D).add(g());
    }

    public Color backgroundcolor() {
        return displaycapabilities().backgroundcolor;
    }

    public DisposableBin stats_view() {
        return _myStatView;
    }

    public DisposableBin g() {
        return _myG;
    }

    public void addComment(String theComment) {
        _myStats.add(theComment);
    }

    public void addFPS(final float theDeltaTime) {
        addStatistic("FPS", 1 / theDeltaTime);
    }

    public void addStatistic(String theStatistic, boolean theValue) {
        _myStats.add("> " + theStatistic + ": " + theValue);
    }

    public void addStatistic(String theStatistic, int theValue) {
        _myStats.add("> " + theStatistic + ": " + theValue);
    }

    public void addStatistic(String theStatistic, int theValue, int theDigits) {
        _myStats.add("> " + theStatistic + ": " + werkzeug.Util.formatNumber(theValue, theDigits));
    }

    public void addStatistic(String theStatistic, float theValue) {
        _myStats.add("> " + theStatistic + ": " + theValue);
    }

    public void addStatistic(String theStatistic, float theValue, int theDigits) {
        _myStats.add("> " + theStatistic + ": " + werkzeug.Util.decimalPlace(theValue, theDigits));
    }

    public void addStatistic(String theStatistic, double theValue) {
        addStatistic(theStatistic, (float)theValue);
    }

    public void addStatistic(String theStatistic, double theValue, int theDigits) {
        addStatistic(theStatistic, (float)theValue, theDigits);
    }

    public void addStatistic(String theStatistic, String theValue) {
        _myStats.add("> " + theStatistic + ": " + theValue);
    }

    public void addStatistic(String theStatistic, Vector3f theValue) {
        _myStats.add("> " + theStatistic + ": " + theValue);
    }

    public void addStatistic(String theStatistic, Vector3f theValue, int theDigits) {
        _myStats.add("> " + theStatistic + ": "
                + werkzeug.Util.decimalPlace(theValue.x, theDigits) + ", "
                + werkzeug.Util.decimalPlace(theValue.y, theDigits) + ", "
                + werkzeug.Util.decimalPlace(theValue.z, theDigits));
    }

    public void update(float theDeltaTime) {
        super.update(theDeltaTime);
        parseStatistics();

        frame++;
        frameRate = 1.0f / theDeltaTime;
        if (event() != null) {
            mouseX = event().mouseX;
            mouseY = event().mouseY;
            mouseButton = event().mouseButton;
            mouseDown = event().mouseDown;
        }
    }

    private void parseStatistics() {
        final int x = 10 - width / 2;
        final int mySpacing = 12;
        int y = height / 2;

        stats_view().textalign(FastBitmapFont.LEFT);
        stats_view().color().set(statistics_color);
        stats_view().font(GLUT.BITMAP_HELVETICA_10);
        y -= mySpacing * 2;
        for (final String myStat : _myStats) {
            stats_view().text(myStat, x, y);
            y -= mySpacing;
        }
        _myStats.clear();
    }


    /* some processing mimic stuff */
    public void keyPressed(char theKey, int theKeyCode) {
        key = theKey;
        keyCode = theKeyCode;
        keyPressed();
    }

    public void keyPressed() {
    }

    public void mousePressed(int x, int y, int thePressedMouseButton) {
        mousePressed();
    }

    public void mousePressed() {
    }

    public void mouseDragged(int x, int y, int thePressedMouseButton) {
        mouseDragged();
    }

    public void mouseDragged() {
    }

    public float random(float a, float b) {
        return Random.FLOAT(a, b);
    }

    public float random() {
        return Random.FLOAT(0, 1);
    }

    public float sin(float r) {
        return (float)Math.sin(r);
    }

    public float cos(float r) {
        return (float)Math.cos(r);
    }

    public float abs(float v) {
        return (float)Math.abs(v);
    }

    public static void init(Class<? extends AnimatorRenderer> theClass) {
        G.init(theClass);
    }
}
