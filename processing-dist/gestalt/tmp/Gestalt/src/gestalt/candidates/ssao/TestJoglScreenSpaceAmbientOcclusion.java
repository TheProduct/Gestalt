package gestalt.candidates.ssao;


//import controlP5.ControlP5;
import gestalt.context.DisplayCapabilities;
import gestalt.context.GLContext;
import gestalt.extension.glsl.ShaderManager;
import gestalt.extension.glsl.ShaderProgram;
import gestalt.material.TexturePlugin;
import gestalt.material.texture.Bitmaps;
import gestalt.render.AnimatorRenderer;
import gestalt.shape.AbstractDrawable;
import gestalt.shape.Plane;
import gestalt.shape.Quad;
import gestalt.shape.Sphere;
import gestalt.util.CameraMover;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import mathematik.Util;
import mathematik.Vector3f;
import processing.core.PApplet;
import data.Resource;


/**
 *
 * @author patrick
 */
public class TestJoglScreenSpaceAmbientOcclusion
    extends AnimatorRenderer {

    private Plane _myDepthBufferPlane;

    private Plane _mySSAOPlane;

    private Quad _mySSAOQuad;

    private Sphere[] _myScene;

    private TexturePlugin _myDepthTexture;

    private TexturePlugin _myColorTexture;

    private SSAOShaderMaterial _mySSAO;

    private FrameBufferDepth _myCustomDepthBuffer;

    private DepthBufferMaterial _myDepthBufferShader;

    public void setup() {
        framerate(60);
        this.fpscounter(true);
        light().enable = true;

        /* shader manager */
        ShaderManager myShaderManager = drawablefactory().extensions().shadermanager();
        ShaderProgram myShaderProgram = myShaderManager.createShaderProgram();
        bin(BIN_FRAME_SETUP).add(myShaderManager);

        _myDepthBufferShader = new DepthBufferMaterial(myShaderManager,
                                                       myShaderProgram,
                                                       Resource.getStream("demo/shader/ssao/depthbuffer.vsh"),
                                                       Resource.getStream("demo/shader/ssao/depthbuffer.fsh"));
        _myCustomDepthBuffer = FrameBufferDepth.createDefault(512, 512, drawablefactory().camera());
        _myCustomDepthBuffer.backgroundcolor().set(1f, 1f, 1f, 1f);
        _myCustomDepthBuffer.shader = _myDepthBufferShader;
        _myCustomDepthBuffer.camera().nearclipping = 0;
        _myCustomDepthBuffer.camera().farclipping = 2000;
        bin(BIN_3D).add(_myCustomDepthBuffer);

        _myScene = new Sphere[20];
        for (int i = 0; i < _myScene.length; i++) {
            _myScene[i] = drawablefactory().sphere();
            _myScene[i].material().color4f().set(1f,
                                             1f,
                                             Math.round( (float) Math.random()),
                                             1f);
            _myScene[i].rotation().set(i, i, i);
            float offset = 80;
            _myScene[i].position().add(Util.random( -offset, offset),
                                       Util.random( -offset, offset),
                                       Util.random( -offset, offset));
            _myScene[i].material().depthmask = true;
            _myScene[i].material().depthtest = true;
            _myScene[i].material().transparent = false;
            _myScene[i].material().lit = true;
            _myCustomDepthBuffer.add(_myScene[i]);
            bin(BIN_3D).add(_myScene[i]);
        }

        Plane myFloor = drawablefactory().plane();
        myFloor.scale().set(1000, 1000, 1);
        myFloor.rotation().x = PI / 2f;
//        _myCustomDepthBuffer.add(myFloor);
//        bin(BIN_3D).add(myFloor);

        _myDepthBufferPlane = drawablefactory().plane();
        _myDepthBufferPlane.scale().set(displaycapabilities().width / 2f, displaycapabilities().height / 2f, 1);
        _myDepthBufferPlane.position().x -= displaycapabilities().width / 4f;
        _myDepthBufferPlane.material().addPlugin(_myCustomDepthBuffer);

        /* add shader material to plane */
        ShaderProgram mySSAOShaderProgram = myShaderManager.createShaderProgram();
        _mySSAO = new SSAOShaderMaterial(myShaderManager,
                                         mySSAOShaderProgram,
                                         Resource.getStream("demo/shader/ssao/ssao.vsh"),
                                         Resource.getStream("demo/shader/ssao/ssao.fsh"));

        _mySSAOQuad = drawablefactory().quad();
        _mySSAOQuad.a().position.set( -displaycapabilities().width / 2f, -displaycapabilities().height / 2f, 0);
        _mySSAOQuad.b().position.set( -displaycapabilities().width / 2f, displaycapabilities().height / 2f, 0);
        _mySSAOQuad.c().position.set(displaycapabilities().width / 2f, displaycapabilities().height / 2f, 0);
        _mySSAOQuad.d().position.set(displaycapabilities().width / 2f, -displaycapabilities().height / 2f, 0);

        _mySSAOQuad.a().color.set(0, 0, 0, 1);
        _mySSAOQuad.b().color.set(0, 1, 0, 1);
        _mySSAOQuad.c().color.set(1, 1, 0, 1);
        _mySSAOQuad.d().color.set(1, 0, 0, 1);

        updateFrustumPosition();

        TexturePlugin myRandomMap = drawablefactory().texture();
        myRandomMap.load(Bitmaps.getBitmap(Resource.getPath("demo/common/random.png")));
        myRandomMap.setTextureUnit(GL.GL_TEXTURE1);

        _myCustomDepthBuffer.setWrapMode(TEXTURE_WRAPMODE_CLAMP_TO_BORDER);
        _mySSAOQuad.material().addTexture(_myCustomDepthBuffer);
        _mySSAOQuad.material().addTexture(myRandomMap);
        _mySSAOQuad.material().addPlugin(_mySSAO);
        _mySSAOQuad.material().blendmode = MATERIAL_BLEND_MULTIPLY;
        bin(BIN_2D_FOREGROUND).add(_mySSAOQuad);
    }


    private void updateFrustumPosition() {
        float myRatio = width / (float) height;
        float myFovyRadians = (float) Math.toRadians(_myCustomDepthBuffer.camera().fovy);
        float myFarPlaneHeight = 2f * (float) Math.tan(myFovyRadians / 2f) * _myCustomDepthBuffer.camera().farclipping;
        float myFarPlaneWidth = myFarPlaneHeight * myRatio;

        Vector3f direction = new Vector3f(0, 0, 1);
        direction.scale(_myCustomDepthBuffer.camera().farclipping);

        Vector3f position = new Vector3f(_myCustomDepthBuffer.camera().position());
        Vector3f myFarClippingPos = new Vector3f(0, 0, 0);
        myFarClippingPos.add(direction);

        Vector3f myUp = new Vector3f(_myCustomDepthBuffer.camera().upvector());
        myUp.scale(myFarPlaneHeight / 2f);

        Vector3f myRight = new Vector3f(1, 0, 0);
        myRight.scale(myFarPlaneWidth / 2f);

        Vector3f myUpperLeft = new Vector3f(myFarClippingPos);
        myUpperLeft.add(myUp);
        myUpperLeft.sub(myRight);

        Vector3f myUpperRight = new Vector3f(myUpperLeft);
        myUpperRight.x += myFarPlaneWidth;

        Vector3f myBottomRight = new Vector3f(myUpperRight);
        myBottomRight.y -= myFarPlaneHeight;

        Vector3f myBottomLeft = new Vector3f(myUpperLeft);
        myBottomLeft.y -= myFarPlaneHeight;

        _mySSAOQuad.a().texcoord.set(myUpperLeft);
        _mySSAOQuad.b().texcoord.set(myBottomLeft);
        _mySSAOQuad.c().texcoord.set(myBottomRight);
        _mySSAOQuad.d().texcoord.set(myUpperRight);
    }


    float myCounter = 0;

    public void loop(final float theDeltaTime) {
        camera().set(_myCustomDepthBuffer.camera());
        CameraMover.handleKeyEvent(_myCustomDepthBuffer.camera(), _myEvent, theDeltaTime);

        light().position().set(camera().position());
        updateFrustumPosition();
        if (event().keyPressed) {
            if (event().key == ',') {
                _mySSAOQuad.setActive(true);
            }
            if (event().key == '.') {
                _mySSAOQuad.setActive(false);
            }
        }

//        myCounter += theDeltaTime * 0.5f;
        for (int i = 0; i < _myScene.length; i++) {
            float myRadius = (float) Math.sin(myCounter + i);
            float mySize = myRadius * i * 10;
            _myScene[i].scale().set(mySize, mySize, mySize);
            _myScene[i].rotation().x += 0.01f;
            _myScene[i].rotation().y += 0.01f;
        }
        _mySSAO.sampleRadius = P.SAMPLE_RADIUS;
        _mySSAO.distanceScale = P.DISTANCE_SCALE;
        _mySSAO.maxSampleDelta = P.MAX_SAMPLE_DELTA;

        _myDepthBufferShader.far = _myCustomDepthBuffer.camera().farclipping;
    }


    private class DepthColorBufferCopy
        extends AbstractDrawable {

        private final TexturePlugin _myDepthTexture;

        private final TexturePlugin _myColorTexture;

        public boolean depthbufferclearing = true;

        public int x;

        public int y;

        public int width;

        public int height;

        public DepthColorBufferCopy(final TexturePlugin theTexture,
                                    final TexturePlugin theColorTexture) {
            _myDepthTexture = (TexturePlugin) theTexture;
            _myColorTexture = (TexturePlugin) theColorTexture;
        }


        public void draw(GLContext theRenderContext) {
            final GL gl = (  theRenderContext).gl;
            final GLU glu = (  theRenderContext).glu;

            /* copy depthbuffer into texture */
            gl.glBindTexture(_myDepthTexture.getTextureTarget(),
                             _myDepthTexture.getTextureID());
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT, width, height, 0,
                            GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
            gl.glCopyTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);

            /* copy colorbuffer into texture */
            gl.glBindTexture(_myColorTexture.getTextureTarget(),
                             _myColorTexture.getTextureID());
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_RGB, width, height, 0,
                            GL.GL_RGB, GL.GL_UNSIGNED_BYTE, null);
            gl.glCopyTexSubImage2D(GL.GL_TEXTURE_2D, 0, 0, 0, 0, 0, width, height);
            if (depthbufferclearing) {
                gl.glClear(GL.GL_DEPTH_BUFFER_BIT);
            }
        }
    }


    public static class P
        extends PApplet {

//        private ControlP5 controlP5;

        private static float SAMPLE_RADIUS = 0.005f;

        private static float DISTANCE_SCALE = 1000f;

        private static float MAX_SAMPLE_DELTA = 1000f;

        public void setup() {
            size(400, 200);

//            int x = 20;
//            int y = 20;
//            int myRaster = 20;
//            int myWidth = 150;
//            controlP5 = new ControlP5(this);
//
//            controlP5.addSlider("SAMPLE_RADIUS",
//                                0, 0.1f, SAMPLE_RADIUS,
//                                x, y,
//                                myWidth, 10);
//            y += myRaster;
//
//            controlP5.addSlider("DISTANCE_SCALE",
//                                1f, 5000f, DISTANCE_SCALE,
//                                x, y,
//                                myWidth, 10);
//            y += myRaster;
//
//            controlP5.addSlider("MAX_SAMPLE_DELTA",
//                                0f, 1f, MAX_SAMPLE_DELTA,
//                                x, y,
//                                myWidth, 10);
//            y += myRaster;
//
//            controlP5.load(Resource.getPath("demo/shader/ssao/" +
//                                            TestJoglScreenSpaceAmbientOcclusion.class.getSimpleName() + "-saved.xml"));
//            controlP5.trigger();
        }


        public void draw() {
            background(50);
            frame.setLocation(0, 0);
        }

//        public void keyPressed() {
//            if (key == '0') {
//                controlP5.save(Resource.getPath("demo/shader/ssao/") +
//                               TestJoglScreenSpaceAmbientOcclusion.class.getSimpleName() + "-saved.xml");
//            }
//            if (key == '1') {
//                controlP5.load(Resource.getPath("demo/shader/ssao/" +
//                                                TestJoglScreenSpaceAmbientOcclusion.class.getSimpleName() + "-saved.xml"));
//            }
//            controlP5.trigger();
//        }
    }


    public static void main(String[] theArgs) {
        PApplet.main(new String[] {P.class.getName()});

        DisplayCapabilities dc = new DisplayCapabilities();
        dc.width = 512;
        dc.height = 512;
        dc.undecorated = true;
        dc.backgroundcolor.set(1, 1, 1);
        dc.antialiasinglevel = 4;
        new TestJoglScreenSpaceAmbientOcclusion().init(dc);
    }
}
