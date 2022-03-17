package cc.rome753.opengles3.shader;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GroupRender extends BaseRender {

    int program;
    int[] vao;
    int[] vbo;

    static int MAX_COUNT = 1000;
    static int BOUND = 1024;

    float[] pos = new float[MAX_COUNT * 2];
    float[] vel = new float[MAX_COUNT * 2];

    ByteBuffer posBuffer;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        initPos();

        program = ShaderUtils.loadProgramGroup();

//        //分配内存空间,每个浮点型占4字节空间
        posBuffer = ByteBuffer
                .allocateDirect(2 * 4 * MAX_COUNT)
                .order(ByteOrder.nativeOrder());

        vao = new int[1];
        glGenVertexArrays(1, vao, 0);
        glBindVertexArray(vao[0]);

        vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
//        glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, vertexBuffer, GL_STATIC_DRAW);
        glBufferData(GL_ARRAY_BUFFER, MAX_COUNT * 4 * 2, posBuffer, GL_STREAM_DRAW);

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
        glViewport(0, 0, width, height);
//        glViewport(-(height - width) / 2, 0, height, height);
        this.width = width;
        this.height = height;
    }

    int width, height;

    @Override
    public void onDrawFrame(GL10 gl) {

        update();

        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 刷新vbo数据
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, MAX_COUNT * 4 * 2,  posBuffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);


        // Use the program object
        glUseProgram(program);
        glBindVertexArray(vao[0]);

        glDrawArrays(GL_POINTS, 0, MAX_COUNT);
    }

    Random r = new Random();
    float dt = 1f / 60;

    void initPos() {
        for (int i = 0; i < pos.length; i++) {
            pos[i] = r.nextInt(BOUND);
            vel[i] = (r.nextFloat() - 0.5f) * 1000.0f;
        }
    }

    void update() {
        for (int i = 0; i < pos.length; i++) {
            float next = pos[i] + dt * vel[i];
            if (next < 0) {
                vel[i] = -vel[i];
            } else if (next > BOUND) {
                vel[i] = -vel[i];
            } else {
                pos[i] = next;
            }
        }
        posBuffer.position(0);
        posBuffer.asFloatBuffer().put(pos);
    }



    int[] tempPos;
    void updateAll() {
        tempPos = new int[pos.length];
        for (int i = 0; i < pos.length - 1; i+=2) {
            int iPart = posMap.get(i);
            Part part = partMap.get(iPart);
        }


    }


    static int PARTS = 8; // 8 * 8
    static int partW = BOUND / PARTS;

    // pos index -> PART index
    HashMap<Integer, Integer> posMap = new HashMap<>();

    // PART index -> Part
    HashMap<Integer, Part> partMap = new HashMap<>();

    static class Part {

        int lowX, lowY, highX, highY;

        public Part(int partIndex) {
            int px = partIndex % PARTS;
            int py = partIndex % PARTS;
            lowX = px * partW;
            highX = lowX + partW;

            lowY = py * partW;
            highY = lowY + partW;

            set = new HashSet<>();
        }

        HashSet<Integer> set;

        boolean isInPart(float x, float y) {
            return x >= lowX && x < highX && y >= lowY && y < highY;
        }
    }

}