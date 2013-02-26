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


package gestalt.shape;


import gestalt.material.Material;
import gestalt.Gestalt;
import gestalt.context.Display;
import gestalt.context.DisplayCapabilities;
import gestalt.context.JoglDisplay;
import gestalt.extension.ExtensionFactory;
import gestalt.input.EventHandler;
import gestalt.model.Model;
import gestalt.model.ModelData;
import gestalt.render.Drawable;
import gestalt.render.MinimalRenderer;
import gestalt.render.controller.OrthoFinish;
import gestalt.render.controller.OrthoSetup;
import gestalt.render.controller.Bin3DFinish;
import gestalt.render.controller.Camera;
import gestalt.render.controller.cameraplugins.Fog;
import gestalt.render.controller.FrameFinish;
import gestalt.render.controller.FrameSetup;
import gestalt.render.controller.cameraplugins.Light;
import gestalt.render.controller.Origin;
import gestalt.material.TexturePlugin;


public class DrawableFactory {

    private DrawableFactory() {
    }

    private static DrawableFactory getFactory(int theVariant) {
        DrawableFactory shapeFactory = null;
        switch (theVariant) {
            case Gestalt.ENGINE_JOGL:
                shapeFactory = new DrawableFactory();
                break;

            default:
                System.err.println("### ERROR @ ShapeFactory / engine not supported: " + theVariant);
        }
        return shapeFactory;
    }

    public static DrawableFactory getDefaultFactory() {
        return getFactory(Gestalt.ENGINE_JOGL);
    }

    public static DrawableFactory getFactory() {
        return getDefaultFactory();
    }

    /* shapes */
    public Plane plane() {
        return new Plane();
    }

    public Cuboid cuboid() {
        return new Cuboid();
    }

    public Line line() {
        return new Line();
    }

    public Disk disk() {
        return new Disk();
    }

    public Sphere sphere() {
        return new Sphere();
    }

    public Mesh mesh(boolean useVBO,
                     float[] theVertices,
                     int theVertexComponents,
                     float[] theColors,
                     int theColorComponents,
                     float[] theTexCoords,
                     int theTexCoordComponents,
                     float[] theNormals,
                     int thePrimitive) {
        if (useVBO) {
            return new MeshVBO(theVertices,
                               theVertexComponents,
                               theColors,
                               theColorComponents,
                               theTexCoords,
                               theTexCoordComponents,
                               theNormals,
                               thePrimitive);
        } else {
            return new Mesh(theVertices,
                            theVertexComponents,
                            theColors,
                            theColorComponents,
                            theTexCoords,
                            theTexCoordComponents,
                            theNormals,
                            thePrimitive);
        }
    }

    public TransformNode transformnode() {
        return new TransformNode();
    }

    public Quad quad() {
        return new Quad();
    }

    public Quads quads() {
        return new Quads();
    }

    public Triangle triangle() {
        return new Triangle();
    }

    public Triangles triangles() {
        return new Triangles();
    }


    /* controller */
    public Drawable orthoSetup() {
        return new OrthoSetup();
    }

    public Drawable orthoFinish() {
        return new OrthoFinish();
    }


    /* plugins */
    public Camera camera() {
        return new Camera();
    }

    public Fog fog() {
        return new Fog();
    }

    public FrameSetup frameSetup() {
        return new FrameSetup();
    }

    public FrameFinish frameFinish() {
        return new FrameFinish();
    }

    public Bin3DFinish bin3DFinish() {
        return new Bin3DFinish();
    }

    public Light light() {
        return new Light();
    }

    public Origin origin() {
        return new Origin();
    }


    /* extension */
    public ExtensionFactory extensions() {
        return new ExtensionFactory();
    }


    /* core */
    public EventHandler eventhandler() {
        return new EventHandler();
    }

    public Display display(DisplayCapabilities theDisplayCapabilities,
                           MinimalRenderer theResource,
                           EventHandler theEventHandler) {
        return new JoglDisplay(theDisplayCapabilities,
                               theResource,
                               theEventHandler);
    }


    /* material plugins */
    public Material material() {
        return new Material();
    }

    public TexturePlugin texture() {
        return new TexturePlugin(true);
    }

    public Model model(ModelData theModelData, Mesh theModelView) {
        return new Model(theModelData, theModelView);
    }
}
