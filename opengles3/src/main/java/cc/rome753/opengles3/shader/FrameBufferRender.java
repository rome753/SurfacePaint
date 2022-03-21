package cc.rome753.opengles3.shader;

import static android.opengl.GLES20.GL_COLOR_ATTACHMENT0;
import static android.opengl.GLES20.GL_FRAMEBUFFER;
import static android.opengl.GLES20.GL_FRAMEBUFFER_COMPLETE;
import static android.opengl.GLES20.GL_RENDERBUFFER;
import static android.opengl.GLES20.GL_RGB;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindFramebuffer;
import static android.opengl.GLES20.glBindRenderbuffer;
import static android.opengl.GLES20.glCheckFramebufferStatus;
import static android.opengl.GLES20.glDisable;
import static android.opengl.GLES20.glFramebufferRenderbuffer;
import static android.opengl.GLES20.glFramebufferTexture2D;
import static android.opengl.GLES20.glGenFramebuffers;
import static android.opengl.GLES20.glGenRenderbuffers;
import static android.opengl.GLES20.glRenderbufferStorage;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glViewport;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES30.GL_DEPTH24_STENCIL8;
import static android.opengl.GLES30.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES30.GL_DEPTH_STENCIL_ATTACHMENT;
import static android.opengl.GLES30.GL_DEPTH_TEST;
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
import static android.opengl.GLES30.glActiveTexture;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindTexture;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glClear;
import static android.opengl.GLES30.glClearColor;
import static android.opengl.GLES30.glDrawArrays;
import static android.opengl.GLES30.glEnable;
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

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class FrameBufferRender extends BaseRender {

    @Override
    public OurCamera getOurCamera() {
        return ourCamera;
    }

    Simple3DShader simple3DShader = new Simple3DShader();
    ScreenShader screenShader = new ScreenShader();

    float width, height;

    int[] fbo;
    int[] tcbo;
    int[] rbo;

    private void initFramebuffer(int w, int h) {
        if (fbo != null) {
            return;
        }
        fbo = new int[1];
        glGenFramebuffers(1, fbo, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);

        // 生成纹理
        tcbo = new int[1];
        glGenTextures(1, tcbo, 0);
        glBindTexture(GL_TEXTURE_2D, tcbo[0]);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, w, h, 0, GL_RGB, GL_UNSIGNED_BYTE, null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glBindTexture(GL_TEXTURE_2D, 0);

        // 将它附加到当前绑定的帧缓冲对象
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tcbo[0], 0);

        rbo = new int[1];
        glGenRenderbuffers(1, rbo, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, rbo[0]);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH24_STENCIL8, w, h);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo[0]);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            Log.e("chao", "创建Framebuffer没有完成！");
        }
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        simple3DShader.init();
        screenShader.init();

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        initFramebuffer(width, height);
        glViewport(0, 0, width, height);
    }

    float[] modelMat = new float[16];
    float[] viewMat = new float[16];
    float[] projectionMat = new float[16];

    int rot;

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        // Clear the color buffer

//        Matrix.setIdentityM(modelMat, 0);
        Matrix.setIdentityM(viewMat, 0);
        Matrix.setIdentityM(projectionMat, 0);

        rot += 2;
        rot %= 360;
        Matrix.perspectiveM(projectionMat, 0, OurCamera.radians(ourCamera.Zoom), width / height, 0.1f, 100.0f);
        ourCamera.GetViewMatrix(viewMat);


