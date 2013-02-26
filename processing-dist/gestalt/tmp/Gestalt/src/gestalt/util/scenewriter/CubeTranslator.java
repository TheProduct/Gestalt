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


package gestalt.util.scenewriter;


import gestalt.render.Drawable;
import gestalt.shape.Cuboid;
import gestalt.shape.Mesh;
import gestalt.util.JoglUtil;


public class CubeTranslator
    implements DrawableOBJTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Cuboid;
    }


    public void parse(SceneWriter theParent,
                      Drawable theDrawable) {
        final Cuboid myCube = (Cuboid) theDrawable;
        final Mesh myMesh = JoglUtil.getCubeMesh();
        myMesh.setPositionRef(myCube.position());
        myMesh.setRotationRef(myCube.rotation());
        myMesh.setScaleRef(myCube.scale());
        myMesh.setMaterialRef(myCube.material());
        myMesh.setTransformRef(myCube.transform());
        MeshTranslator.parseMesh(theParent, myMesh);
    }
}
