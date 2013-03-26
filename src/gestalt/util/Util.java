package gestalt.util;


import gestalt.Gestalt;
import gestalt.shape.Mesh;
import java.util.Vector;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;


public class Util {

    public static Mesh mergeMeshes(Vector<Mesh> pMeshes) {
        int mVertexCount = 0;
        for (int i = 0; i < pMeshes.size(); i++) {
            mVertexCount += pMeshes.get(i).vertices().length;
        }

        final float[] mVertexData = new float[mVertexCount];
        int mCounter = 0;
        for (int i = 0; i < pMeshes.size(); i++) {
            final float[] mIndividualVertexData = pMeshes.get(i).vertices();
            for (int j = 0; j < mIndividualVertexData.length; j++) {
                mVertexData[mCounter++] = mIndividualVertexData[j];
            }
        }
        return new Mesh(mVertexData, 3,
                null, 4,
                null, 2, null, Gestalt.MESH_TRIANGLES);
    }

    public static TransformMatrix4f getTranslateRotationTransform(final int theTransformMode,
            final TransformMatrix4f theTransform,
            final Vector3f theRotation,
            final Vector3f theScale) {

        final TransformMatrix4f myMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            myMatrix.multiply(theTransform);
        }

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_POSITION_AND_ROTATION) {
            final TransformMatrix4f myTranslationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
            myTranslationMatrix.translation.set(theTransform.translation);
            myMatrix.multiply(myTranslationMatrix);
        }

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_POSITION_AND_ROTATION
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            if (theRotation.x != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setXRotation(theRotation.x);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.y != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setYRotation(theRotation.y);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.z != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setZRotation(theRotation.z);
                myMatrix.multiply(myRotationMatrix);
            }
        }

        /* finally scale the shape */
        final TransformMatrix4f myScaleMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myScaleMatrix.rotation.xx = theScale.x;
        myScaleMatrix.rotation.yy = theScale.y;
        myScaleMatrix.rotation.zz = theScale.z;
        myMatrix.multiply(myScaleMatrix);

        return myMatrix;
    }

    public static TransformMatrix4f getRotationTransform(final int theTransformMode,
            final TransformMatrix4f theTransform,
            final Vector3f theRotation,
            final Vector3f theScale) {

        final TransformMatrix4f myMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        final TransformMatrix4f myTransform = new TransformMatrix4f(theTransform);
        myTransform.translation.set(0, 0, 0);

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            myMatrix.multiply(myTransform);
        }

        if (theTransformMode == Gestalt.SHAPE_TRANSFORM_POSITION_AND_ROTATION
                || theTransformMode == Gestalt.SHAPE_TRANSFORM_MATRIX_AND_ROTATION) {
            if (theRotation.x != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setXRotation(theRotation.x);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.y != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setYRotation(theRotation.y);
                myMatrix.multiply(myRotationMatrix);
            }
            if (theRotation.z != 0.0f) {
                final TransformMatrix4f myRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myRotationMatrix.rotation.setZRotation(theRotation.z);
                myMatrix.multiply(myRotationMatrix);
            }
        }

        /* finally scale the shape */
        final TransformMatrix4f myScaleMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
        myScaleMatrix.rotation.xx = theScale.x;
        myScaleMatrix.rotation.yy = theScale.y;
        myScaleMatrix.rotation.zz = theScale.z;
        myMatrix.multiply(myScaleMatrix);

        return myMatrix;
    }
}
