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



package gestalt.extension.shadow;


import javax.media.opengl.GL;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractShape;


public class JoglShadowMapDisplay
    extends AbstractShape {

    private JoglShadowMap _myShadowMapExtension;

    private final int _myTextureTargetID;

    public JoglShadowMapDisplay(JoglShadowMap theShadowMapExtension,
                                int theWidth,
                                int theHeight) {
        scale.set(theWidth, theHeight);
        _myShadowMapExtension = theShadowMapExtension;
        _myTextureTargetID = theShadowMapExtension.getTextureTargetID();
    }


    public void draw(GLContext theRenderContext) {

        final GLContext myJoglContext =  theRenderContext;
        final GL gl = myJoglContext.gl;

        _myShadowMapExtension.disableShadow(gl);

        gl.glEnable(_myTextureTargetID);
        gl.glEnable(GL.GL_BLEND);
        gl.glBindTexture(_myTextureTargetID, _myShadowMapExtension.getTextureID());

        /* deactivate compare mode */
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_COMPARE_MODE, 0);

        gl.glColor4f(material.color4f().r, material.color4f().g, material.color4f().b, material.color4f().a);
        gl.glDisable(GL.GL_LIGHTING);

        gl.glPushMatrix();

        gl.glTranslatef(position().x, position().y, position().z);
        if (rotation.x != 0.0f) {
            gl.glRotatef( (float) Math.toDegrees(rotation.x), 1, 0, 0);
        }
        if (rotation.y != 0.0f) {
            gl.glRotatef( (float) Math.toDegrees(rotation.y), 0, 1, 0);
        }
        if (rotation.z != 0.0f) {
            gl.glRotatef( (float) Math.toDegrees(rotation.z), 0, 0, 1);
        }

        gl.glScalef(scale.x, scale.y, 1);
        gl.glTranslatef( -0.5f, -0.5f, 0);

        /* draw plane */
        gl.glBegin(GL.GL_QUADS);

        /** @todo why is this negative one? */
        gl.glNormal3f(0, 0, -1);

        if (_myShadowMapExtension.getTextureTargetID() == GL.GL_TEXTURE_RECTANGLE_ARB) {
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(0, 0);

            gl.glTexCoord2f(0, _myShadowMapExtension.getTextureHeight());
            gl.glVertex2f(0, 1);

            gl.glTexCoord2f(_myShadowMapExtension.getTextureWidth(), _myShadowMapExtension.getTextureHeight());
            gl.glVertex2f(1, 1);

            gl.glTexCoord2f(_myShadowMapExtension.getTextureWidth(), 0);
            gl.glVertex2f(1, 0);

        } else {

            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(0, 0);

            gl.glTexCoord2f(0, 1);
            gl.glVertex2f(0, 1);

            gl.glTexCoord2f(1, 1);
            gl.glVertex2f(1, 1);

            gl.glTexCoord2f(1, 0);
            gl.glVertex2f(1, 0);
        }

        gl.glEnd();

        gl.glPopMatrix();

        /* restore texture mode */
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_COMPARE_MODE, GL.GL_COMPARE_R_TO_TEXTURE);
        gl.glTexParameteri(_myTextureTargetID, GL.GL_TEXTURE_COMPARE_FUNC, GL.GL_LEQUAL);

        if (_myShadowMapExtension.enabled) {
            _myShadowMapExtension.enableShadow(gl);
        }

        gl.glDisable(_myTextureTargetID);
    }
}
