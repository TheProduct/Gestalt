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


import java.io.File;
import java.io.IOException;

import java.awt.image.BufferedImage;

import gestalt.Gestalt;
import gestalt.render.Drawable;
import gestalt.material.Color;
import gestalt.shape.Mesh;
import gestalt.material.texture.bitmap.ByteBitmap;
import gestalt.util.ImageUtil;

import mathematik.TransformMatrix4f;
import mathematik.Vector2f;
import mathematik.Vector3f;


public class MeshTranslator
        implements DrawableOBJTranslator {

    public boolean isClass(Drawable theDrawable) {
        return theDrawable instanceof Mesh;
    }

    public void parse(SceneWriter theParent, Drawable theDrawable) {
        final Mesh theMesh = (Mesh)theDrawable;
        parseMesh(theParent, theMesh);
    }

    protected static void parseMaterial(SceneWriter theParent, Mesh theMesh) {
        theParent.parseMaterial(theMesh.material());
    }

    public static void parseMesh(SceneWriter theParent, Mesh theMesh) {
        /* write unique header */
        int myUniqueID = Gestalt.UNDEFINED;
        if (!SceneWriter.IGNORE_OBJECTS) {
            myUniqueID = theParent.bumpUniqueObjectID();
            theParent.writeGroupTag("mesh" + myUniqueID);
        }

        /* write material */
        parseMaterial(theParent, theMesh);

        /* save vertexcolors as texture */
        if (theParent.saveVertexColorsAsTexture() && myUniqueID != Gestalt.UNDEFINED) {
            writeTexture(theMesh, theParent.getTextureDirectory(), myUniqueID);
        }

        sendMesh(theParent, theMesh);
    }

    public static void sendMesh(final SceneWriter theParent,
                                final Mesh theMesh) {
        /* apply transform */
        final TransformMatrix4f myTransform = mathematik.Util.getTranslateRotationTransform(theMesh.getTransformMode(),
                                                                                            theMesh.transform(),
                                                                                            theMesh.rotation(),
                                                                                            theMesh.scale());

        /* -- vertex data -- */

        /* write vertices -- 'v' */
        for (int i = 0; i < theMesh.vertices().length;
                i += theMesh.getNumberOfVertexComponents()) {
            /** @todo we assume for now that we always have 3 vertex components. */
            if (theMesh.getNumberOfVertexComponents() != 3) {
                System.out.println(
                        "### WARNING / use 3 vertex components only.");
            }

            final Vector3f myPosition = new Vector3f(theMesh.vertices()[i + 0],
                                                     theMesh.vertices()[i + 1],
                                                     theMesh.vertices()[i + 2]);

            myTransform.transform(myPosition);

            theParent.writeVertex(myPosition);
        }
        theParent.println();

        /* write texture coordiantes -- 'vt' */
        if (theMesh.texcoords() != null) {
            for (int i = 0; i < theMesh.texcoords().length; i += theMesh.getNumberOfTexCoordComponents()) {
                /* transform texture coords */
                /** @todo rotation is missing */
                final Vector2f myTexCoord = new Vector2f(theMesh.texcoords()[i + 0],
                                                         theMesh.texcoords()[i + 1]);
                if (theMesh.material() != null &&
                        theMesh.material().texture() != null) {
                    final Vector2f myTexScale = new Vector2f(theMesh.material().texture().scale().x,
                                                             theMesh.material().texture().scale().y);
                    myTexScale.scale(theMesh.material().texture().nonpoweroftwotexturerescale());
                    myTexCoord.scale(myTexScale);
                    final Vector2f myTexPosition = theMesh.material().texture().position();
                    myTexCoord.add(myTexPosition);
                }
                theParent.writeTextureCoordinates(myTexCoord);
            }
        }

        /* write vertex normals -- 'vn' */
        if (theMesh.normals() != null) {
            for (int i = 0; i < theMesh.normals().length; i += 3) {
                final Vector3f myNormal = new Vector3f(theMesh.normals()[i + 0],
                                                       theMesh.normals()[i + 1],
                                                       theMesh.normals()[i + 2]);
                myTransform.transform(myNormal);
                theParent.writeNormal(myNormal);
            }
        }

        /* -- elements -- */

        /* write faces -- 'f' ( this is the only element cinema4d supports. ) */
        /* 'f v/vt/vn v/vt/vn v/vt/vn v/vt/vn' */
        final int myShape;
        if (theMesh.getPrimitive() == Gestalt.MESH_QUADS) {
            myShape = 4;
        } else if (theMesh.getPrimitive() == Gestalt.MESH_TRIANGLES) {
            myShape = 3;
        } else {
            System.out.println("### WARNING / use quads and triangles only.");
            myShape = 3;
        }

        for (int i = 0; i < theMesh.vertices().length / theMesh.getNumberOfVertexComponents(); i += myShape) {
            theParent.print(SceneWriter.FACE);
            theParent.print(SceneWriter.DELIMITER);
            for (int j = 0; j < myShape; j++) {
                theParent.print(theParent.bumpVertexCounter());
                theParent.print(SceneWriter.FACE_DELIMITER);
                theParent.print(theParent.bumpTextureCoordinateCounter());
                theParent.print(SceneWriter.FACE_DELIMITER);
                theParent.print(theParent.bumpNormalVertexCounter());
                theParent.print(SceneWriter.DELIMITER);
            }
            theParent.println();
        }
        theParent.println();
    }

    private static void writeTexture(Mesh theMesh, String theFileName, int theID) {
        /* define the number of vertices per shape */
        int myNumberOfVerticesPerShape = 4;
        if (theMesh.getPrimitive() == Gestalt.MESH_QUADS) {
            myNumberOfVerticesPerShape = 4;
        } else if (theMesh.getPrimitive() == Gestalt.MESH_TRIANGLES) {
            myNumberOfVerticesPerShape = 3;
        } else {
            System.out.println("### WARNING @ MeshTranslator.writeTexture() / primitive unsupported yet. sorry!");
            return;
        }
        int myNumberOfShapes = (theMesh.colors().length /
                theMesh.getNumberOfColorComponents()) / myNumberOfVerticesPerShape;

        /* create bitmap */
        int myBitmapWidth = myNumberOfShapes * 2;
        ByteBitmap myBitmap = ByteBitmap.getDefaultImageBitmap(myBitmapWidth, 2);

        for (int i = 0; i < theMesh.colors().length;
                i += theMesh.getNumberOfColorComponents()) {
            /* create color4f for the pixel */
            Color myPixel = new Color();
            myPixel.r = theMesh.colors()[i + 0];
            myPixel.g = theMesh.colors()[i + 1];
            myPixel.b = theMesh.colors()[i + 2];
            if (theMesh.getNumberOfColorComponents() == 4) {
                myPixel.a = theMesh.colors()[i + 3];
            } else {
                myPixel.a = 1f;
            }

            /* vertex info */
            int myCurrentVertex = i / theMesh.getNumberOfColorComponents();
            int myVertexInShape = (i / theMesh.getNumberOfColorComponents()) % myNumberOfVerticesPerShape;

            /* tex coords info */
            int myTexCoordsIndex = myCurrentVertex * theMesh.getNumberOfTexCoordComponents();
            int myOffset = i / (theMesh.getNumberOfColorComponents() * myNumberOfVerticesPerShape) * 2;
            float myTexCoordsUnit = 1f / myBitmapWidth;
            float myTexCoordsOffset = myTexCoordsUnit / 2f;

            /* assign color4f of pixel and texcoords */
            if (myVertexInShape == 0) {
                myBitmap.setPixel(0 + myOffset,
                                  1,
                                  myPixel);
                theMesh.texcoords()[myTexCoordsIndex] = myTexCoordsUnit * myOffset + myTexCoordsOffset;
                theMesh.texcoords()[myTexCoordsIndex + 1] = 0.25f;
            } else if (myVertexInShape == 1) {
                myBitmap.setPixel(1 + myOffset,
                                  1,
                                  myPixel);
                theMesh.texcoords()[myTexCoordsIndex] = myTexCoordsUnit * myOffset + myTexCoordsUnit +
                        myTexCoordsOffset;
                theMesh.texcoords()[myTexCoordsIndex + 1] = 0.25f;
            } else if (myVertexInShape == 2) {
                myBitmap.setPixel(1 + myOffset,
                                  0,
                                  myPixel);
                theMesh.texcoords()[myTexCoordsIndex] = myTexCoordsUnit * myOffset + myTexCoordsUnit +
                        myTexCoordsOffset;
                theMesh.texcoords()[myTexCoordsIndex + 1] = 0.75f;
            } else if (myVertexInShape == 3) {
                myBitmap.setPixel(0 + myOffset,
                                  0,
                                  myPixel);
                theMesh.texcoords()[myTexCoordsIndex] = myTexCoordsUnit * myOffset + myTexCoordsOffset;
                theMesh.texcoords()[myTexCoordsIndex + 1] = 0.75f;
            }

        }

        /* write the imagefile */
        try {
            final String myFileName = theFileName + "/meshvertexcolors_" + theID + ".png";
            File file = new File(myFileName);
            BufferedImage myBufferedImage = ImageUtil.convertByteBitmap2BufferedImage(myBitmap);
            javax.imageio.ImageIO.write(myBufferedImage, "png", file);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }
}
