package cc.rome753.opengles3.shader;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES30.GL_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES30.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES30.GL_FLOAT;
import static android.opengl.GLES30.GL_STATIC_DRAW;
import static android.opengl.GLES30.GL_TRIANGLES;
import static android.opengl.GLES30.GL_UNSIGNED_INT;
import static android.opengl.GLES30.glBindBuffer;
import static android.opengl.GLES30.glBindVertexArray;
import static android.opengl.GLES30.glBufferData;
import static android.opengl.GLES30.glClear;
import static android.opengl.GLES30.glClearColor;
import static android.opengl.GLES30.glDrawElements;
import static android.opengl.GLES30.glEnableVertexAttribArray;
import static android.opengl.GLES30.glGenBuffers;
import static android.opengl.GLES30.glGenVertexArrays;
import static android.opengl.GLES30.glGetUniformLocation;
import static android.opengl.GLES30.glUniformMatrix4fv;
import static android.opengl.GLES30.glUseProgram;
import static android.opengl.GLES30.glVertexAttribPointer;

import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AudioRender extends BaseRender {

    @Override
    public OurCamera getOurCamera() {
        return ourCamera;
    }

    public static int w = 128, h = 128;

    int[] indices;

    int program;
    ByteBuffer vertexBuffer;
    byte[] lineBytes = new byte[w];
    int lineNum = 0;

    int[] vao;
    int[] vbo;

    IntBuffer intBuffer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        program = ShaderUtils.loadProgramAudio();

        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(w * h)
                .order(ByteOrder.LITTLE_ENDIAN);
        vertexBuffer.position(0);

//        updateBuffer();
        vertexBuffer.position(0);

        vao = new int[1];
        glGenVertexArrays(1, vao, 0);
        glBindVertexArray(vao[0]);

        vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferData(GL_ARRAY_BUFFER, w * h, vertexBuffer, GL_STATIC_DRAW);

        indices = strip(w, h);

        intBuffer = IntBuffer.allocate(indices.length * 4);
        intBuffer.put(indices);
        intBuffer.position(0);

        int[] ebo = new int[1];
        glGenBuffers(1, ebo, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, intBuffer, GL_STATIC_DRAW);


        // Load the vertex data
        glVertexAttribPointer(0, 1, GL_FLOAT, false, 1, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        glEnable(GL_DEPTH_TEST);
//        glCullFace(GL_BACK);
//        glEnable(GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }

    float width, height;

    float[] modelMat = new float[16];
    float[] viewMat = new float[16];
    float[] projectionMat = new float[16];

    Random r = new Random();

    private void updateBuffer() {
        vertexBuffer.position(0);
        for (int i = 0; i < h; i++) {
            r.nextBytes(lineBytes);
//            for (int j = 0; j < W; j++) {
//                lineBytes[j] = (byte) r.nextInt(128);
//            }
            vertexBuffer.position(i * w);
            vertexBuffer.put(lineBytes);
        }
    }

    public void update(byte[] bytes) {
        vertexBuffer.position(lineNum * w);
        vertexBuffer.put(bytes);
        lineNum = (lineNum + 1) % h;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 刷新vbo数据
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        vertexBuffer.position(lineNum * w);
        glBufferSubData(GL_ARRAY_BUFFER, 0, (h - lineNum) * w, vertexBuffer);
        vertexBuffer.position(0);
        glBufferSubData(GL_ARRAY_BUFFER, (h - lineNum) * w, lineNum * w, vertexBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);

        // Use the program object
        glUseProgram(program);

//        Matrix.setIdentityM(modelMat, 0);
        Matrix.setIdentityM(viewMat, 0);
        Matrix.setIdentityM(projectionMat, 0);

        Matrix.perspectiveM(projectionMat, 0, OurCamera.radians(ourCamera.Zoom), width / height, 0.1f, 100.0f);
        ourCamera.GetViewMatrix(viewMat);

        int loc1 = glGetUniformLocation(program, "view");
        glUniformMatrix4fv(loc1, 1, false, viewMat, 0);
        int loc2 = glGetUniformLocation(program, "projection");
        glUniformMatrix4fv(loc2, 1, false, projectionMat, 0);
        Matrix.setIdentityM(modelMat, 0);
        Matrix.translateM(modelMat, 0, 0, 0, 0);
        Matrix.rotateM(modelMat, 0, rx, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMat, 0, ry, 1.0f, 0.0f, 0.0f);
        int loc3 = glGetUniformLocation(program, "model");
        glUniformMatrix4fv(loc3, 1, false, modelMat, 0);

        int loc = glGetUniformLocation(program, "lineNum");
        glUniform1i(loc, lineNum);
        loc = glGetUniformLocation(program, "w");
        glUniform1i(loc, w);
        loc = glGetUniformLocation(program, "h");
        glUniform1i(loc, h);

        glBindVertexArray(vao[0]);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

    }

    // 用GL_TRIANGLE_STRIP方式把平面上所有点转化成一个三角形条带
    public static int[] strip(int w, int h) {
        List<Integer> list = new ArrayList<>();
        for (int j = 0; j < h - 1; j++) {
            for (int i = 0; i < w - 1; i++) {
                int p = j * w + i;
                list.add(p);
                list.add(p + w);
                list.add(p + 1);
                list.add(p + 1);
                list.add(p + w);
                list.add(p + w + 1);
            }
        }
        int[] a = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            a[i] = list.get(i);
        }
        return a;
    }
}