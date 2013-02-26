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


package gestalt.demo.basic;


import gestalt.G;
import gestalt.render.AnimatorRenderer;
import gestalt.render.controller.cameraplugins.ArcBall;
import gestalt.shape.Cuboid;
import gestalt.shape.Plane;


public class UsingArcBall
        extends AnimatorRenderer {

    private ArcBall _myArcball;

    public void setup() {
        /* create an arcball plugin and set it s properties */
        _myArcball = new ArcBall();
        _myArcball.radius(height);
        _myArcball.center().set(0, 0, 0); /* seems to be a bit buggy still... */

        /* add plugin to current camera */
        camera().plugins().add(_myArcball);

        Cuboid a = G.cuboid();
        a.scale(100, 100, 100);
        a.position().set(0, 0, 0);
        a.material().transparent = false;

        Cuboid b = G.cuboid();
        b.scale(50, 50, 50);
        b.position().set(0, 100, 0);
        b.material().transparent = false;

        Cuboid c = G.cuboid();
        c.scale(50, 50, 50);
        c.position().set(100, 0, 0);
        c.material().transparent = false;

        Plane p = G.plane();
        p.scale(width, height);
        p.material().color4f().set(0, 0.5f, 1.0f, 0.25f);
    }

    public static void main(String[] args) {
        G.init(UsingArcBall.class);
    }
}
