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


package gestalt.extension.gpgpu.particles;


import data.Resource;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;


public abstract class AbstractParticleResetter {

    protected ShaderManager _myShaderManager;

    protected ShaderProgram _myShaderProgram;

    public AbstractParticleResetter(ShaderManager theShaderManager,
                                    ShaderProgram theShaderProgram,
                                    String theShader) {
        _myShaderManager = theShaderManager;
        _myShaderProgram = theShaderProgram;
        _myShaderManager.attachFragmentShader(_myShaderProgram,
                                              Resource.getStream(theShader));
    }

    public abstract void draw();
}
