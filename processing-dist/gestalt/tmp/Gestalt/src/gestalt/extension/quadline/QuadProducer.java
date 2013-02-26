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


package gestalt.extension.quadline;


import java.io.Serializable;

import gestalt.material.Color;

import mathematik.Vector3f;

import werkzeug.interpolation.Interpolator;


public class QuadProducer
        implements Serializable {

    public static boolean VERBOSE = true;

    boolean adaptiveTexCoords = true;

    public static final int QUAD_TEXCORDS_ADAPTIVE = 0;

    public static final int QUAD_TEXCORDS_NORMALIZED = 1;

    public static final int QUAD_TEXCORDS_FIXEDLENGTH = 2;

    private int _myTexCoordsMode = QUAD_TEXCORDS_NORMALIZED;

    private float _myTexCoordsFixedLength = 1;

    private Vector3f _myProposedUpVector;

    private final Vector3f _myForwardVector;

    private Interpolator _myInterpolator;

    /* temp helpers */
    private Vector3f _mySideVector;

    private Vector3f _myUpVector;

    public boolean UPVECTOR_PROPAGATION = false;

    public QuadProducer() {
        _myProposedUpVector = new Vector3f(0, 0, 1);
        _myForwardVector = new Vector3f();

        _mySideVector = new Vector3f();
        _myUpVector = new Vector3f();

        _myInterpolator = null;
    }

    public Vector3f upvector() {
        return _myProposedUpVector;
    }

    public void setUpVectorRef(Vector3f theUpVectorRef) {
        _myProposedUpVector = theUpVectorRef;
    }

    public void setLineWidthInterpolator(Interpolator theInterpolator) {
        _myInterpolator = theInterpolator;
    }

    public void setTexCoordsMode(int theMode) {
        _myTexCoordsMode = theMode;
    }

    public void setTexCoordsFixedLength(float theTexCoordsFixedLength) {
        _myTexCoordsFixedLength = theTexCoordsFixedLength;
    }

    public void getQuadStrip(final Vector3f[] theLines,
                             final Color[] theColors,
                             final float[] theStrokeSizes,
                             final float theStrokeSize,
                             final QuadFragment[] theQuadFragments) {
        if (theLines != null
                && theQuadFragments != null
                && theQuadFragments.length == theLines.length
                && (theStrokeSizes == null || theStrokeSizes.length == theQuadFragments.length)
                && theLines.length >= 2) {

            if (theColors != null && theLines.length != theColors.length) {
                if (VERBOSE) {
                    System.err.println(
                            "### WARNING QuadProducer.getQuadStrip / malformed data / lines.length != colors.length");
                }
                return;
            }

            final Vector3f myPreviousUpVector = new Vector3f(_myProposedUpVector);

            float myTotalLineLength = 0;
            if (_myTexCoordsMode == QUAD_TEXCORDS_ADAPTIVE) {
                for (int i = 0; i < theLines.length - 1; i++) {
                    Vector3f myDistance = new Vector3f(theLines[i]);
                    myDistance.sub(theLines[i + 1]);
                    myTotalLineLength += myDistance.length();
                }
            }

            float myOldRatio = 0;
            for (int i = 0; i < theLines.length; ++i) {
                if (theQuadFragments[i] == null) {
                    theQuadFragments[i] = new QuadFragment();
                }

                /* linewidth */
                final float myPercentage = (float)i / (float)(theLines.length - 1);
                final float myLineWidthInterpolation;
                if (_myInterpolator == null) {
                    if (theStrokeSizes != null) {
                        myLineWidthInterpolation = theStrokeSizes[i];
                    } else {
                        myLineWidthInterpolation = theStrokeSize;
                    }
                } else {
                    myLineWidthInterpolation = _myInterpolator.get(myPercentage);
                }

                if (i < theLines.length - 1) {
                    _myForwardVector.sub(theLines[i + 1], theLines[i]);
                }

                getSideAndUpVector(myPreviousUpVector);

                /* store upvector for next fragment */
                if (UPVECTOR_PROPAGATION) {
                    myPreviousUpVector.set(_myUpVector);
                    /** @todo why in hell do we need to do this */
                    myPreviousUpVector.scale(-1);
                }

                _mySideVector.scale(myLineWidthInterpolation / 2.0f);
                theQuadFragments[i].pointA.sub(theLines[i], _mySideVector);
                theQuadFragments[i].pointB.add(theLines[i], _mySideVector);

                /* color */
                if (theColors != null) {
                    theQuadFragments[i].colorA = theColors[i];
                    theQuadFragments[i].colorB = theColors[i];
                }

                /* texture coordinates */
                float myRatio = 0;
                switch (_myTexCoordsMode) {
                    case (QUAD_TEXCORDS_NORMALIZED):
                        myRatio = myPercentage;
                        break;
                    case (QUAD_TEXCORDS_FIXEDLENGTH):
                        if (i > 0 && i < theLines.length - 1) {
                            Vector3f myDistance = new Vector3f(theLines[i - 1]);
                            myDistance.sub(theLines[i]);
                            myRatio = myDistance.length() / _myTexCoordsFixedLength + myOldRatio;
                        } else if (i == 0) {
                            myRatio = 0;
                        } else if (i == theLines.length - 1) {
                            myRatio = 1;
                        }
                        myOldRatio = myRatio;
                        break;
                    case (QUAD_TEXCORDS_ADAPTIVE):
                        if (i > 0 && i < theLines.length - 1) {
                            Vector3f myDistance = new Vector3f(theLines[i - 1]);
                            myDistance.sub(theLines[i]);
                            myRatio = myDistance.length() / myTotalLineLength + myOldRatio;
                        } else if (i == 0) {
                            myRatio = 0;
                        } else if (i == theLines.length - 1) {
                            myRatio = 1;
                        }
                        myOldRatio = myRatio;
                        break;
                }
                theQuadFragments[i].texcoordA.set(myRatio, 1);
                theQuadFragments[i].texcoordB.set(myRatio, 0);

                /* normal */
                theQuadFragments[i].normal.set(_myUpVector);
            }
        } else {
            if (VERBOSE) {
                System.err.println("### WARNING QuadProducer.getQuadStrip / malformed data");
            }
            return;
        }
    }

//    /**
//     * @deprecated
//     * this is method not yet tested.
//     */
//    public QuadPrimitive[] getQuads(final Vector3f[] theLines,
//                                    final Color[] theColors,
//                                    final float theStrokeSize,
//                                    final QuadPrimitive[] theQuads) {
//        /*
//         TODO
//         it is better to first get the quad points and then make the quads
//         in a second step.
//         two iteration vs calculationg everthing twice.
//         */
//        if ( (theLines.length >= 2) &&
//            (theLines.length == theColors.length) &&
//            (theQuads.length == (theLines.length - 1))) {
//
//            for (int i = 0; i < theLines.length - 2; ++i) {
//                if (theQuads[i] == null) {
//                    theQuads[i] = new QuadPrimitive();
//                }
//                _myForwardVector.sub(theLines[i + 1], theLines[i]);
//                _myForwardVector.normalize();
//                getSideAndUpVector(_myForwardVector, mySideVectorFront, myUpVectorFront);
//                _myForwardVector.sub(theLines[i + 2], theLines[i + 1]);
//                _myForwardVector.normalize();
//                getSideAndUpVector(_myForwardVector, mySideVectorBack, myUpVectorBack);
//
//                // scale to line width
//                mySideVectorFront.scale(theStrokeSize / 2.0f);
//                mySideVectorBack.scale(theStrokeSize / 2.0f);
//
//                // write to quads
//                theQuads[i].points[0].sub(theLines[i], mySideVectorFront);
//                theQuads[i].colors[0].set(theColors[i]);
//                theQuads[i].points[1].sub(theLines[i + 1], mySideVectorBack);
//                theQuads[i].colors[1].set(theColors[i + 1]);
//                theQuads[i].points[2].add(theLines[i + 1], mySideVectorBack);
//                theQuads[i].colors[2].set(theColors[i + 1]);
//                theQuads[i].points[3].add(theLines[i], mySideVectorFront);
//                theQuads[i].colors[3].set(theColors[i]);
//                theQuads[i].normals[0].set(myUpVectorFront);
//                theQuads[i].normals[1].set(myUpVectorBack);
//            }
//
//            final int myLast = theLines.length - 2;
//            theQuads[myLast].points[0].sub(theLines[myLast], mySideVectorBack);
//            theQuads[myLast].colors[0].set(theColors[myLast]);
//            theQuads[myLast].points[1].sub(theLines[myLast + 1], mySideVectorBack);
//            theQuads[myLast].colors[1].set(theColors[myLast + 1]);
//            theQuads[myLast].points[2].add(theLines[myLast + 1], mySideVectorBack);
//            theQuads[myLast].colors[2].set(theColors[myLast + 1]);
//            theQuads[myLast].points[3].add(theLines[myLast], mySideVectorBack);
//            theQuads[myLast].colors[3].set(theColors[myLast]);
//            theQuads[myLast].normals[0].set(myUpVectorBack);
//            theQuads[myLast].normals[1].set(myUpVectorBack);
//            return theQuads;
//        } else {
//            if (VERBOSE) {
//                System.err.println("### WARNING QuadProducer.getQuads / malformed data");
//            }
//            return null;
//        }
//    }
    private void getSideAndUpVector(final Vector3f theProposedUpVector) {
        /* get sideVector */
        _mySideVector.cross(theProposedUpVector, _myForwardVector);
        _mySideVector.normalize();
        /* get 'real' upVector */
        _myUpVector.cross(_mySideVector, _myForwardVector);
        _myUpVector.normalize();
    }
}
