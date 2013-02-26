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


package gestalt.extension.quadline;


import java.io.Serializable;

import gestalt.material.Color;

import mathematik.Vector2f;
import mathematik.Vector3f;


public class QuadFragment
    implements Serializable {

    public final Vector3f pointA;

    public final Vector3f pointB;

    public final Vector2f texcoordA;

    public final Vector2f texcoordB;

    public Color colorA;

    public Color colorB;

    public final Vector3f normal;

    public QuadFragment() {
        pointA = new Vector3f();
        pointB = new Vector3f();
        texcoordA = new Vector2f();
        texcoordB = new Vector2f();
//        colorA = new Color();
//        colorB = new Color();
        normal = new Vector3f();
    }
}
