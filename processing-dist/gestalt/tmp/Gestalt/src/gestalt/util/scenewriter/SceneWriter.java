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


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.render.controller.Camera;
import gestalt.material.Material;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import mathematik.TransformMatrix4f;
import mathematik.Vector2f;
import mathematik.Vector3f;


/**
 * for reference to the model see the <a href="http://www.csit.fsu.edu/~burkardt/txt/obj_format.txt">OBJ specification</a>. <br/>
 * for reference to the material see the<a href="http://www.fileformat.info/format/material/">MTL specification</a>.
 */
public class SceneWriter {

    private PrintStream _myPrintStream;

    private static int _myUniqueObjectID;

    private static int _myUniqueMaterialID;

    private int _myVertexCounter;

    private int _myNormalVertexCounter;

    private int _myTextureCoordinateCounter;

    private final HashMap<Material, Integer> _myMaterialMap;

    private final Vector<DrawableOBJTranslator> _myTranslators;

    private final boolean _mySaveVertexColorsAsTexture;

    private final String _myTextureDirectory;

    public static boolean IGNORE_MATERIAL = false;

    public static boolean IGNORE_NORMALS = false;

    public static boolean IGNORE_TEX_COORDS = false;

    public static boolean IGNORE_WARNINGS = false;

    public static boolean USE_ZIP = false;

    public static final String DELIMITER = " ";

    public static final String FACE = "f";

    public static final String VERTEX = "v";

    public static final String VERTEX_NORMALS = "vn";

    public static final String TEXTURE_COORDINATES = "vt";

    public static final String GROUP = "g";

    public static final String USE_MATERIAL = "usemtl";

    public static final String FACE_DELIMITER = "/";

    public static boolean IGNORE_OBJECTS = false;

    private TransformMatrix4f _myCameraTransform;

    public SceneWriter(final String theFilename, final Bin theBin) {
        this(theFilename, theBin, false, "");
    }

    public SceneWriter(final String theFilename,
                       final Bin theBin,
                       final boolean theSaveVertexColorsAsTexture,
                       final String theTextureDirectory) {
        this(theSaveVertexColorsAsTexture, theTextureDirectory);

        /* add all available translators */
        _myTranslators.add(new MeshTranslator());
        _myTranslators.add(new ModelTranslator());
        _myTranslators.add(new QuadLineTranslator());
        _myTranslators.add(new QuadBezierCurveTranslator());
        _myTranslators.add(new PlaneTranslator());
        _myTranslators.add(new CubeTranslator());
        _myTranslators.add(new TriangleTranslator());
        _myTranslators.add(new TubeLineTranslator());

        open(theFilename);
        parse(theBin);
        close();
    }

    public SceneWriter(final boolean theSaveVertexColorsAsTexture,
                       final String theTextureDirectory) {

        _mySaveVertexColorsAsTexture = theSaveVertexColorsAsTexture;
        _myTextureDirectory = theTextureDirectory;

        _myTranslators = new Vector<DrawableOBJTranslator>();
        _myMaterialMap = new HashMap<Material, Integer>();
        _myVertexCounter = 1;
        _myNormalVertexCounter = 1;
        _myTextureCoordinateCounter = 1;
    }

    public SceneWriter() {
        this(false, "");
    }

    public void parse(final String theFilename,
                      final Bin theBin) {
        open(theFilename);
        parse(theBin);
        close();
    }

    public Vector<DrawableOBJTranslator> translator() {
        return _myTranslators;
    }

    public void writeGroupTag(final String theGroupName) {
        writer().println(SceneWriter.GROUP + " " + theGroupName);
    }

    public void println() {
        writer().println();
    }

    public void print(String theString) {
        writer().print(theString);
    }

    public void print(int theString) {
        writer().print(theString);
    }

    private PrintStream writer() {
        return _myPrintStream;
    }

    public int bumpVertexCounter() {
        return _myVertexCounter++;
    }

    public int getVertexCounter() {
        return _myVertexCounter;
    }

    public int bumpNormalVertexCounter() {
        return _myNormalVertexCounter++;
    }

    public int bumpTextureCoordinateCounter() {
        return _myTextureCoordinateCounter++;
    }

    public int getTextureCoordinateCounter() {
        return _myTextureCoordinateCounter;
    }

    public int bumpUniqueObjectID() {
        return _myUniqueObjectID++;
    }

    private void parse(final Bin theBin) {
        /* header */
        _myPrintStream.println("# wavefront obj file");
        _myPrintStream.println();

        if (SceneWriter.IGNORE_OBJECTS) {
            writeGroupTag("frame-" + bumpUniqueObjectID());
        }

        /* parse objects */
        Drawable[] mySortables = theBin.getDataRef();
        for (int i = 0; i < theBin.size(); i++) {
            final Drawable myDrawable = mySortables[i];
            if (myDrawable != null) {
                if (myDrawable.isActive()) {
                    parseDrawable(myDrawable);
                    _myPrintStream.println();
                }
            }
        }
    }

