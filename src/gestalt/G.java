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


package gestalt;

import gestalt.context.DisplayCapabilities;
import gestalt.extension.quadline.QuadLine;
import gestalt.material.Material;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmap;
import gestalt.material.texture.Bitmaps;
import gestalt.model.ModelData;
import gestalt.model.ModelLoaderOBJ;
import gestalt.processing.Model;
import gestalt.render.AnimatorRenderer;
import gestalt.render.BasicRenderer;
import gestalt.render.Drawable;
import gestalt.render.bin.Bin;
import gestalt.render.bin.DisposableBin;
import gestalt.render.controller.Camera;
import gestalt.render.controller.cameraplugins.Light;
import gestalt.shape.Cuboid;
import gestalt.shape.Disk;
import gestalt.shape.Mesh;
import gestalt.shape.Plane;
import gestalt.shape.PointSpriteCloud;
import gestalt.shape.Quad;
import gestalt.shape.Sphere;
import gestalt.util.CameraMover;

import java.io.InputStream;


public class G
        implements Gestalt,
                   Runnable {

    protected static G _myInstance;

    protected static BasicRenderer _myGestalt;

    public static int DEFAULT_BIN = Gestalt.BIN_3D;

    /* start in extra thread */
    private DisplayCapabilities _myTempDisplayCapabilities = null;

    private AnimatorRenderer _myTempRenderer = null;

    public static void initInExtraThread(Class<? extends AnimatorRenderer> theClass) {
        initInExtraThread(theClass, null);
    }

    public static void initInExtraThread(Class<? extends AnimatorRenderer> theClass, DisplayCapabilities theDisplayCapabilities) {
        init(theClass, theDisplayCapabilities, false);
        new Thread(_myInstance).start();
    }

    public static void initInExtraThread(Class<? extends AnimatorRenderer> theClass, int theWidht, int theHeight) {
        DisplayCapabilities dc = new DisplayCapabilities();
        dc.width = theWidht;
        dc.height = theHeight;
        initInExtraThread(theClass, dc);
    }

    public void run() {
        final DisplayCapabilities myTempDisplayCapabilities = _myTempDisplayCapabilities;
        final AnimatorRenderer myTempRenderer = _myTempRenderer;
        _myTempRenderer = null;
        _myTempDisplayCapabilities = null;
        if (myTempDisplayCapabilities == null) {
            myTempRenderer.init();
        } else {
            myTempRenderer.init(myTempDisplayCapabilities);
        }
    }


    /* --- */
    public static G init(Class<? extends AnimatorRenderer> theClass, DisplayCapabilities theDisplayCapabilities, boolean theStartRightAway) {
        cleanup();
        if (_myInstance == null) {
            try {
                final AnimatorRenderer myClass = (AnimatorRenderer)theClass.asSubclass(AnimatorRenderer.class).newInstance();
                G myG = setup(myClass);
                myG._myTempRenderer = myClass;
                myG._myTempDisplayCapabilities = theDisplayCapabilities;
                if (theStartRightAway) {
                    if (theDisplayCapabilities == null) {
                        myClass.init();
                    } else {
                        myClass.init(theDisplayCapabilities);
                    }
                }
                return myG;
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (InstantiationException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static void init(Class<? extends AnimatorRenderer> theClass, DisplayCapabilities theDisplayCapabilities) {
        init(theClass, theDisplayCapabilities, true);
    }

    public static void init(Class<? extends AnimatorRenderer> theClass, int theWidth, int theHeight) {
        init(theClass, theWidth, theHeight, false);
    }

    public static void init(Class<? extends AnimatorRenderer> theClass, int theWidth, int theHeight, boolean theUndecorated) {
        final DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = theWidth;
        myDisplayCapabilities.height = theHeight;
        myDisplayCapabilities.undecorated = theUndecorated;
        init(theClass, myDisplayCapabilities);
    }

    public static void init(Class<? extends AnimatorRenderer> theClass, int theWidth, int theHeight, int theMultiSampling) {
        final DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = theWidth;
        myDisplayCapabilities.height = theHeight;
        myDisplayCapabilities.antialiasinglevel = theMultiSampling;
        init(theClass, myDisplayCapabilities);
    }

    public static void init_fullscreen(Class<? extends AnimatorRenderer> theClass, boolean theExclusiveFullscreen) {
        final DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = DisplayCapabilities.getScreenSize().x;
        myDisplayCapabilities.height = DisplayCapabilities.getScreenSize().y;
        myDisplayCapabilities.fullscreen = theExclusiveFullscreen;
        myDisplayCapabilities.undecorated = true;
        init(theClass, myDisplayCapabilities);
    }

    public static void init(Class<? extends AnimatorRenderer> theClass) {
        init(theClass, null);
    }

    public static void cleanup() {
        _myInstance = null;
        _myGestalt = null;
        DEFAULT_BIN = Gestalt.BIN_3D;
    }

    public static G setup(BasicRenderer theBasicRenderer) {
        if (_myInstance == null) {
            _myInstance = new G(theBasicRenderer);
        }
        return _myInstance;
    }

    protected G() {
    }

    protected G(BasicRenderer theBasicRenderer) {
        _myGestalt = theBasicRenderer;
    }


    /* little helpers */
    protected static boolean die() {
        if (_myGestalt == null) {
            System.err.println("### ERROR @ Ge / initialize gestalt first by calling 'Ge.setup(...)'.");
            return true;
        } else {
            return false;
        }
    }

    public static final void add(Drawable theDrawable) {
        if (DEFAULT_BIN != Gestalt.UNDEFINED) {
            _myGestalt.bin(DEFAULT_BIN).add(theDrawable);
        }
    }

    public static final Drawable remove(Drawable theDrawable) {
        if (DEFAULT_BIN != Gestalt.UNDEFINED) {
            return _myGestalt.bin(DEFAULT_BIN).remove(theDrawable);
        }
        return null;
    }

    public static void setBinRef(int theDefaultBin) {
        DEFAULT_BIN = theDefaultBin;
    }

    public static Bin default_bin() {
        return _myGestalt.bin(DEFAULT_BIN);
    }


    /* convenience methods */
    public static Cuboid cuboid(final Bin theBin) {
        if (die()) {
            return null;
        }

        final Cuboid myCube = _myGestalt.drawablefactory().cuboid();
        if (theBin == null) {
            add(myCube);
        } else {
            theBin.add(myCube);
        }

        return myCube;
    }

    public static Cuboid cuboid() {
        return cuboid((Bin)null);
    }

    public static <T extends Drawable> T drawable(Class<T> theDrawable, final Bin theBin) {
        if (die()) {
            return null;
        }

        T mDrawable;
        try {
            mDrawable = theDrawable.newInstance();
            if (theBin == null) {
                add(mDrawable);
            } else {
                theBin.add(mDrawable);
            }
        } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
            mDrawable = null;
        }
        return mDrawable;
    }

    public static <T extends Drawable> T drawable(Class<T> theDrawable) {
        return drawable(theDrawable, (Bin)null);
    }

    public static Model model(InputStream theModelFile, InputStream theImageFile) {
        return model(theModelFile, theImageFile, false);
    }

    public static Model model(InputStream theModelFile,
                              InputStream theImageFile,
                              boolean theUseVBO) {
        final Model myModel = model(theModelFile, theUseVBO);

        final Bitmap myBitmap = Bitmaps.getBitmap(theImageFile);
        final TexturePlugin myTexture = _myGestalt.drawablefactory().texture();
        myTexture.scale().y = -1;
        myTexture.load(myBitmap);

        myModel.mesh().material().addTexture(myTexture);

        return myModel;
    }

    public static Model model(final InputStream theModelFile) {
        return model(theModelFile, false);
    }

    public static Model model(final ModelData myModelData, boolean theUseVBO) {
        if (die()) {
            return null;
        }

        Mesh myModelMesh = _myGestalt.drawablefactory().mesh(theUseVBO,
                                                             myModelData.vertices, 3,
                                                             myModelData.vertexColors, 4,
                                                             myModelData.texCoordinates, 2,
                                                             myModelData.normals,
                                                             myModelData.primitive);
        final Model myModel = new Model(myModelData, myModelMesh);
        add(myModel);

        return myModel;
    }

    public static Model model(final ModelData myModelData) {
        return model(myModelData, false);
    }

    public static Model model(final InputStream theModelFile, boolean theUseVBO) {
        if (die()) {
            return null;
        }

        final ModelData myModelData = ModelLoaderOBJ.getModelData(theModelFile);
        final Mesh myModelMesh = _myGestalt.drawablefactory().mesh(theUseVBO,
                                                                   myModelData.vertices, 3,
                                                                   myModelData.vertexColors, 4,
                                                                   myModelData.texCoordinates, 2,
                                                                   myModelData.normals,
                                                                   myModelData.primitive);
        final Model myModel = new Model(myModelData, myModelMesh);
        add(myModel);

        return myModel;
    }

    public static Mesh extract_mesh_from_modeldata(final InputStream theModelFile) {
        return extract_mesh_from_modeldata(theModelFile, false);
    }

    public static Mesh extract_mesh_from_modeldata(final InputStream theModelFile, boolean theUseVBO) {
        if (die()) {
            return null;
        }

        final ModelData myModelData = ModelLoaderOBJ.getModelData(theModelFile);
        final Mesh myModelMesh = _myGestalt.drawablefactory().mesh(theUseVBO,
                                                                   myModelData.vertices, 3,
                                                                   myModelData.vertexColors, 4,
                                                                   myModelData.texCoordinates, 2,
                                                                   myModelData.normals,
                                                                   myModelData.primitive);
        return myModelMesh;
    }

    public static Mesh mesh(boolean theUseVBO,
                            float[] theVertices,
                            int thePrimitive) {
        return mesh(theUseVBO, theVertices, null, null, null, thePrimitive);
    }

    public static Mesh mesh(boolean theUseVBO,
                            float[] theVertices,
                            float[] theVertexColors,
                            int thePrimitive) {
        return mesh(theUseVBO, theVertices, theVertexColors, null, null, thePrimitive);
    }

    public static Mesh mesh(boolean theUseVBO,
                            float[] theVertices,
                            float[] theVertexColors,
                            float[] theTextureCoordinates,
                            int thePrimitive) {
        return mesh(theUseVBO, theVertices, theVertexColors, theTextureCoordinates, null, thePrimitive);
    }

    public static Mesh mesh(boolean theUseVBO,
                            float[] theVertices,
                            float[] theVertexColors,
                            float[] theTextureCoordinates,
                            float[] theNormals,
                            int thePrimitive) {
        if (die()) {
            return null;
        }

        final Mesh myMesh = _myGestalt.drawablefactory().mesh(theUseVBO,
                                                              theVertices, 3,
                                                              theVertexColors, 4,
                                                              theTextureCoordinates, 2,
                                                              theNormals,
                                                              thePrimitive);
        add(myMesh);
        return myMesh;
    }

    public static Plane plane() {
        return plane((Bin)null);
    }

    public static Plane plane(final Bin theBin) {
        if (die()) {
            return null;
        }

        final Plane myPlane = _myGestalt.drawablefactory().plane();
        if (theBin == null) {
            add(myPlane);
        } else {
            theBin.add(myPlane);
        }

        return myPlane;
    }

    public static Plane plane(InputStream theFile) {
        return plane(null, theFile);
    }

    public static Plane plane(Bin theBin, InputStream theFile) {
        if (die()) {
            return null;
        }

        final TexturePlugin myTexture = texture(theFile);
        final Plane myPlane = plane(theBin);

        myPlane.material().addPlugin(myTexture);
        myPlane.setPlaneSizeToTextureSize();

        return myPlane;
    }

    public static Plane plane(Bin theBin, String theFile) {
        if (die()) {
            return null;
        }

        final TexturePlugin myTexture = texture(theFile);
        final Plane myPlane = plane(theBin);

        myPlane.material().addPlugin(myTexture);
        myPlane.setPlaneSizeToTextureSize();

        return myPlane;
    }

    public static Plane plane(String theFile) {
        return plane(null, theFile);
    }

    public static Quad quad() {
        return quad((Bin)null);
    }

    public static Quad quad(Bin theBin) {
        if (die()) {
            return null;
        }

        final Quad myQuad = _myGestalt.drawablefactory().quad();
        if (theBin == null) {
            add(myQuad);
        } else {
            theBin.add(myQuad);
        }

        return myQuad;
    }

    public static Quad quad(InputStream theFile) {
        return quad(null, theFile);
    }

    public static Quad quad(Bin theBin, InputStream theFile) {
        if (die()) {
            return null;
        }

        final TexturePlugin myTexture = texture(theFile);
        final Quad myQuad = quad(theBin);

        myQuad.material().addPlugin(myTexture);

        return myQuad;
    }

    public static Disk disk() {
        return disk((Bin)null);
    }

    public static Disk disk(Bin theBin) {
        if (die()) {
            return null;
        }

        final Disk myDisk = _myGestalt.drawablefactory().disk();

        if (theBin == null) {
            add(myDisk);
        } else {
            theBin.add(myDisk);
        }

        return myDisk;
    }

    public static Disk disk(InputStream theFile) {
        return disk(null, theFile);
    }

    public static Disk disk(Bin theBin, InputStream theFile) {
        final TexturePlugin myTexture = texture(theFile);
        final Disk myDisk = disk(theBin);

        myDisk.material().addPlugin(myTexture);
        myDisk.setDiskSizeToTextureSize();

        return myDisk;
    }

    public static QuadLine quadline() {
        if (die()) {
            return null;
        }

        final QuadLine myQuadLine = _myGestalt.drawablefactory().extensions().quadline();
        add(myQuadLine);
        myQuadLine.autoupdate(true);

        return myQuadLine;
    }

    public static QuadLine quadline(InputStream theFile) {
        final TexturePlugin myTexture = texture(theFile);
        final QuadLine myQuadLine = quadline();

        myQuadLine.material().addPlugin(myTexture);

        return myQuadLine;
    }

    public static Sphere sphere() {
        return sphere((Bin)null);
    }

    public static Sphere sphere(Bin theBin) {
        if (die()) {
            return null;
        }

        final Sphere mySphere = _myGestalt.drawablefactory().sphere();
        if (theBin == null) {
            add(mySphere);
        } else {
            theBin.add(mySphere);
        }

        return mySphere;
    }

    public static DisposableBin disposable() {
        return disposable(null);
    }

    public static DisposableBin disposable(final Bin theBin) {
        if (die()) {
            return null;
        }

        final DisposableBin myJoglDisposableBin = new DisposableBin();
        if (theBin == null) {
            add(myJoglDisposableBin);
        } else {
            theBin.add(myJoglDisposableBin);
        }

        return myJoglDisposableBin;
    }

    public static PointSpriteCloud pointspritecloud() {
        if (die()) {
            return null;
        }

        final PointSpriteCloud myJoglPointSpriteCloud = new PointSpriteCloud();
        add(myJoglPointSpriteCloud);

        return myJoglPointSpriteCloud;
    }

    public static PointSpriteCloud pointspritecloud(InputStream theFile) {
        final TexturePlugin myTexture = texture(theFile);
        final PointSpriteCloud myJoglPointSpriteCloud = pointspritecloud();

        myJoglPointSpriteCloud.material().addPlugin(myTexture);

        return myJoglPointSpriteCloud;
    }

    public static TexturePlugin texture(final InputStream theImageFile) {
        if (die()) {
            return null;
        }

        final Bitmap myBitmap = Bitmaps.getBitmap(theImageFile);
        TexturePlugin myTexture = _myGestalt.drawablefactory().texture();
        myTexture.load(myBitmap);

        return myTexture;
    }

    public static TexturePlugin texture(final String theImageFile) {
        if (die()) {
            return null;
        }

        final Bitmap myBitmap = Bitmaps.getBitmap(theImageFile);
        TexturePlugin myTexture = _myGestalt.drawablefactory().texture();
        myTexture.load(myBitmap);

        return myTexture;
    }

    public static TexturePlugin texture(final Bitmap myBitmap) {
        if (die()) {
            return null;
        }

        TexturePlugin myTexture = _myGestalt.drawablefactory().texture();
        myTexture.load(myBitmap);

        return myTexture;
    }

    public static Material material() {
        if (die()) {
            return null;
        }

        return _myGestalt.drawablefactory().material();
    }


    /* camera */
    public static Camera camera() {
        if (die()) {
            return null;
        }

        return _myGestalt.camera();
    }

    public static void cameramover(float theDeltaTime) {
        if (die()) {
            return;
        }

        CameraMover.handleKeyEvent(_myGestalt.camera(), _myGestalt.event(), theDeltaTime);
    }


    /* light */
    public static Light light() {
        if (die()) {
            return null;
        }

        return _myGestalt.light();
    }

    public static DisplayCapabilities createDisplayCapabilities() {
        return new DisplayCapabilities();
    }

    public static DisplayCapabilities createDisplayCapabilities(int theWidth, int theHeight,
                                                                float r, float g, float b,
                                                                boolean theFullscreen, boolean theSyncToVBlank) {
        final DisplayCapabilities myDisplayCapabilities = new DisplayCapabilities();
        myDisplayCapabilities.width = theWidth;
        myDisplayCapabilities.height = theHeight;
        myDisplayCapabilities.fullscreen = theFullscreen;
        myDisplayCapabilities.synctovblank = theSyncToVBlank;
        myDisplayCapabilities.backgroundcolor.set(r, g, b);
        return myDisplayCapabilities;
    }
}
