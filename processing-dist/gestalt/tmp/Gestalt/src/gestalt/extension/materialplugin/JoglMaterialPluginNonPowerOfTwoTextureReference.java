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


package gestalt.extension.materialplugin;


public class JoglMaterialPluginNonPowerOfTwoTextureReference
        extends JoglMaterialPluginNonPowerOfTwoTexture {

    public JoglMaterialPluginNonPowerOfTwoTextureReference(final boolean theHintFlipYAxis) {
        super(theHintFlipYAxis);
    }

    private int _myPixelHeight = 0;

    private int _myPixelWidth = 0;

    public int getPixelWidth() {
        return _myPixelWidth;
    }

    public int getPixelHeight() {
        return _myPixelHeight;
    }

    public void setPixelWidth(int thePixelWidth) {
        _myPixelWidth = thePixelWidth;
    }

    public void setPixelHeight(int thePixelHeight) {
        _myPixelHeight = thePixelHeight;
    }
}
