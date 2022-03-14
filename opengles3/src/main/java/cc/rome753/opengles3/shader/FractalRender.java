package cc.rome753.opengles3.shader;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BACK;
import static android.opengl.GLES20.GL_CULL_FACE;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_FRONT;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glCullFace;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
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

public class FractalRender extends BaseRender {

    @Override
    public OurCamera getOurCamera() {
        return ourCamera;
    }

    float vertices[] = new float[401 * 401 * 2];
    int[] indices;

    int program;
    FloatBuffer vertexBuffer;
    int[] vao;

    IntBuffer intBuffer;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        int p = 0;
        for (int i = -200; i <= 200; i++) {
            for (int j = -200; j <= 200; j++) {
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

        indices = strip(401, 401);

        intBuffer = IntBuffer.allocate(indices.length * 4);
        intBuffer.put(indices);
        intBuffer.position(0);

        int[] ebo = new int[1];
        glGenBuffers(1, ebo, 0);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, intBuffer, GL_STATIC_DRAW);


        // Load the vertex data
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 2 * 4, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

//        glCullFace(GL_BACK);
//        glEnable(GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
    }

    float width, height;
//    float[] c = {0.285f, 0.01f};
    float[] c = {0.225f, 0.01f};

    float[] modelMat = new float[16];
    float[] viewMat = new float[16];
    float[] projectionMat = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Use the program object
        glUseProgram(program);

        c[0] += 0.0001f;
//        c[1] += 0.00001f;
        int loc = glGetUniformLocation(program, "c");
        glUniform2fv(loc, 1, c, 0);

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