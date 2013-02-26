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


package gestalt.extension.glsl;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Vector;
import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import gestalt.context.GLContext;
import gestalt.context.GLContext;
import gestalt.shape.AbstractDrawable;
import gestalt.material.Color;
import gestalt.material.texture.TextureInfo;
import gestalt.util.JoglUtil;

import mathematik.Vector2f;
import mathematik.Vector3f;


public class ShaderManager
        extends AbstractDrawable {

    public static boolean VERBOSE = true;

    protected static ShaderManager _myInstance = null;

    private Vector<ShaderProgram> _myShaderPrograms;

    private Vector<ShaderProgram> _myShaderProgramsToDelete;

    private GL gl;

    private GLU glu;

    private boolean _myIsInitialized = false;

    private int _myUniqueNameCounter;

    public ShaderManager() {
        _myUniqueNameCounter = -1;
        _myShaderPrograms = new Vector<ShaderProgram>();
        _myShaderProgramsToDelete = new Vector<ShaderProgram>();
    }

    public ShaderProgram createShaderProgram() {
        _myUniqueNameCounter++;
        ShaderProgram myShaderProgram = new ShaderProgram(_myUniqueNameCounter);
        _myShaderPrograms.add(myShaderProgram);
        if (_myIsInitialized) {
            _myShaderPrograms.get(_myUniqueNameCounter).createShaderProgram(gl);
        }
        return myShaderProgram;
    }

    public void deleteShaderProgram(ShaderProgram theShaderProgram) {
        /* TODO implement deletion */
        if (_myIsInitialized) {
            gl.glDeleteObjectARB(theShaderProgram.getOpenGLID());
            _myShaderPrograms.remove(theShaderProgram);
        } else {
            _myShaderProgramsToDelete.add(theShaderProgram);
        }
    }

    public void attachFragmentShader(ShaderProgram theShaderProgram,
                                     InputStream theFilePath) {
        String[] myFragmentShaderString = loadTextFile(theFilePath);
        attachFragmentShader(theShaderProgram, myFragmentShaderString);
    }

    public void attachFragmentShader(ShaderProgram theShaderProgram,
                                     String[] myFragmentShaderString) {
        theShaderProgram.setFragmentShaderString(myFragmentShaderString);
        if (_myIsInitialized) {
            theShaderProgram.attachFragmentShader(gl);
        }
    }

    public void attachVertexShader(ShaderProgram theShaderProgram,
                                   InputStream theFilePath) {
        String[] myVertexShaderString = loadTextFile(theFilePath);
        attachVertexShader(theShaderProgram, myVertexShaderString);
    }

    public void attachVertexShader(ShaderProgram theShaderProgram,
                                   String[] myVertexShaderString) {
        theShaderProgram.setVertexShaderString(myVertexShaderString);
        if (_myIsInitialized) {
            theShaderProgram.attachVertexShader(gl);
        }
    }

    public void enable(ShaderProgram theShaderProgram) {
        if (_myIsInitialized) {
            gl.glUseProgramObjectARB(theShaderProgram.getOpenGLID());
            JoglUtil.printGLError(gl, getClass().getSimpleName() + ".enable");
        } else {
            System.err.println("### WARNING @ ShaderManager / shader cannot be enabled; not yet initialized");
        }
    }

    public void disable() {
        if (_myIsInitialized) {
            gl.glUseProgramObjectARB(0);
            JoglUtil.printGLError(gl, getClass().getSimpleName() + ".disable");
        }
    }

    public void disable(GL gl) {
        gl.glUseProgramObjectARB(0);
    }


    /* --> interface obligations */
    public void draw(final GLContext theRenderContext) {
        gl = theRenderContext.gl;
        glu = theRenderContext.glu;
        if (!_myIsInitialized) {
            init(gl, glu);
        }
    }

    public void init(GL theGL, GLU theGLU) {
        gl = theGL;
        glu = theGLU;
        if (!_myIsInitialized) {
            if (!gl.isExtensionAvailable("GL_ARB_shader_objects") || !gl.isExtensionAvailable("GL_ARB_fragment_shader") || !gl.isExtensionAvailable("GL_ARB_shading_language_100")) {
                System.err.println("### WARNING @ ShaderManager / GLSL not available");
            } else {
                if (VERBOSE) {
                    System.out.println("### INFO @ ShaderManager / GLSL is available");
                }
            }
            for (int i = 0; i < _myShaderPrograms.size(); i++) {
                if (_myShaderPrograms.get(i) != null) {
                    _myShaderPrograms.get(i).createShaderProgram(gl);
                    _myShaderPrograms.get(i).attachVertexShader(gl);
                    _myShaderPrograms.get(i).attachFragmentShader(gl);
                }
            }
            for (int i = 0; i < _myShaderProgramsToDelete.size(); i++) {
                gl.glDeleteObjectARB(_myShaderProgramsToDelete.get(i).getOpenGLID());
            }
            _myShaderProgramsToDelete.clear();
            _myIsInitialized = true;
        }
        JoglUtil.printGLError(gl, getClass().getSimpleName() + ".init");
    }


    /* --> util */
    private String[] loadTextFile(InputStream theFile) {
        final LinkedList<String> list = new LinkedList<String>();
        try {
            BufferedReader myBufferedReader = new BufferedReader(
                    new InputStreamReader(theFile));
            String line = null;
            while ((line = myBufferedReader.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception theException) {
            // System.err.println("### ERROR @ " + getClass().getName() + " / "
            // + theException);
            return null;
        }
        String[] myLines = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            myLines[i] = (String)list.get(i);
        }
        return myLines;
    }

    public static int getUniformLoc(GL gl, GLU glu, int program, String name) {
        int loc;
        loc = gl.glGetUniformLocation(program, name);
        if (loc == -1) {
            System.out.println("### INFO @ ShaderProgram / No such uniform named " + name);
        }
        JoglUtil.printGLError(gl, glu, "getUniformLoc", true);
        return loc;
    }


    /* helper methods for setting uniforms */

    /* 1f */
    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName, final float theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform1f(myID, theValue);
    }


    /* 1i */
    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName, final int theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform1i(myID, theValue);
    }

    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName,
                           final boolean theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform1i(myID, theValue ? 1 : 0);
    }


    /* fv */
    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName, final float[] theValue) {
        switch (theValue.length) {
            case 1:
                setUniform1fv(theShaderProgram, theUniformName, theValue);
                break;
            case 2:
                setUniform2fv(theShaderProgram, theUniformName, theValue);
                break;
            case 3:
                setUniform3fv(theShaderProgram, theUniformName, theValue);
                break;
            case 4:
                setUniform4fv(theShaderProgram, theUniformName, theValue);
                break;
            default:
                setUniform1fv(theShaderProgram, theUniformName, theValue);
        }
    }

    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName, final Vector3f theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform3fv(myID, 1, theValue.toArray(), 0);
    }

    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName, final Vector2f theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform2fv(myID, 1, theValue.toArray(), 0);
    }

    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName, final Color theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform4fv(myID, 1, theValue.toArray(), 0);
    }

    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName,
                           final float theA,
                           final float theB,
                           final float theC) {
        setUniform3fv(theShaderProgram, theUniformName, new float[] {theA, theB, theC});
    }

    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName,
                           final float theA,
                           final float theB,
                           final float theC,
                           final float theD) {
        setUniform3fv(theShaderProgram, theUniformName, new float[] {theA, theB, theC, theD});
    }

    public void setUniformMat4f(final ShaderProgram theShaderProgram,
                                final String theUniformName,
                                final float[] theValue,
                                boolean theTranspose) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        if (theValue.length != 16) {
            System.err.println("### WARNING @ " + getClass().getName() + ".setUniformMat4 / arry should contain of 16 floats.");
        }
        gl.glUniformMatrix4fv(myID, theValue.length, theTranspose, FloatBuffer.wrap(theValue));
    }

    public void setUniformMat4f(final ShaderProgram theShaderProgram,
                                final String theUniformName,
                                final float[] theValue) {
        setUniformMat4f(theShaderProgram,
                        theUniformName,
                        theValue,
                        false);
    }

    public void setUniform1fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final float[] theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform1fv(myID, theValue.length, FloatBuffer.wrap(theValue));
    }

    public void setUniform2fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final float[] theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform2fv(myID, 1, theValue, 0);
    }

    public void setUniform3fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final float[] theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform3fv(myID, 1, theValue, 0);
    }

    public void setUniform4fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final float[] theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform4fv(myID, 1, theValue, 0);
    }

    public void setUniform1fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final FloatBuffer theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform1fv(myID, theValue.capacity(), theValue);
    }

    public void setUniform2fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final FloatBuffer theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform2fv(myID, theValue.capacity(), theValue);
    }

    public void setUniform3fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final FloatBuffer theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(), theUniformName);
        gl.glUniform3fv(myID, theValue.capacity(), theValue);
    }

    public void setUniform4fv(final ShaderProgram theShaderProgram,
                              final String theUniformName, final FloatBuffer theValue) {
        final int myID = getUniformLoc(gl, glu, theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform4fv(myID, theValue.capacity(), theValue);
    }

    /* texture uniforms */
    public void setUniform(final ShaderProgram theShaderProgram,
                           final String theUniformName,
                           final TextureInfo theValue) {
        final int myID = getUniformLoc(gl, glu,
                                       theShaderProgram.getOpenGLID(),
                                       theUniformName);
        gl.glUniform1i(myID, JoglUtil.getTextureUnitID(theValue.getTextureUnit()));
    }
}
