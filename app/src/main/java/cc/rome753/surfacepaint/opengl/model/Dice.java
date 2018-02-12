package cc.rome753.surfacepaint.opengl.model;

import cc.rome753.surfacepaint.opengl.data.VertexArray;
import cc.rome753.surfacepaint.opengl.programs.TextureShaderProgram;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static cc.rome753.surfacepaint.opengl.Constants.BYTES_PER_FLOAT;

public class Dice {
    private static final int POSITION_COMPONENT_COUNT = 3;
    private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT
            + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT;


    static float x = 0.5f;
    static float s = 0.25f;
    static float t = 0.5f;

    private static final float[] VERTEX_DATA = {
            // Order of coordinates: X, Y, Z, S, T
            -x, -x, -x, s, t,
             x, -x, -x, s, t*2,
             x, -x,  x, 0, t*2,
            -x, -x,  x, 0, t,
            -x,  x,  x, 0, 0,
            -x,  x, -x, s, 0,
             x,  x, -x, s*2, 0,
             x, -x, -x, s*2, t,

             x,  x,  x, s*2, t,
             x, -x,  x, s, t,
            -x, -x,  x, s, t*2,
            -x,  x,  x, s*2, t*2,
            -x,  x, -x, s*3, t*2,
             x,  x, -x, s*3, t,
             x, -x, -x, s*3, 0,
             x, -x,  x, s*2, 0,
    };

    private final VertexArray vertexArray;

    public Dice() {
        vertexArray = new VertexArray(VERTEX_DATA);
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
        glDrawArrays(GL_TRIANGLE_FAN, 0, 8);
        glDrawArrays(GL_TRIANGLE_FAN, 8, 8);
    }
}
