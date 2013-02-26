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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import static gestalt.Gestalt.*;
import gestalt.Gestalt;

import mathematik.Vector3f;


/* the following describes the model concept in gestalt
 *
 * in gestalt we use .obj as fileformat to get models from
 * modelling software like cinema4d. speaking of which, this is
 * the only software we exported models from. we hope, that every
 * other application will export in the same format :)
 * but back to business.
 * obj-files seperate different models in form of groups or objects.
 * you can either display all models at once, or you can treat every
 * object as one frame of an animation. to play an animation, you use
 * the gestalt modelplayer. due to this, you have to care, that every
 * object in you scene has the same amount of vertices.
 */

public class ModelLoaderOBJ {

    public static boolean VERBOSE = false;

    public static int GET_NORMALS_CCW = 0;

    public static int GET_NORMALS_CW = 1;

    public static int GET_NORMALS_DIRECTION = GET_NORMALS_CCW;

    public static int NUMBER_OF_VERTEX_COMPONENTS = 3;

    public static int PRIMITIVE = Gestalt.MESH_TRIANGLES;

    public static ModelData getModelData(InputStream theModelFile) {
        return parseFile(theModelFile);
    }


    public static ModelData[] getModelDataAsDiscreteModels(InputStream theModelFile) {
        ModelData[] myModelData = parseFileSingleModel(theModelFile);
        return myModelData;
    }


