package gestalt.demo.advanced;


import gestalt.render.AnimatorRenderer;
import gestalt.shape.Mesh;
import gestalt.util.scenewriter.SceneWriter;


public class UsingVertexColorsToTexture
    extends AnimatorRenderer {

    public void setup() {
        displaycapabilities().backgroundcolor.r = 1f;
        displaycapabilities().backgroundcolor.g = 1f;
        displaycapabilities().backgroundcolor.b = 1f;

        createMesh();

        new SceneWriter("../mesh.obj", bin(BIN_3D), true, "../");
    }


    public Mesh createMesh() {
        int myNumberOfVertices = 480;
        float[] myVertices = new float[myNumberOfVertices * 3];
        float[] myColors = new float[myNumberOfVertices * 3];
        float[] myTexCoords = new float[myNumberOfVertices * 2];
        float[] myNormals = null;

        for (int i = 0; i < myNumberOfVertices * 3; i += 9) {
            myVertices[i + 0] = 0;
            myVertices[i + 1] = 0;
            myVertices[i + 2] = 0;
            myVertices[i + 3] = (float) Math.random() * 300 - 150;
            myVertices[i + 4] = (float) Math.random() * 300 - 150;
            myVertices[i + 5] = (float) Math.random() * 300 - 150;
            myVertices[i + 6] = (float) Math.random() * 300 - 150;
            myVertices[i + 7] = (float) Math.random() * 300 - 150;
            myVertices[i + 8] = (float) Math.random() * 300 - 150;

            myColors[i + 0] = 1f;
            myColors[i + 1] = 1f;
            myColors[i + 2] = 1f;
            myColors[i + 3] = 1f;
            myColors[i + 4] = 0;
            myColors[i + 5] = 1f;
            myColors[i + 6] = 0;
            myColors[i + 7] = 1f;
            myColors[i + 8] = 1f;
        }

        for (int i = 0; i < myTexCoords.length; i += 2) {
            myTexCoords[i + 0] = i / (float) (myTexCoords.length);
            myTexCoords[i + 1] = 0f;
        }

        Mesh myMesh = drawablefactory().mesh(false,
                                             myVertices, 3,
                                             myColors, 3,
                                             myTexCoords, 2,
                                             myNormals,
                                             MESH_TRIANGLES);
        /* add to renderer */
        bin(BIN_3D).add(myMesh);

        return myMesh;
    }


    public static void main(String[] args) {
        new UsingVertexColorsToTexture().init();
    }
}
