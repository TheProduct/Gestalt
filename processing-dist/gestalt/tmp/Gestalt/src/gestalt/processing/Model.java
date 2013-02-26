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


package gestalt.processing;

import gestalt.model.ModelData;
import gestalt.material.Material;
import gestalt.shape.Mesh;
import gestalt.material.TexturePlugin;

import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class Model
        extends gestalt.model.Model {

    public Model(ModelData theModelData, Mesh theModelView) {
        super(theModelData, theModelView);
    }

    public Vector3f position() {
        return _myModelView.position();
    }

    public Vector3f rotation() {
        return _myModelView.rotation();
    }

    public Vector3f scale() {
        return _myModelView.scale();
    }

    public TransformMatrix4f transform() {
        return _myModelView.transform();
    }

    public TexturePlugin texture() {
        return _myModelView.material().texture();
    }

    public Material material() {
        return _myModelView.material();
    }
}