    private static ModelData parseFile(InputStream theFilename) {
        InputStreamReader myInputStreamReader = new InputStreamReader(theFilename);
        BufferedReader myBufferedReader = new BufferedReader(myInputStreamReader);
        int myNumberOfObjects = 0;
        String myLine;
        Vector<Float> myTempVertices = new Vector<Float> ();
        Vector<Float> myTempTexCoords = new Vector<Float> ();
        Vector<Float> myTempNormals = new Vector<Float> ();
        Vector<Integer> myTempVertexIndices = new Vector<Integer> ();
        Vector<Integer> myTempTexCoordsIndices = new Vector<Integer> ();
        Vector<Integer> myTempNormalIndices = new Vector<Integer> ();
        Vector<Integer> myGroupChangeIndices = new Vector<Integer> ();
        Vector<String> myNames = new Vector<String> ();
        try {
            while ( (myLine = myBufferedReader.readLine()) != null) {
                java.lang.String myLineElements[] = myLine.split("\\s+");
                if (myLineElements.length > 0) {
                    /* get groups */
                    if (myLineElements[0].equals("g") || myLineElements[0].equals("o")) {
                        if (VERBOSE) {
                            System.out.println("### INFO @ ModelLoaderOBJ.parseFile() / start loading group: " + myLine);
                        }
                        myNumberOfObjects++;
                        myNames.add(myLineElements[1]);
                        myGroupChangeIndices.add(myTempVertices.size());
                    }

                    /* get vertices */
                    if (myLineElements[0].equals("v")) {
                        myTempVertices.add(Float.valueOf(myLineElements[1]));
                        myTempVertices.add(Float.valueOf(myLineElements[2]));
                        myTempVertices.add(Float.valueOf(myLineElements[3]));
                    }

                    /* get texturecoordinates */
                    if (myLineElements[0].equals("vt")) {
                        myTempTexCoords.add(Float.valueOf(myLineElements[1]));
                        myTempTexCoords.add(Float.valueOf(myLineElements[2]));
                    }

                    /* get normals */
                    if (myLineElements[0].equals("vn")) {
                        myTempNormals.add(Float.valueOf(myLineElements[1]));
                        myTempNormals.add(Float.valueOf(myLineElements[2]));
                        myTempNormals.add(Float.valueOf(myLineElements[3]));
                    }

                    /* get indices for vertices and texture coordinates */
                    if (myLineElements[0].equals("f")) {
                        for (int i = 1; i < myLineElements.length; i++) {
                            String myFaceElement = myLineElements[i];
                            String[] myFaceElements = myFaceElement.split("/");
                            if (myFaceElements.length == 1) {
                                myTempVertexIndices.add(Integer.valueOf(myFaceElements[0]));
                            } else if (myFaceElements.length == 2) {
                                myTempVertexIndices.add(Integer.valueOf(myFaceElements[0]));
                                myTempTexCoordsIndices.add(Integer.valueOf(myFaceElements[1]));
                            } else if (myFaceElements.length == 3) {
                                myTempVertexIndices.add(Integer.valueOf(myFaceElements[0]));
                                myTempTexCoordsIndices.add(Integer.valueOf(myFaceElements[1]));
                                myTempNormalIndices.add(Integer.valueOf(myFaceElements[2]));
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("### ERROR @ ModelLoaderOBJ.parseFile() / " + ex);
        }

        /* convert and rearrange vertices depending on indices described in 'f' */
        float[] myVertices = new float[myTempVertices.size()];
        float[] myUnsortedVertices = new float[myTempVertices.size()];
        for (int i = 0; i < myTempVertices.size(); i++) {
            myVertices[i] = ( (Float) (myTempVertices.get(i))).floatValue();
            myUnsortedVertices[i] = ( (Float) (myTempVertices.get(i))).floatValue();
        }
        int[] myFaces = new int[myTempVertexIndices.size()];
        for (int i = 0; i < myTempVertexIndices.size(); i++) {
            myFaces[i] = ( (Integer) (myTempVertexIndices.elementAt(i))).intValue() - 1;
        }
        myVertices = ModelUtil.rearrangeVertices(myVertices, myFaces, 3, new Vector3f(1, 1, 1),
                                                 new Vector3f(0, 0, 0));

        /* convert and rearrange texture coordinates depending on indices described in 'f' */
        float[] myTexCoordinates = new float[myTempTexCoords.size()];
        for (int i = 0; i < myTempTexCoords.size(); i++) {
            myTexCoordinates[i] = ( (Float) (myTempTexCoords.get(i))).floatValue();
        }
        int[] myTexCoordsIndices = new int[myTempTexCoordsIndices.size()];
        for (int i = 0; i < myTempTexCoordsIndices.size(); i++) {
            myTexCoordsIndices[i] = (int) ( (Integer) (myTempTexCoordsIndices.elementAt(i))).intValue() - 1;
        }
        myTexCoordinates = ModelUtil.rearrangeVertices(myTexCoordinates, myTexCoordsIndices, 2, new Vector3f(1, 1, 1),
                                                       new Vector3f(0, 0, 0));

        /* convert and rearrange normals depending on indices described in 'f'
         * if there are no normals specified in the file then we
         * create normals on our own.
         */
        float[] myNormals = null;
        if (myTempNormals.size() > 0) {
            myNormals = new float[myTempNormals.size()];
            for (int i = 0; i < myTempNormals.size(); i++) {
                myNormals[i] = ( (Float) (myTempNormals.get(i))).floatValue();
            }
            int[] myNormalsIndices = new int[myTempNormalIndices.size()];
            for (int i = 0; i < myTempNormalIndices.size(); i++) {
                myNormalsIndices[i] = (int) ( (Integer) (myTempNormalIndices.elementAt(i))).intValue() - 1;
            }
            myNormals = ModelUtil.rearrangeVertices(myNormals, myNormalsIndices, 3, new Vector3f(1, 1, 1),
                                                    new Vector3f(0, 0, 0));
        } else {
            myNormals = new float[myVertices.length];
            if (PRIMITIVE == MESH_TRIANGLES) {
                createNormalsTRIANGLE(myVertices, myNormals);
            } else if (PRIMITIVE == MESH_QUADS) {
                createNormalsQUADS(myVertices, myNormals);
            } else {
                System.out.println(
                    "### WARNING @ ModelLoaderOBJ / normal autogenerator for this primitive isn t implemented yet");
            }
        }

        /* create modeldata */
        if (myNumberOfObjects == 0) {
            myNumberOfObjects = 1;
        }

        float[] myVertexColors = null; // the obj format doesn t store vertex colors :(
        ModelData myModelData = new ModelData(myVertices,
                                              myUnsortedVertices,
                                              myTexCoordinates,
                                              myNormals,
                                              myVertexColors,
                                              myFaces,
                                              PRIMITIVE,
                                              NUMBER_OF_VERTEX_COMPONENTS,
                                              myNumberOfObjects,
                                              myNames.isEmpty() ? "" : myNames.get(0));

        /* print info */
        if (VERBOSE) {
            System.out.println(myModelData);
        }
        return myModelData;
    }


    private static ModelData[] parseFileSingleModel(InputStream theFilename) {
        InputStreamReader myInputStreamReader = new InputStreamReader(theFilename);
        BufferedReader myBufferedReader = new BufferedReader(myInputStreamReader);
        int myNumberOfObjects = 0;
        String myLine;
        Vector<Float> myTempVertices = new Vector<Float> ();
        Vector<Float> myTempTexCoords = new Vector<Float> ();
        Vector<Float> myTempNormals = new Vector<Float> ();
        Vector<Integer> myTempVertexIndices = new Vector<Integer> ();
        Vector<Integer> myTempTexCoordsIndices = new Vector<Integer> ();
        Vector<Integer> myTempNormalIndices = new Vector<Integer> ();
        Vector<Integer> myGroupChangeIndices = new Vector<Integer> ();
        Vector<String> myNames = new Vector<String> ();
        Vector<ModelData> myModelDatas = new Vector<ModelData> ();

        float[] myVertices;
        float[] myTexCoordinates;
        float[] myNormals;
        float[] myUnsortedVertices;
        int[] myFaces;
        int myIndexOffset = 0;
        int myTexIndexOffset = 0;
        int myNormalIndexOffset = 0;

        try {
            while ( (myLine = myBufferedReader.readLine()) != null) {
                java.lang.String myLineElements[] = myLine.split("\\s+");
                if (myLineElements.length > 0) {
                    /* get groups */
                    if (myLineElements[0].equals("g") || myLineElements[0].equals("o")) {
                        if (VERBOSE) {
                            System.out.println("### INFO @ ModelLoaderOBJ.parseFile() / start loading group: " + myLine);
                        }

                        if (myNumberOfObjects > 0) {
                            /* convert and rearrange vertices depending on indices described in 'f' */
                            myUnsortedVertices = new float[myTempVertices.size()];
                            myFaces = new int[myTempVertexIndices.size()];
                            myVertices = distributeVertices(myTempVertices,
                                                            myTempVertexIndices,
                                                            myUnsortedVertices,
                                                            myFaces,
                                                            myIndexOffset);

                            /* convert and rearrange texcoordinates depending on indices described in 'f' */
                            myTexCoordinates = distributeTexCoordinates(myTempTexCoords,
                                                                        myTempTexCoordsIndices,
                                                                        myTexIndexOffset);

                            /* convert and rearrange normals depending on indices described in 'f'
                             * if there are no normals specified in the file then we
                             * create normals on our own.
                             */
                            if (myTempNormals.size() > 0) {
                                myNormals = distributeNormals(myTempNormals,
                                                              myTempNormalIndices,
                                                              myNormalIndexOffset,
                                                              myVertices);
                            } else {
                                myNormals = new float[myVertices.length];
                                if (PRIMITIVE == MESH_TRIANGLES) {
                                    createNormalsTRIANGLE(myVertices, myNormals);
                                } else if (PRIMITIVE == MESH_QUADS) {
                                    createNormalsQUADS(myVertices, myNormals);
                                } else {
                                    System.out.println(
                                        "### WARNING @ ModelLoaderOBJ / normal autogenerator for this primitive isn t implemented yet");
                                }
                            }

                            float[] myVertexColors = null; // the obj format doesn t store vertex colors :(
                            ModelData myModelData = new ModelData(myVertices,
                                                                  myUnsortedVertices,
                                                                  myTexCoordinates,
                                                                  myNormals,
                                                                  myVertexColors,
                                                                  myFaces,
                                                                  PRIMITIVE,
                                                                  NUMBER_OF_VERTEX_COMPONENTS,
                                                                  1,
                                                                  myNames.get(0));
                            myModelDatas.add(myModelData);

                            /* print info */
                            if (VERBOSE) {
                                System.out.println(myModelData);
                            }

                            /* reset collections */
                            myIndexOffset += myTempVertices.size() / NUMBER_OF_VERTEX_COMPONENTS;
                            myTexIndexOffset += myTempTexCoords.size() / 2;
                            myNormalIndexOffset += myTempNormals.size() / 3;
                            myTempVertices = new Vector<Float> ();
                            myTempTexCoords = new Vector<Float> ();
                            myTempNormals = new Vector<Float> ();
                            myTempVertexIndices = new Vector<Integer> ();
                            myTempTexCoordsIndices = new Vector<Integer> ();
                            myTempNormalIndices = new Vector<Integer> ();
                            myNames = new Vector<String> ();
                        }

                        myNumberOfObjects++;

                        if (myLineElements.length == 1) {
                            myNames.add("(default)");
                        } else
                        if (myLineElements.length == 2) {
                            myNames.add(myLineElements[1]);
                        } else
                        if (myLineElements.length > 2) {
                            StringBuffer myNameSegments = new StringBuffer();
                            myNameSegments.append(myLineElements[1]);
                            for (int i = 2; i < myLineElements.length; i++) {
                                myNameSegments.append("/");
                                myNameSegments.append(myLineElements[i]);
                            }
                            myNames.add(myNameSegments.toString());
                        }
                        myGroupChangeIndices.add(myTempVertices.size());
                    }

                    /* get vertices */
                    if (myLineElements[0].equals("v")) {
                        myTempVertices.add(Float.valueOf(myLineElements[1]));
                        myTempVertices.add(Float.valueOf(myLineElements[2]));
                        myTempVertices.add(Float.valueOf(myLineElements[3]));
                    }

                    /* get texturecoordinates */
                    if (myLineElements[0].equals("vt")) {
                        myTempTexCoords.add(Float.valueOf(myLineElements[1]));
                        myTempTexCoords.add(Float.valueOf(myLineElements[2]));
                    }

                    /* get normals */
                    if (myLineElements[0].equals("vn")) {
                        myTempNormals.add(Float.valueOf(myLineElements[1]));
                        myTempNormals.add(Float.valueOf(myLineElements[2]));
                        myTempNormals.add(Float.valueOf(myLineElements[3]));
                    }

                    /* get indices for vertices and texture coordinates */
                    if (myLineElements[0].equals("f")) {
                        for (int i = 1; i < myLineElements.length; i++) {
                            String myFaceElement = myLineElements[i];
                            String[] myFaceElements = myFaceElement.split("/");
                            if (myFaceElements.length == 1) {
                                myTempVertexIndices.add(Integer.valueOf(myFaceElements[0]));
                            } else if (myFaceElements.length == 2) {
                                myTempVertexIndices.add(Integer.valueOf(myFaceElements[0]));
                                myTempTexCoordsIndices.add(Integer.valueOf(myFaceElements[1]));
                            } else if (myFaceElements.length == 3) {
                                myTempVertexIndices.add(Integer.valueOf(myFaceElements[0]));
                                myTempTexCoordsIndices.add(Integer.valueOf(myFaceElements[1]));
                                myTempNormalIndices.add(Integer.valueOf(myFaceElements[2]));
                            }
                        }
                    }
                }
            }
            /* convert and rearrange vertices depending on indices described in 'f' */
            myUnsortedVertices = new float[myTempVertices.size()];
            myFaces = new int[myTempVertexIndices.size()];
            myVertices = distributeVertices(myTempVertices,
                                            myTempVertexIndices,
                                            myUnsortedVertices,
                                            myFaces,
                                            myIndexOffset);

            /* convert and rearrange texcoordinates depending on indices described in 'f' */
            myTexCoordinates = distributeTexCoordinates(myTempTexCoords,
                                                        myTempTexCoordsIndices,
                                                        myTexIndexOffset);

            /* convert and rearrange normals depending on indices described in 'f'
             * if there are no normals specified in the file then we
             * create normals on our own.
             */
            if (myTempNormals.size() > 0) {
                myNormals = distributeNormals(myTempNormals,
                                              myTempNormalIndices,
                                              myNormalIndexOffset,
                                              myVertices);
            } else {
                myNormals = new float[myVertices.length];
                if (PRIMITIVE == MESH_TRIANGLES) {
                    createNormalsTRIANGLE(myVertices, myNormals);
                } else if (PRIMITIVE == MESH_QUADS) {
                    createNormalsQUADS(myVertices, myNormals);
                } else {
                    System.out.println(
                        "### WARNING @ ModelLoaderOBJ / normal autogenerator for this primitive isn t implemented yet");
                }
            }

            float[] myVertexColors = null; // the obj format doesn t store vertex colors :(
            ModelData myModelData = new ModelData(myVertices,
                                                  myUnsortedVertices,
                                                  myTexCoordinates,
                                                  myNormals,
                                                  myVertexColors,
                                                  myFaces,
                                                  PRIMITIVE,
                                                  NUMBER_OF_VERTEX_COMPONENTS,
                                                  1,
                                                  myNames.get(0));
            myModelDatas.add(myModelData);

            /* print info */
            if (VERBOSE) {
                System.out.println(myModelData);
            }
        } catch (IOException ex) {
            System.err.println("### ERROR @ ModelLoaderOBJ.parseFile() / " + ex);
        }

        ModelData[] myDatas = new ModelData[myModelDatas.size()];
        for (int i = 0; i < myModelDatas.size(); i++) {
            myDatas[i] = myModelDatas.get(i);
        }
        return myDatas;
    }


    private static float[] distributeVertices(Vector<Float> theTempVertices,
                                              Vector<Integer> theTempVertexIndices,
                                              float[] theUnsortedVertices,
                                              int[] theFaces,
                                              int theIndexOffset) {
        float[] theVertices = new float[theTempVertices.size()];
        for (int i = 0; i < theTempVertices.size(); i++) {
            theVertices[i] = theTempVertices.get(i);
            theUnsortedVertices[i] = theTempVertices.get(i);
        }
        for (int i = 0; i < theTempVertexIndices.size(); i++) {
            theFaces[i] = ( (Integer) (theTempVertexIndices.elementAt(i))).intValue() - 1 - theIndexOffset;
        }
        theVertices = ModelUtil.rearrangeVertices(theVertices, theFaces, 3, new Vector3f(1, 1, 1),
                                                  new Vector3f(0, 0, 0));

        return theVertices;
    }


    private static float[] distributeTexCoordinates(Vector<Float> theTempTexCoords,
                                                    Vector<Integer> theTempTexCoordsIndices,
                                                    int theIndexOffset) {
        float[] myTexCoordinates = new float[theTempTexCoords.size()];
        for (int i = 0; i < theTempTexCoords.size(); i++) {
            myTexCoordinates[i] = theTempTexCoords.get(i);
        }
        int[] myTexCoordsIndices = new int[theTempTexCoordsIndices.size()];
        for (int i = 0; i < theTempTexCoordsIndices.size(); i++) {
            myTexCoordsIndices[i] = (int) ( (Integer) (theTempTexCoordsIndices.elementAt(i))).
                                    intValue() - 1 - theIndexOffset;
        }
        myTexCoordinates = ModelUtil.rearrangeVertices(myTexCoordinates, myTexCoordsIndices, 2,
                                                       new Vector3f(1, 1, 1),
                                                       new Vector3f(0, 0, 0));
        return myTexCoordinates;
    }


    private static float[] distributeNormals(Vector<Float> theTempNormals,
                                             Vector<Integer> theTempNormalIndices,
                                             int theIndexOffset,
                                             float[] theVertices) {
        float[] myNormals = null;
        if (theTempNormals.size() > 0) {
            myNormals = new float[theTempNormals.size()];
            for (int i = 0; i < theTempNormals.size(); i++) {
                myNormals[i] = ( (Float) (theTempNormals.get(i))).floatValue();
            }
            int[] myNormalsIndices = new int[theTempNormalIndices.size()];

            for (int i = 0; i < theTempNormalIndices.size(); i++) {
                myNormalsIndices[i] = (int) ( (Integer) (theTempNormalIndices.elementAt(i))).intValue() -
                                      1 - theIndexOffset;
            }
            myNormals = ModelUtil.rearrangeVertices(myNormals, myNormalsIndices, 3,
                                                    new Vector3f(1, 1, 1),
                                                    new Vector3f(0, 0, 0));
        } else {
            myNormals = new float[theVertices.length];
            if (PRIMITIVE == MESH_TRIANGLES) {
                createNormalsTRIANGLE(theVertices, myNormals);
            } else if (PRIMITIVE == MESH_QUADS) {
                createNormalsQUADS(theVertices, myNormals);
            } else {
                System.out.println(
                    "### WARNING @ ModelLoaderOBJ / normal autogenerator for this primitive isn t implemented yet");
            }
        }
        return myNormals;
    }


    public static void createNormalsTRIANGLE(float[] theVertices, float[] theNormals) {
        int myNumberOfPoints = 3;
        for (int i = 0; i < theVertices.length;
                     i += (myNumberOfPoints * NUMBER_OF_VERTEX_COMPONENTS)) {
            Vector3f a = new Vector3f(theVertices[i], theVertices[i + 1], theVertices[i + 2]);
            Vector3f b = new Vector3f(theVertices[i + 3], theVertices[i + 4], theVertices[i + 5]);
            Vector3f c = new Vector3f(theVertices[i + 6], theVertices[i + 7], theVertices[i + 8]);
            Vector3f myNormal = new Vector3f();
            if (GET_NORMALS_DIRECTION == GET_NORMALS_CCW) {
                mathematik.Util.calculateNormal(a, b, c, myNormal);
            } else if (GET_NORMALS_DIRECTION == GET_NORMALS_CW) {
                mathematik.Util.calculateNormal(b, a, c, myNormal);
            }

            theNormals[i + 0] = myNormal.x;
            theNormals[i + 1] = myNormal.y;
            theNormals[i + 2] = myNormal.z;

            theNormals[i + 3] = myNormal.x;
            theNormals[i + 4] = myNormal.y;
            theNormals[i + 5] = myNormal.z;

            theNormals[i + 6] = myNormal.x;
            theNormals[i + 7] = myNormal.y;
            theNormals[i + 8] = myNormal.z;
        }
    }


    private static void createNormalsQUADS(float[] theVertices, float[] theNormals) {
        System.out.println("### WARNING @ ModelLoaderOBJ / normal autogenerator for QUADS isn t implemented yet");
    }
}
