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


import gestalt.Gestalt;
import gestalt.extension.quadline.QuadFragment;
import java.io.Serializable;

import gestalt.material.Color;

import mathematik.Vector3f;


public class TubeLineProducer
        implements Serializable {

    private Vector3f mProposedUpVector;

    /* temp helpers */
    private final Vector3f mSideVector;

    private final Vector3f mUpVector;

    private final Vector3f mForwardVector;

    public boolean UPVECTOR_PROPAGATION = true;

    public TubeLineProducer() {
        mProposedUpVector = new Vector3f(0, 0, 1);
        mForwardVector = new Vector3f();
        mSideVector = new Vector3f();
        mUpVector = new Vector3f();
    }

    public Vector3f upvector() {
        return mProposedUpVector;
    }

    public void setUpVectorRef(Vector3f theUpVectorRef) {
        mProposedUpVector = theUpVectorRef;
    }

    public QuadFragment[][] produce(final Vector3f[] pLines,
                                    final float pWidth,
                                    final int pSteps) {
        return produce(pLines,
                       null,
                       null,
                       pWidth,
                       pSteps);
    }

    public QuadFragment[][] produce(final Vector3f[] pLines,
                                    final Color[] pColors,
                                    final float[] pWidthSet,
                                    final int pSteps) {
        return produce(pLines,
                       pColors,
                       pWidthSet,
                       0,
                       pSteps);
    }

    public QuadFragment[][] produce(final Vector3f[] pLines,
                                    final float[] pWidthSet,
                                    final int pSteps) {
        return produce(pLines,
                       null,
                       pWidthSet,
                       0,
                       pSteps);
    }

    public QuadFragment[][] produce(final Vector3f[] pLines,
                                    final Color[] pColors,
                                    final float pWidth,
                                    final int pSteps) {
        return produce(pLines,
                       pColors,
                       null,
                       pWidth,
                       pSteps);
    }

    public QuadFragment[][] produce(final Vector3f[] pLines,
                                    final Color[] pColors,
                                    final float[] pWidthSet,
                                    final float pWidth,
                                    final int pSteps) {
        if (pLines == null) {
            System.err.println("### WARNING @" + getClass().getSimpleName() + " / couldn t produce fragments. data corrupted.");
            return null;
        }

        if (pColors != null && pLines.length != pColors.length) {
            System.err.println("### WARNING @" + getClass().getSimpleName() + " / couldn t produce fragments. data corrupted.");
            return null;
        }

        if (pWidthSet != null && pLines.length != pWidthSet.length) {
            System.err.println("### WARNING @" + getClass().getSimpleName() + " / couldn t produce fragments. data corrupted.");
            return null;
        }

        final QuadFragment[][] mFragments = new QuadFragment[pSteps][pLines.length];
        final Vector3f myPreviousUpVector = new Vector3f(mProposedUpVector);

        for (int i = 0; i < pLines.length; ++i) {
            /* width or radius */
            final float mInterpolatedWidth;
            if (pWidthSet != null) {
                mInterpolatedWidth = pWidthSet[i];
            } else {
                mInterpolatedWidth = pWidth;
            }

            /* forward vector -- get vector between 2 points */
            if (i < pLines.length - 1) {
                mForwardVector.sub(pLines[i + 1], pLines[i]);
            }

            /* side vector */
            getSideAndUpVector(myPreviousUpVector);

            /* store upvector for next fragment */
            if (UPVECTOR_PROPAGATION) {
                myPreviousUpVector.set(mUpVector);
                /** @todo why in hell do we need to do this */
                myPreviousUpVector.scale(-1);
            }

            /* add scaled sidevector to  */
            final float mStepSize = Gestalt.TWO_PI / (float)pSteps;
            for (int j = 0; j < pSteps; j++) {
                mFragments[j][i] = new QuadFragment();
                QuadFragment mFrag = mFragments[j][i];

                /* position */
                final float mAngle = mStepSize * j;
                final float mNextAngle = mAngle + mStepSize;
                final Vector3f mSide = mathematik.Util.scale(mSideVector, mInterpolatedWidth / 2.0f);
                mFrag.pointA.set(mathematik.Util.rotatePoint(mSide, mAngle, mForwardVector));
                mFrag.pointB.set(mathematik.Util.rotatePoint(mSide, mNextAngle, mForwardVector));
                mFrag.pointA.add(pLines[i]);
                mFrag.pointB.add(pLines[i]);

                /* color */
                if (pColors != null) {
                    mFrag.colorA = pColors[i];
                    mFrag.colorB = pColors[i];
                }

                /* texture coordinates */
                // disabled tex coords for now

                /* normal */
                Vector3f mNewSide = mathematik.Util.sub(mFrag.pointA, mFrag.pointB);
                mFrag.normal.cross(mForwardVector, mNewSide);
                mFrag.normal.normalize();
            }
        }

        return mFragments;
    }

    private void getSideAndUpVector(final Vector3f theProposedUpVector) {
        /* get sideVector */
        mSideVector.cross(theProposedUpVector, mForwardVector);
        mSideVector.normalize();
        /* get 'real' upVector */
        mUpVector.cross(mSideVector, mForwardVector);
        mUpVector.normalize();
    }
}