//        // 第一阶段处理（Pass），绘制3D图形到Framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, fbo[0]);
        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);
        simple3DShader.draw(modelMat, viewMat, projectionMat, rot);

        // 第二阶段处理，把Framebuffer绘制为2D纹理
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        screenShader.draw(tcbo);

    }




    static class ScreenShader {

        float vertices[] = { // vertex attributes for a quad that fills the entire screen in Normalized Device Coordinates.
                // positions   // texCoords
                -1.0f,  1.0f,  0.0f, 1.0f,
                -1.0f, -1.0f,  0.0f, 0.0f,
                1.0f, -1.0f,  1.0f, 0.0f,

                -1.0f,  1.0f,  0.0f, 1.0f,
                1.0f, -1.0f,  1.0f, 0.0f,
                1.0f,  1.0f,  1.0f, 1.0f
        };

        int program;
        int[] vao;

        public void init() {
            program = ShaderUtils.loadProgramFramebuffer();
            //分配内存空间,每个浮点型占4字节空间
            FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
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

            glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * 4, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * 4, 2 * 4);
            glEnableVertexAttribArray(1);


            glUseProgram(program);
            int loc = glGetUniformLocation(program, "screenTexture");
            glUniform1i(loc, 1);


            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

        }

        public void draw(int[] tcbo) {
            glUseProgram(program);
            glBindVertexArray(vao[0]);
            glDisable(GL_DEPTH_TEST);
            glBindTexture(GL_TEXTURE_2D, tcbo[0]);
            glDrawArrays(GL_TRIANGLES, 0, 6);
        }

    }


    static class Simple3DShader {

        int program;
        FloatBuffer vertexBuffer;
        int[] vao;
        int[] tex;

        public void init() {

            program = ShaderUtils.loadProgram3D();
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

            tex = new int[2];
            glGenTextures(2, tex, 0);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, tex[0]);
            // 为当前绑定的纹理对象设置环绕、过滤方式
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            Bitmap bitmap = ShaderUtils.loadImageAssets("wall.jpg");
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
            glGenerateMipmap(GL_TEXTURE_2D);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, tex[1]);
            // 为当前绑定的纹理对象设置环绕、过滤方式
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            Bitmap bitmap1 = ShaderUtils.loadImageAssets("face.png");
            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap1, 0);
            glGenerateMipmap(GL_TEXTURE_2D);

            glUseProgram(program);
            int loc0 = glGetUniformLocation(program, "texture1");
            glUniform1i(loc0, 0);
            int loc1 = glGetUniformLocation(program, "texture2");
            glUniform1i(loc1, 1);

            // Load the vertex data
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0);
            glEnableVertexAttribArray(0);

            glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);
            glEnableVertexAttribArray(1);


            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        public void draw(float[] modelMat, float[] viewMat, float[] projectionMat, float rot) {
            // Use the program object
            glUseProgram(program);
            glBindTexture(GL_TEXTURE_2D, tex[0]);
            glBindTexture(GL_TEXTURE_2D, tex[1]);

            int loc1 = glGetUniformLocation(program, "view");
            glUniformMatrix4fv(loc1, 1, false, viewMat, 0);
            int loc2 = glGetUniformLocation(program, "projection");
            glUniformMatrix4fv(loc2, 1, false, projectionMat, 0);

            glBindVertexArray(vao[0]);
//Matrix.setLookAtM();
            for (int i = 0; i < cubePositions.length; i++) {
                Matrix.setIdentityM(modelMat, 0);
                Matrix.translateM(modelMat, 0, cubePositions[i][0], cubePositions[i][1], cubePositions[i][2]);
                if (i % 3 == 0) {
                    Matrix.rotateM(modelMat, 0, rot, 0.5f, 1f, 0.2f);
                }
                int loc = glGetUniformLocation(program, "model");
                glUniformMatrix4fv(loc, 1, false, modelMat, 0);

                glDrawArrays ( GL_TRIANGLES, 0, vertices.length );
            }

        }


        float vertices[] = {
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 0.0f,

                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 1.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,

                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,

                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,
                0.5f, -0.5f, -0.5f,  1.0f, 1.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                0.5f, -0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f, -0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f,  0.0f, 1.0f,

                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f,
                0.5f,  0.5f, -0.5f,  1.0f, 1.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                0.5f,  0.5f,  0.5f,  1.0f, 0.0f,
                -0.5f,  0.5f,  0.5f,  0.0f, 0.0f,
                -0.5f,  0.5f, -0.5f,  0.0f, 1.0f
        };

        float cubePositions[][] = {
                {0.0f,  0.0f,  0.0f},
                {2.0f,  5.0f, -15.0f},
                {-1.5f, -2.2f, -2.5f},
                {-3.8f, -2.0f, -12.3f},
                {2.4f, -0.4f, -3.5f},
                {-1.7f,  3.0f, -7.5f},
                {1.3f, -2.0f, -2.5f},
                {1.5f,  2.0f, -2.5f},
                {1.5f,  0.2f, -1.5f},
                {-1.3f,  1.0f, -1.5f},
        };

    }
}