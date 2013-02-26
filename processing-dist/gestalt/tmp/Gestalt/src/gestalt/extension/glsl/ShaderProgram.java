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


import java.util.Vector;
import javax.media.opengl.GL;

import static gestalt.Gestalt.UNDEFINED;


public class ShaderProgram {

    public static boolean VERBOSE = false;

    private int _myID;

    private Vector<String[]> _myFragmentShaderStrings;

    private Vector<String[]> _myVertexShaderStrings;

    private int _myGLShaderProgramID;

    private int _myVertexShaderID;

    private int _myFragmentShaderID;

    public ShaderProgram(int theID) {
        _myID = theID;
        _myVertexShaderID = UNDEFINED;
        _myFragmentShaderID = UNDEFINED;
        _myFragmentShaderStrings = new Vector<String[]> ();
        _myVertexShaderStrings = new Vector<String[]> ();
    }


    public int getID() {
        return _myID;
    }


    public int getOpenGLID() {
        return _myGLShaderProgramID;
    }


    public int getVertexProgramID() {
        return _myVertexShaderID;
    }


    public int getFragmentProgramID() {
        return _myFragmentShaderID;
    }


    public void setFragmentShaderString(String[] theFragmentShaderString) {
        _myFragmentShaderStrings.add(theFragmentShaderString);
    }


    public void setVertexShaderString(String[] theVertexShaderString) {
        _myVertexShaderStrings.add(theVertexShaderString);
    }


    public void createShaderProgram(GL gl) {
        _myGLShaderProgramID = gl.glCreateProgramObjectARB();
        if (VERBOSE) {
            System.out.println("### INFO @ ShaderProgram createShaderProgram with PROGRAM ID: " + _myGLShaderProgramID);
        }
    }


    public void attachFragmentShader(GL gl) {
        for (String[] myFragmentShaderString : _myFragmentShaderStrings) {
            if (myFragmentShaderString != null) {
                _myFragmentShaderID = loadShader(gl, myFragmentShaderString, GL.GL_FRAGMENT_SHADER_ARB);
                gl.glAttachObjectARB(_myGLShaderProgramID, _myFragmentShaderID);
                gl.glDeleteShader(_myFragmentShaderID);
                if (VERBOSE) {
                    System.out.println("### INFO @ ShaderProgram / attachFragmentShader with SHADER ID: "
                                       + _myFragmentShaderID);
                }
            } else {
                if (VERBOSE) {
                    System.out.println("### INFO @ ShaderProgram / no fragment shader to attach");
                }
            }
        }
        linkShaderObject(gl, _myGLShaderProgramID);
    }


    public void attachVertexShader(GL gl) {
        for (String[] myVertexShaderString : _myVertexShaderStrings) {
            if (myVertexShaderString != null) {
                _myVertexShaderID = loadShader(gl, myVertexShaderString, GL.GL_VERTEX_SHADER_ARB);
                gl.glAttachObjectARB(_myGLShaderProgramID, _myVertexShaderID);
                gl.glDeleteShader(_myVertexShaderID);
                if (VERBOSE) {
                    System.out
                        .println("### INFO @ ShaderProgram / attachVertexShader with SHADER ID: "
                                 + _myVertexShaderID);
                }
            } else {
                if (VERBOSE) {
                    System.out.println("### INFO @ ShaderProgram / no vertex shader to attach");
                }
            }
        }
        linkShaderObject(gl, _myGLShaderProgramID);
    }


    /**
     * Link shader object. NOTE! Do this after attaching programs!
     *
     * @param handle Shader object handle
     * @return boolean indicating success/failure. Detailed error logged to
     *         file.
     */
    private boolean linkShaderObject(GL gl,
                                     int theShaderProgram) {
        gl.glLinkProgramARB(theShaderProgram);
        int[] linkStatus = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetObjectParameterivARB(theShaderProgram, GL.GL_OBJECT_LINK_STATUS_ARB, linkStatus, 0);
        if (linkStatus[0] == GL.GL_FALSE) {
            if (VERBOSE) {
                String out = "### ERROR @ ShaderProgram / linkShaderObject failed / Log dump:";
                int[] infologlength = new int[1];
                byte[] infolog = new byte[1024];
                /** @todo JSR-231 -- added 0 */
                gl.glGetInfoLogARB(theShaderProgram, 1024, infologlength, 0, infolog, 0);
                String logstr = new String(infolog);
                System.out.println(out + " / " + logstr);
            }
            return false;
        }
        return true;
    }


    /**
     * Load and compile fragment shader.
     *
     * @param program String array containing lines of program
     * @return int handle or -1 on error. Detailed error logged to file.
     */
    private int loadShader(GL gl,
                           String[] theProgramLines,
                           int theShaderType) {
        int handle = UNDEFINED;
        // Convert to single string
        String myProgramString = "";
        for (int i = 0; i < theProgramLines.length; i++) {
            myProgramString += theProgramLines[i] += "\n";
        }

        // Convert to array
        String[] myPrograms = {myProgramString};

        // Create an empty shader object
        handle = gl.glCreateShaderObjectARB(theShaderType);

        // Create an array consisting of shader lengths
        int[] myShaderlengths = new int[myPrograms.length];
        for (int i = 0; i < myPrograms.length; i++) {
            myShaderlengths[i] = myPrograms[i].length();
        }

        // Provide shader source code
        /** @todo JSR-231 -- added 0 */
        gl.glShaderSourceARB(handle, myPrograms.length, myPrograms, myShaderlengths, 0);

        // Compile the shader
        gl.glCompileShaderARB(handle);

        // check for error
        int[] compileStatus = new int[1];
        /** @todo JSR-231 -- added 0 */
        gl.glGetObjectParameterivARB(handle, GL.GL_OBJECT_COMPILE_STATUS_ARB, compileStatus, 0);
        if (compileStatus[0] == GL.GL_FALSE) {
            if (VERBOSE) {
                int[] infologlength = new int[1];
                byte[] infolog = new byte[1024];
                /** @todo JSR-231 -- added 0 */
                gl.glGetInfoLogARB(handle, 1024, infologlength, 0, infolog, 0);
                String logstr = new String(infolog);
                System.err.println("### ERROR @ " + this.getClass() + " / Shader compilation failed / " + logstr);
            }
            return UNDEFINED;
        }
        return handle;
    }
}
