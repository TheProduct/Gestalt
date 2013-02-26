

package gestalt.util;


import gestalt.Gestalt;
import gestalt.shape.Mesh;
import gestalt.shape.Mesh;
import java.util.Vector;


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
}
