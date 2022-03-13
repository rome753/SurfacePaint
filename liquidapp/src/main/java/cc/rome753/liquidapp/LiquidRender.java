package cc.rome753.liquidapp;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DYNAMIC_DRAW;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_STATIC_DRAW;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glClear;
import static android.opengl.GLES30.glClearColor;
import static android.opengl.GLES30.glDrawElements;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGenBuffers;
import static android.opengl.GLES30.glGenTextures;
import static android.opengl.GLES30.glGenVertexArrays;
import static android.opengl.GLES30.glUniformMatrix4fv;
import static android.opengl.GLES30.glUseProgram;
import static android.opengl.GLES30.glVertexAttribPointer;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class LiquidRender implements GLSurfaceView.Renderer {

    int program;
    int[] vao;
    int[] vbo;

    ByteBuffer mParticlePositionBuffer;

    LiquidManager liquidManager;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        liquidManager = new LiquidManager();

        program = ShaderUtils.loadProgram();

//        //分配内存空间,每个浮点型占4字节空间
        mParticlePositionBuffer = ByteBuffer
                .allocateDirect(2 * 4 * LiquidManager.MAX_COUNT)
                .order(ByteOrder.nativeOrder());

        vao = new int[1];
        glGenVertexArrays(1, vao, 0);
        glBindVertexArray(vao[0]);

        vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
//        glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, vertexBuffer, GL_STATIC_DRAW);
        glBufferData(GL_ARRAY_BUFFER, LiquidManager.MAX_COUNT * 4 * 2, mParticlePositionBuffer, GL_STREAM_DRAW);

        // Load the vertex data
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

//        glCullFace(GL_BACK);
//        glEnable(GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(-(height - width) / 2, 0, height, height);
        this.width = width;
        this.height = height;
    }

    int width, height;

    @Override
    public void onDrawFrame(GL10 gl) {
        liquidManager.initWorld(width, height);
        liquidManager.updatePosition(mParticlePositionBuffer);

        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 刷新vbo数据
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, LiquidManager.MAX_COUNT * 4 * 2,  mParticlePositionBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);


        // Use the program object
        glUseProgram(program);
        glBindVertexArray(vao[0]);

        glDrawArrays(GL_POINTS, 0, LiquidManager.MAX_COUNT);
    }

}