    public void resetUniqueMaterialID() {
        _myUniqueMaterialID = 0;
    }

    public void resetUniqueObjectID() {
        _myUniqueObjectID = 0;
    }

    private void parseDrawable(final Drawable theDrawable) {

        for (final DrawableOBJTranslator myTranslator : _myTranslators) {
            if (myTranslator != null & myTranslator.isClass(theDrawable)) {
                myTranslator.parse(this, theDrawable);
                return;
            }
        }

        if (!IGNORE_WARNINGS) {
            System.out.println("### WARNING / drawable type unsupported. / " + theDrawable.getClass());
        }
    }

    public void parseMaterial(Material theMaterial) {
        if (IGNORE_MATERIAL) {
            return;
        }

        /* write material -- 'material name (usemtl)' */

        _myPrintStream.print(USE_MATERIAL);
        _myPrintStream.print(DELIMITER);

        if (theMaterial != null) {
            /* try to get material from hashmap */
            Integer myStoredMaterialID = _myMaterialMap.get(theMaterial);
            int myMaterialID;
            if (myStoredMaterialID == null) {
                _myUniqueMaterialID++;
                myMaterialID = _myUniqueMaterialID;
                _myMaterialMap.put(theMaterial, myMaterialID);
            } else {
                myMaterialID = myStoredMaterialID.intValue();
            }
            _myPrintStream.print("material" + myMaterialID);
        } else {
            _myPrintStream.print("default");
        }
        _myPrintStream.println();

        /** @todo can we write an .mtl file? -- 'mtllib master.mtl' */
        /* http://www.fileformat.info/format/material/ */
    }

    private void open(final String theFilename) {
        try {
            final FileOutputStream myFileOutputStream;
            final BufferedOutputStream myBufferedOutputStream;
            if (USE_ZIP) {
                myFileOutputStream = new FileOutputStream(theFilename + ".zip");
                final ZipOutputStream myZip = new ZipOutputStream(myFileOutputStream);
                final String[] myFullPathName = theFilename.split("/");
                final String mySceneName = myFullPathName[myFullPathName.length - 1];
                myZip.putNextEntry(new ZipEntry(mySceneName));
                myBufferedOutputStream = new BufferedOutputStream(myZip);
            } else {
                myFileOutputStream = new FileOutputStream(theFilename);
                myBufferedOutputStream = new BufferedOutputStream(myFileOutputStream);
            }

            _myPrintStream = new PrintStream(myBufferedOutputStream);
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    private void close() {
        _myPrintStream.close();
    }

    public void applyCameraTransform(final Camera theCamera) {
        TransformMatrix4f r = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        r.rotation.set(theCamera.getRotationMatrix());
        TransformMatrix4f t = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        t.translation.set(theCamera.position());
        t.translation.scale(-1);
        _myCameraTransform = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        _myCameraTransform.multiply(r);
        _myCameraTransform.multiply(t);
    }

    public void writeVertex(final Vector3f thePosition) {
        final Vector3f myPosition = new Vector3f(thePosition);
        if (_myCameraTransform != null) {
            _myCameraTransform.transform(myPosition);
        }

        writer().print(SceneWriter.VERTEX);
        writer().print(SceneWriter.DELIMITER);
        writer().print(myPosition.x);
        writer().print(SceneWriter.DELIMITER);
        writer().print(myPosition.y);
        writer().print(SceneWriter.DELIMITER);
        writer().print(myPosition.z);
        writer().println();
    }

    public void writeTextureCoordinates(Vector2f theCoordinates) {
        if (IGNORE_TEX_COORDS) {
            return;
        }
        writer().print(SceneWriter.TEXTURE_COORDINATES);
        writer().print(SceneWriter.DELIMITER);
        writer().print(theCoordinates.x);
        writer().print(SceneWriter.DELIMITER);
        writer().print(theCoordinates.y);
        writer().println();
    }

    public void writeNormal(final Vector3f theNormal) {
        if (IGNORE_NORMALS) {
            return;
        }
        writer().print(SceneWriter.VERTEX_NORMALS);
        writer().print(SceneWriter.DELIMITER);
        writer().print(theNormal.x);
        writer().print(SceneWriter.DELIMITER);
        writer().print(theNormal.y);
        writer().print(SceneWriter.DELIMITER);
        writer().print(theNormal.z);
        writer().println();
    }

    public boolean saveVertexColorsAsTexture() {
        return _mySaveVertexColorsAsTexture;
    }

    public String getTextureDirectory() {
        return _myTextureDirectory;
    }
}
