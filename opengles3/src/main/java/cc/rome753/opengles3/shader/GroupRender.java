package cc.rome753.opengles3.shader;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
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

    static int MAX_COUNT = 200;
    static int BOUND = 1024;

    float[] pos = new float[MAX_COUNT * 2];
    float[] vel = new float[MAX_COUNT * 2];

    ByteBuffer posBuffer;


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

//        initPos();
        initAll();

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

//        update();
        updateAll();

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
    float vmax = 200.0f;
    float vmin = 160.0f;

    int visiDis = 300 * 300;
    int closeDis = 120 * 120;

    void initAll() {
        for (int i = 0; i < pos.length; i++) {
            pos[i] = r.nextInt(BOUND);
            vel[i] = r.nextFloat() - 0.5f;
        }

        for (int i = 0; i < pos.length - 1; i+=2) {
            controlSpeed(i);
            Part p = findPart(i);
            p.set.add(i);
        }
    }

    void controlSpeed(int i) {
        float vx = vel[i];
        float vy = vel[i + 1];
        float vv = (float) Math.sqrt(vx * vx + vy * vy);
        float mul = 1f;
        if (vv >= vmax) {
            mul = vmax / vv;
        } else if (vv < vmin) {
            mul = vmin / vv;
        }
        vel[i] *= mul;
        vel[i + 1] *= mul;
    }

    Part findPart(int posIndex) {
        float x = pos[posIndex];
        float y = pos[posIndex + 1];
        int px = (int)x / partW;
        int py = (int)y / partW;
        int partIndex = px + py * PARTS;
        Part p = partMap.get(partIndex);
        if (p == null) {
            p = new Part();
            partMap.put(partIndex, p);
            Log.d("chao", "create part " + partIndex);
        }
        return p;
    }

    int[] neighbors = {
            -1, -1, 0, -1, 1, -1,
            -1,  0, 0,  0, 1,  0,
            -1,  1, 0,  1, 1,  1,
    };

    HashSet<Integer> findNeighborParts(int posIndex) {
        HashSet<Integer> all = new HashSet<>();
        float x = pos[posIndex];
        float y = pos[posIndex + 1];
        int px = (int)x / partW;
        int py = (int)y / partW;
        for (int i = 0; i < neighbors.length - 1; i+=2) {
            int rx = px + neighbors[i];
            int ry = py + neighbors[i + 1];
            if (rx >= 0 && rx < PARTS && ry >= 0 && ry < PARTS) {
                int partIndex = rx + ry * PARTS;
                Part p = partMap.get(partIndex);
                if (p != null) {
                    all.addAll(p.set);
                }
            }
        }
        return all;
    }

    float[] tempPos = new float[pos.length];

    void updateAll() {
        long time = System.currentTimeMillis();
        float[] tempPos = new float[pos.length];

        for (int i = 0; i < pos.length - 1; i+=2) {
            HashSet<Integer> set = findNeighborParts(i);

            // 计算群速度：可见的别的点的速度和
            float vxSum = 0;
            float vySum = 0;
            float count = 0;
            for (Integer j : set) {
                if (i != j) {
                    float dx = pos[i] - pos[j];
                    float dy = pos[i + 1] - pos[j + 1];
                    float dis = dx * dx + dy * dy;
                    if (dis < visiDis) {
                        vxSum += pos[j] - pos[i];
                        vySum += pos[j + 1] - pos[i + 1];
                        count++;
                    } else if (dis < closeDis) {
                        vxSum -= pos[j] - pos[i];
                        vySum -= pos[j + 1] - pos[i + 1];
                        count++;
                    }
                }
            }

            // 改变速度
            if (count > 0) {
                float vxAve = vxSum / count;
                float vyAve = vySum / count;
                vel[i] = vel[i] * 0.8f + vxAve * 0.2f;
                vel[i + 1] = vel[i + 1] * 0.8f + vyAve * 0.2f;
                controlSpeed(i);
            }

            // 更新位置，保存到temp里
            float x1 = pos[i] + dt * vel[i];
            float y1 = pos[i + 1] + dt * vel[i + 1];
            boolean out = false;
            if (x1 < 0 || x1 >= BOUND) {
                vel[i] = -vel[i];
                out = true;
            }
            if (y1 < 0 || y1 >= BOUND) {
                vel[i + 1] = -vel[i + 1];
                out = true;
            }
            if (out) {
                tempPos[i] = pos[i];
                tempPos[i + 1] = pos[i + 1];
            } else {
                tempPos[i] = x1;
                tempPos[i + 1] = y1;
            }
        }

        // 更新Part，写入新速度
        for (int i = 0; i < pos.length - 1; i+=2) {
            Part p0 = findPart(i);
            pos[i] = tempPos[i];
            pos[i + 1] = tempPos[i + 1];
            Part p1 = findPart(i);
            if (p0 == p1) {
                continue;
            }
            p0.set.remove(i);
            p1.set.add(i);
        }

        posBuffer.position(0);
        posBuffer.asFloatBuffer().put(pos);

        time = System.currentTimeMillis() - time;
        Log.d("chao", "updae time " + time);
    }


    static int PARTS = 8; // 8 * 8
    static int partW = BOUND / PARTS;

    // PART index -> Part
    HashMap<Integer, Part> partMap = new HashMap<>();

    static class Part {

        public Part() {
            set = new HashSet<>();
        }

        HashSet<Integer> set;

    }

}