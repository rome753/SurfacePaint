package cc.rome753.opengles3.shader;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES30.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_MAP_READ_BIT;
import static android.opengl.GLES30.GL_STATIC_DRAW;
import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.GL_UNSIGNED_INT;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindBufferBase;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glClear;
import static android.opengl.GLES30.glClearColor;
import static android.opengl.GLES30.glDrawElements;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGenBuffers;
import static android.opengl.GLES30.glGenVertexArrays;
import static android.opengl.GLES30.glGetUniformLocation;
import static android.opengl.GLES30.glMapBufferRange;
import static android.opengl.GLES30.glUniformMatrix4fv;
import static android.opengl.GLES30.glUnmapBuffer;
import static android.opengl.GLES30.glUseProgram;
import static android.opengl.GLES30.glVertexAttribPointer;

import android.opengl.GLES20;
import android.opengl.GLES30;
import android.opengl.GLES31;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ComputeRender extends BaseRender {

    int program;

    FloatBuffer vertexBuffer;
    int[] vbo;


    int iLocRadius;
    int gIndexBufferBinding;

    int frameNum = 1;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        program = ShaderUtils.loadProgramCompute();
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(128)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexBuffer.position(0);

        vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, 128, vertexBuffer, GL_STATIC_DRAW);

        glUseProgram(program);
        iLocRadius = glGetUniformLocation(program, "radius");
        gIndexBufferBinding = 0;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glUseProgram(program);
        glUniform1f(iLocRadius,(float)frameNum);

        glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, gIndexBufferBinding, vbo[0]);

        GLES31.glDispatchCompute(4, 1, 1);

        printSSBO();

        glBindBufferBase(GLES31.GL_SHADER_STORAGE_BUFFER, gIndexBufferBinding, 0);
        frameNum++;
    }

    private void printSSBO() {
        ByteBuffer bb = (ByteBuffer) glMapBufferRange(GLES31.GL_SHADER_STORAGE_BUFFER, 0, 128, GL_MAP_READ_BIT);
        if (bb != null) {
            FloatBuffer res = bb.order(ByteOrder.nativeOrder()).asFloatBuffer();
            String s = "";
            for (int i = 0; i < 32; i++) {
                float num = res.get(i);
                s = s + " " + String.format("%.2f", num);
            }
            Log.d("chao data ", s);
        } else {
            Log.d("chao data", "null");
        }
        glUnmapBuffer(GLES31.GL_SHADER_STORAGE_BUFFER);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }

}
