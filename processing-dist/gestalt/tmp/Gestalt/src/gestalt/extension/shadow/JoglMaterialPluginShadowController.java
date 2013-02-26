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
import gestalt.material.Material;
import gestalt.material.MaterialPlugin;


/**
 * this plugin enables single material to controll whether they
 * receive shadows or not.
 * in real life this plugin should just be used occassionally
 * as the enabling and disabling process can be quite expensive.
 */

public class JoglMaterialPluginShadowController
    implements MaterialPlugin {

    private boolean _myIsActive;

    private boolean _myPreviousState;

    private final JoglShadowMap _myShadowMapExtension;

    public JoglMaterialPluginShadowController(JoglShadowMap theShadowMapExtension) {
        _myShadowMapExtension = theShadowMapExtension;
        _myIsActive = true;
    }


    public boolean isEnabled() {
        return _myIsActive;
    }


    public void enableShadow() {
        _myIsActive = true;
    }


    public void disableShadow() {
        _myIsActive = false;
    }


    public void begin(GLContext theRenderContext, Material theParent) {
        /* set state if different from previous state */
        final GL gl = (  theRenderContext).gl;
        _myPreviousState = _myShadowMapExtension.isShadowEnabled();
        if (_myPreviousState != _myIsActive) {
            if (_myIsActive) {
                _myShadowMapExtension.enableShadow(gl);
            } else {
                _myShadowMapExtension.disableShadow(gl);
            }
        }
    }


    public void end(GLContext theRenderContext,
                    final Material theParent) {
        /* restore previous state */
        final GL gl = (  theRenderContext).gl;
        if (_myPreviousState != _myIsActive) {
            if (_myPreviousState) {
                _myShadowMapExtension.enableShadow(gl);
            } else {
                _myShadowMapExtension.disableShadow(gl);
            }
        }
    }
}
