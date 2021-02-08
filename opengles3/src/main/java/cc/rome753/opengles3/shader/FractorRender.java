package cc.rome753.opengles3.shader;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cc.rome753.opengles3.Utils;

import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES30.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_LINEAR;
import static android.opengl.GLES30.GL_REPEAT;
import static android.opengl.GLES30.GL_STATIC_DRAW;
import static android.opengl.GLES30.GL_TEXTURE0;
import static android.opengl.GLES30.GL_TEXTURE1;
import static android.opengl.GLES30.GL_TEXTURE_2D;
import static android.opengl.GLES30.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES30.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES30.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.GL_UNSIGNED_INT;
import static android.opengl.GLES30.glActiveTexture;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindTexture;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glClear;
import static android.opengl.GLES30.glClearColor;
import static android.opengl.GLES30.glDrawElements;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGenBuffers;
import static android.opengl.GLES30.glGenTextures;
import static android.opengl.GLES30.glGenVertexArrays;
import static android.opengl.GLES30.glGenerateMipmap;
import static android.opengl.GLES30.glGetUniformLocation;
import static android.opengl.GLES30.glTexParameteri;
import static android.opengl.GLES30.glUniform1i;
import static android.opengl.GLES30.glUniformMatrix4fv;
import static android.opengl.GLES30.glUseProgram;
import static android.opengl.GLES30.glVertexAttribPointer;
import static android.opengl.GLES30.glViewport;

public class FractorRender implements GLSurfaceView.Renderer {

    float vertices[] = new float[400 * 400 * 2];

    int program;
    FloatBuffer vertexBuffer;
    int[] vao;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        int p = 0;
        for (int i = -200; i < 200; i++) {
            for (int j = -200; j < 200; j++) {
                vertices[p] = i;
                vertices[p + 1] = j;
                p += 2;
            }
        }

        program = ShaderUtils.loadProgramFractor();
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        //传入指定的坐标数据
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        vao = new int[1];
        glGenVertexArrays(1, vao, 0);
        glBindVertexArray(vao[0]);

        int[] vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, vertexBuffer, GL_STATIC_DRAW);

        // Load the vertex data
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
    }

//    float[] c = {0.285f, 0.01f};
    float[] c = {0.185f, 0.01f};

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT);

        // Use the program object
        glUseProgram(program);

        c[0] += 0.0001f;
//        c[1] += 0.00001f;
        int loc = glGetUniformLocation(program, "c");
        glUniform2fv(loc, 1, c, 0);

        glBindVertexArray(vao[0]);

//            glDrawArrays ( GL_TRIANGLES, 0, vertices.length );
        glDrawArrays(GL_POINTS, 0, vertices.length);

    }
}