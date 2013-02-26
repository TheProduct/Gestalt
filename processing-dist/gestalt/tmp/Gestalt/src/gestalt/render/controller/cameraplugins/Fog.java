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

import gestalt.Gestalt;
import gestalt.context.GLContext;
import gestalt.material.Color;
import gestalt.shape.AbstractDrawable;

import javax.media.opengl.GL;


public class Fog
        extends AbstractDrawable
        implements CameraPlugin {

    public int filter;

    public boolean enable;

    protected boolean _myIsActive;

    protected float _myStart;

    protected float _myEnd;

    protected float _myDensity;

    protected Color _myColor;

    private final int[] _myFogMode;

    public Fog() {
        filter = Gestalt.FOG_FILTER_LINEAR;
        enable = true;
        _myIsActive = true;
        _myStart = 100f;
        _myEnd = 1000f;
        _myDensity = 0.1f;
        _myColor = new Color(0f, 0f, 0f, 1f);

        _myFogMode = new int[] {GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR};
    }

    public void mode(int theFogfilter) {
        filter = theFogfilter;
    }

    public void start(float theFogStart) {
        _myStart = theFogStart;
    }

    public void end(float theFogEnd) {
        _myEnd = theFogEnd;
    }

    public float start() {
        return _myStart;
    }

    public float end() {
        return _myEnd;
    }

    public void density(float theFogDensity) {
        _myDensity = theFogDensity;
    }

    public float density() {
        return _myDensity;
    }

    public Color color() {
        return _myColor;
    }

    public void setColorRef(Color theFogColor) {
        _myColor = theFogColor;
    }

    public void setActive(boolean theActive) {
        _myIsActive = theActive;
    }

    public boolean isActive() {
        return _myIsActive;
    }

    public void begin(final GLContext theRenderContext) {
        draw(theRenderContext);
    }

    public void draw(final GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        if (enable) {
            gl.glEnable(GL.GL_FOG);
            gl.glHint(GL.GL_FOG_HINT, GL.GL_DONT_CARE);
            gl.glHint(GL.GL_FOG_HINT, GL.GL_NICEST);
            gl.glFogi(GL.GL_FOG_MODE, _myFogMode[filter]);
            gl.glFogfv(GL.GL_FOG_COLOR, _myColor.toArray(), 0);
            gl.glFogf(GL.GL_FOG_DENSITY, _myDensity);
            gl.glFogf(GL.GL_FOG_START, _myStart);
            gl.glFogf(GL.GL_FOG_END, _myEnd);
        } else {
            gl.glDisable(GL.GL_FOG);
        }
    }

    public void end(GLContext theRenderContext) {
        final GL gl = theRenderContext.gl;
        gl.glDisable(GL.GL_FOG);
    }
}
