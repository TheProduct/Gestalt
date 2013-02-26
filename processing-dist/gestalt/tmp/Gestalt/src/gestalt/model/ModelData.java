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
package gestalt.model;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import mathematik.Vector3f;


public class ModelData
        implements Serializable {

    private static final long serialVersionUID = 9012388058263957788L;

    public static boolean VERBOSE = false;

    public float[] vertices;

    public float[] unsortedvertices;

    public float[] texCoordinates;

    public float[] normals;

    public float[] vertexColors;

    public int[] faces;

    public int primitive;

    public int numberOfObjects;

    public int numberOfVerticesPerFrame;

    public Vector<ModelAnimation> animations;

    public int numberOfVertexComponents;

    public String name;


    public ModelData(float[] theVertices, float[] theUnsortedVertices,
                     float[] theTexCoordinates, float[] theNormals,
                     float[] theVertexColors, int[] theFaces, int thePrimitiv,
                     int theNumberOfVertexComponents, int theNumberOfObjects,
                     String theName) {
        vertices = theVertices;
        unsortedvertices = theUnsortedVertices;
        texCoordinates = theTexCoordinates;
        normals = theNormals;
        vertexColors = theVertexColors;
        primitive = thePrimitiv;
        faces = theFaces;
        numberOfObjects = theNumberOfObjects;
        numberOfVertexComponents = theNumberOfVertexComponents;
        name = theName;

        if (numberOfObjects > 0) {
            numberOfVerticesPerFrame = (theVertices.length / numberOfVertexComponents) / numberOfObjects;
        } else {
            numberOfVerticesPerFrame = 0;
        }

        /* create default animations */
        animations = new Vector<ModelAnimation>();
        animations.add(new ModelAnimation("default", 0, numberOfObjects));
    }


    /*
     * this method calculates the normals in a way that vertices that share the
     * same position will have the same normal.
     *
     * the algorithm is very inefficient, so that really big models will take
     * minutes to be calculated. ideas to increase the speed are very welcome!
     */
    public void averageNormals() {
        System.out.println(
                "### INFO @ ModelData.averageNormals() / this algorithm is not very efficient yet. Takes quite long!");
        for (int i = 0; i < vertices.length; i += numberOfVertexComponents) {
            if (VERBOSE) {
                System.out.println("### INFO @ ModelData.averageNormals(): loaded " + i + " / " + vertices.length);
            }
            Vector3f myVertex = new Vector3f(vertices[i + 0], vertices[i + 1],
                                             vertices[i + 2]);
            Vector<Vector3f> myNormals = new Vector<Vector3f>();

            Vector<Integer> myNormalIndices = new Vector<Integer>();
            for (int j = 0; j < vertices.length; j += numberOfVertexComponents) {
                Vector3f mySecondVertex = new Vector3f(vertices[j + 0],
                                                       vertices[j + 1], vertices[j + 2]);
                Vector3f mySecondNormal = new Vector3f(normals[j + 0],
                                                       normals[j + 1], normals[j + 2]);
                if (myVertex.equals(mySecondVertex)) {
                    myNormals.add(mySecondNormal);
                    myNormalIndices.add(j);
                }
            }

            /* get average normal */
            Vector3f myAverageNormal = new Vector3f();
            for (int k = 0; k < myNormals.size(); k++) {
                myAverageNormal.add(myNormals.get(k));
            }
            myAverageNormal.normalize();

            for (int k = 0; k < myNormalIndices.size(); k++) {
                normals[myNormalIndices.get(k) + 0] = myAverageNormal.x;
                normals[myNormalIndices.get(k) + 1] = myAverageNormal.y;
                normals[myNormalIndices.get(k) + 2] = myAverageNormal.z;
            }
        }
    }


    public void translate(final Vector3f theTranslation) {
        final float[] myTranslation = theTranslation.toArray();
        for (int i = 0; i < vertices.length; i += numberOfVertexComponents) {
            for (int j = 0; j < numberOfVertexComponents; j++) {
                vertices[i + j] += myTranslation[j];
            }
        }
    }


    public void scale(final Vector3f theScale) {
        final float[] myScale = theScale.toArray();
        for (int i = 0; i < vertices.length; i += numberOfVertexComponents) {
            for (int j = 0; j < numberOfVertexComponents; j++) {
                vertices[i + j] *= myScale[j];
            }
        }
    }


    /**
     * writes this modeldata to a file. this way, you can reload
     * modeldata from a file and skip time consuming tasks like averaging
     * normals.
     *
     * @param theFileName
     *            InputStream
     */
    public void serialize(String theFileName) {
        try {
            FileOutputStream myFileOutputStream = new FileOutputStream(theFileName);
            ObjectOutputStream myObjectOutputStream = new ObjectOutputStream(myFileOutputStream);
            myObjectOutputStream.writeObject(this);
            myFileOutputStream.close();
        } catch (IOException e) {
            System.err.println("### ERROR @ ModelData.serialize() / " + e);
        }
    }


    public static ModelData getSerializedModelData(String theFileName) {
        try {
            FileInputStream myFileInputStream = new FileInputStream(theFileName);
            ObjectInputStream myObjectInputStream = new ObjectInputStream(myFileInputStream);
            Object myObject = myObjectInputStream.readObject();
            myObjectInputStream.close();
            ModelData myModelData = (ModelData) myObject;
            return myModelData;
        } catch (IOException e) {
            System.err.println("### ERROR @ ModelData.getSerializedModelData() / " + e);
        } catch (ClassNotFoundException e) {
            System.err.println("### ERROR @ ModelData.getSerializedModelData() / " + e);
        }
        return null;
    }


    public String toString() {
        return "ModelData:" + "\n" + "----------" + "\n" + "objects:        " + numberOfObjects + "\n" + "vertices:       " + vertices.length + "\n" + "normals:        " + normals.length + "\n" + "texCoordinates: " + texCoordinates.length + "\n";
    }
}
