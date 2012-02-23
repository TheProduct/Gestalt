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


package gestalt.extension.edgeblending;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;

import mathematik.Vector3f;

import werkzeug.interpolation.InterpolateLinear;
import werkzeug.interpolation.Interpolator;

import javax.media.opengl.GL;


public class SoftEdger
    extends AbstractDrawable {

    private Interpolator _myInterpolator;

    private float _myBlendArea;

    private int _myBlendResolution;

    private final Vector3f _myPosition;

    private final Vector3f _myScale;

    private final Material _myMaterial;

    public SoftEdger(TexturePlugin theTexture,
                         int theWidth,
                         int theHeight,
                         float theFrameArea) {
        _myMaterial = new Material();
        _myMaterial.addTexture(theTexture);
        _myMaterial.depthtest = false;
        _myPosition = new Vector3f();
        _myScale = new Vector3f(theWidth, theHeight);
        _myBlendArea = theFrameArea;
        _myBlendResolution = 5;
        _myInterpolator = new Interpolator(new InterpolateLinear());
    }


    public Vector3f scale() {
        return _myScale;
    }


    public Vector3f position() {
        return _myPosition;
    }


    public void setInterpolator(Interpolator theInterpolator) {
        _myInterpolator = theInterpolator;
    }


    public void draw(GLContext theRenderContext) {
        final GL gl = (  theRenderContext).gl;

        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        _myMaterial.begin(theRenderContext);

        gl.glPushMatrix();

        gl.glBegin(GL.GL_QUADS);

        // uv
        float myMiddle = _myBlendArea / 2f / _myScale.y;

        // bottom
        gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, _myMaterial.color4f().a);

        gl.glTexCoord2f(0, 0);
        gl.glVertex3f(_myPosition.x - _myScale.x / 2f, _myPosition.y - _myScale.y / 2f - _myBlendArea, 0);

        gl.glTexCoord2f(0, - (0.5f - myMiddle));
        gl.glVertex3f(_myPosition.x - _myScale.x / 2f, _myPosition.y - _myBlendArea, 0);

        gl.glTexCoord2f(1, - (0.5f - myMiddle));
        gl.glVertex3f(_myPosition.x + _myScale.x / 2f, _myPosition.y - _myBlendArea, 0);

        gl.glTexCoord2f(1, 0);
        gl.glVertex3f(_myPosition.x + _myScale.x / 2f, _myPosition.y - _myScale.y / 2f - _myBlendArea, 0);

        // bottom gradient
        for (int i = 0; i < _myBlendResolution; i++) {
            float myDelta = _myBlendArea / _myBlendResolution;
            float myStartPos = myDelta * i + _myPosition.y - _myBlendArea;
            float myEndPos = myDelta * (i + 1) + _myPosition.y - _myBlendArea;
            float myStartAlpha = 1f - (float) i / _myBlendResolution;
            myStartAlpha = _myInterpolator.get(myStartAlpha);
            float myEndAlpha = 1f - (float) (i + 1) / _myBlendResolution;
            myEndAlpha = _myInterpolator.get(myEndAlpha);
            float myStartUV = (0.5f - myMiddle) + ( (float) i / _myBlendResolution * myMiddle * 2);
            float myEndUV = (0.5f - myMiddle) + ( (float) (i + 1) / _myBlendResolution * myMiddle * 2);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myStartAlpha);
            gl.glTexCoord2f(0, -myStartUV);
            gl.glVertex3f(_myPosition.x - _myScale.x / 2f, myStartPos, 0);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myEndAlpha);
            gl.glTexCoord2f(0, -myEndUV);
            gl.glVertex3f(_myPosition.x - _myScale.x / 2f, myEndPos, 0f);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myEndAlpha);
            gl.glTexCoord2f(1, -myEndUV);
            gl.glVertex3f(_myPosition.x + _myScale.x / 2f, myEndPos, 0);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myStartAlpha);
            gl.glTexCoord2f(1, -myStartUV);
            gl.glVertex3f(_myPosition.x + _myScale.x / 2f, myStartPos, 0);
        }

        // top
        gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, _myMaterial.color4f().a);

        gl.glTexCoord2f(0, -1f);
        gl.glVertex3f(_myPosition.x - _myScale.x / 2f, _myPosition.y + _myScale.y / 2f + _myBlendArea, 0);

        gl.glTexCoord2f(0, - (0.5f + myMiddle));
        gl.glVertex3f(_myPosition.x - _myScale.x / 2f, _myPosition.y + _myBlendArea, 0);

        gl.glTexCoord2f(1, - (0.5f + myMiddle));
        gl.glVertex3f(_myPosition.x + _myScale.x / 2f, _myPosition.y + _myBlendArea, 0);

        gl.glTexCoord2f(1, -1f);
        gl.glVertex3f(_myPosition.x + _myScale.x / 2f, _myPosition.y + _myScale.y / 2f + _myBlendArea, 0);

        // top gradient
        for (int i = 0; i < _myBlendResolution; i++) {
            float myDelta = _myBlendArea / _myBlendResolution;
            float myStartPos = _myPosition.y + _myBlendArea - myDelta * i;
            float myEndPos = _myPosition.y + _myBlendArea - myDelta * (i + 1);
            float myStartAlpha = 1f - (float) i / _myBlendResolution;
            myStartAlpha = _myInterpolator.get(myStartAlpha);
            float myEndAlpha = 1f - (float) (i + 1) / _myBlendResolution;
            myEndAlpha = _myInterpolator.get(myEndAlpha);
            float myStartUV = (0.5f + myMiddle) - ( (float) i / _myBlendResolution * myMiddle * 2);
            float myEndUV = (0.5f + myMiddle) - ( (float) (i + 1) / _myBlendResolution * myMiddle * 2);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myStartAlpha);
            gl.glTexCoord2f(0, -myStartUV);
            gl.glVertex3f(_myPosition.x - _myScale.x / 2f, myStartPos, 0);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myEndAlpha);
            gl.glTexCoord2f(0, -myEndUV);
            gl.glVertex3f(_myPosition.x - _myScale.x / 2f, myEndPos, 0f);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myEndAlpha);
            gl.glTexCoord2f(1, -myEndUV);
            gl.glVertex3f(_myPosition.x + _myScale.x / 2f, myEndPos, 0);

            gl.glColor4f(_myMaterial.color4f().r, _myMaterial.color4f().g, _myMaterial.color4f().b, myStartAlpha);
            gl.glTexCoord2f(1, -myStartUV);
            gl.glVertex3f(_myPosition.x + _myScale.x / 2f, myStartPos, 0);
        }

        gl.glEnd();

        gl.glPopMatrix();

        _myMaterial.end(theRenderContext);
    }
}
