package cc.rome753.surfacepaint.opengl.model;

import android.opengl.GLES20;

import cc.rome753.surfacepaint.opengl.data.VertexArray;
import cc.rome753.surfacepaint.opengl.programs.TextureShaderProgram;

import static cc.rome753.surfacepaint.opengl.Constants.BYTES_PER_FLOAT;

public class Sphere {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int COMPONENT_COUNT = POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    float R = 0.5f;

    int layers = 100;
    int sectors = 360;
    float alpha = (float) (Math.PI / layers);
    float beta = (float) (Math.PI * 2 / sectors);

    float sUnit = 1f / sectors;
    float tUnit = 1f / layers;

    private final VertexArray vertexArray;

    public Sphere() {

        float vertices[] = new float[(layers + 1) * (sectors + 1) * COMPONENT_COUNT];

        int index = -1;
        for (int i = 0; i < layers + 1; i++) {
            float r = (float) (R * Math.sin(i * alpha));
            float y = (float) (R * Math.cos(i * alpha));

            float t = i * tUnit;
            for (int j = 0; j < sectors + 1; j++) {
                float z = (float) (r * Math.sin(j * beta));
                float x = (float) (r * Math.cos(j * beta));

                float s = 1f - j * sUnit;

                vertices[++index] = x;
                vertices[++index] = y;
                vertices[++index] = z;
                vertices[++index] = s;
                vertices[++index] = t;
            }
        }

        float stripVertices[] = getStripArray(vertices, layers + 1, sectors + 1, COMPONENT_COUNT);
        vertexArray = new VertexArray(stripVertices);
    }

    public static float[] getStripArray(float[] array, int rows, int cols, int stride) {
        float stripArray[] = new float[(rows - 1) * (cols * 2) * stride];

        for (int i = 0; i < rows - 1; i++) {
            for (int j = 0; j < cols; j++) {
                int srcPos = (i * cols + j) * stride;
                int destPos = srcPos * 2;
                System.arraycopy(array, srcPos, stripArray, destPos, stride);

                destPos = destPos + stride;
                srcPos = ((i + 1) * cols + j) * stride;
                System.arraycopy(array, srcPos, stripArray, destPos, stride);
            }
        }
        return stripArray;
    }

    public void bindData(TextureShaderProgram textureProgram) {
        vertexArray.setVertexAttrPointer(
                0,
                textureProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT,
                STRIDE);

        vertexArray.setVertexAttrPointer(
                POSITION_COMPONENT_COUNT,
                textureProgram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COORDINATES_COMPONENT_COUNT,
                STRIDE);
    }

    public void draw() {
        for (int i = 0; i < layers; i++) {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,
                    i * (sectors + 1) * 2, ((sectors + 1) * 2));
        }
    }
}
