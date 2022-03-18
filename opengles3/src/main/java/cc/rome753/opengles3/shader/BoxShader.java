package cc.rome753.opengles3.shader;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glGenVertexArrays;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BoxShader {

    int program;
    FloatBuffer vertexBuffer;
    int[] vao;
    int[] vbo;

    public BoxShader() {
        program = ShaderUtils.loadProgramBox();
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        vao = new int[1];
        glGenVertexArrays(1, vao, 0);
        glBindVertexArray(vao[0]);

        vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, vertexBuffer, GL_STATIC_DRAW);

        // Load the vertex data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * 4, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 1, GL_FLOAT, false, 4 * 4, 3 * 4);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void draw(float[] mMat, float[] vMat, float[] pMat) {
        glUseProgram(program);

        int loc = glGetUniformLocation(program, "model");
        glUniformMatrix4fv(loc, 1, false, mMat, 0);
        int loc1 = glGetUniformLocation(program, "view");
        glUniformMatrix4fv(loc1, 1, false, vMat, 0);
        int loc2 = glGetUniformLocation(program, "projection");
        glUniformMatrix4fv(loc2, 1, false, pMat, 0);

        glBindVertexArray(vao[0]);

        glDrawArrays(GL_TRIANGLE_FAN, 0, 8);
        glDrawArrays(GL_TRIANGLE_FAN, 8, 8);
    }

    float[] vertices = {
        -1.0f, -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f, 1.0f,
        -1.0f,  1.0f,  1.0f, 1.0f,
        -1.0f,  1.0f, -1.0f, 1.0f,
        1.0f,  1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,  1.0f,
        1.0f,  1.0f,  1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f, 1.0f,
        -1.0f,  1.0f,  1.0f, 1.0f,
        -1.0f,  1.0f, -1.0f, 1.0f,
        1.0f,  1.0f, -1.0f,  1.0f,
        1.0f, -1.0f, -1.0f,  1.0f,
        1.0f, -1.0f,  1.0f,  1.0f,
    };

}
