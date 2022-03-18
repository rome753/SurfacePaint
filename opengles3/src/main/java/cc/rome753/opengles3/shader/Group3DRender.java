package cc.rome753.opengles3.shader;

import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_TEST;
import static android.opengl.GLES20.GL_FALSE;
import static android.opengl.GLES20.GL_ONE_MINUS_SRC_ALPHA;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_SRC_ALPHA;
import static android.opengl.GLES20.GL_STREAM_DRAW;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glBufferSubData;
import static android.opengl.GLES20.glDepthMask;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glGetProgramiv;
import static android.opengl.GLES20.glGetUniformLocation;
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

public class Group3DRender extends BaseRender {

    @Override
    public OurCamera getOurCamera() {
        return ourCamera;
    }

    int program;
    int[] vao;
    int[] vbo;

    static int MAX_COUNT = 300;
    static int BOUND = 1024;

    float[] pos = new float[MAX_COUNT * 3];
    float[] vel = new float[MAX_COUNT * 3];

    ByteBuffer posBuffer;

    Random r = new Random();
    float dt = 1f / 60;
    float vmax = 240.0f;
    float vmin = 200.0f;

    int visiDis = 60 * 60 * 60;
    int closeDis = 30 * 30 * 30;

    static int PARTS = 16; // 16 * 16 * 16
    static int partW = BOUND / PARTS;

    // PART index -> Part
    HashMap<Integer, Part> partMap = new HashMap<>();

    float[] tempPos = new float[pos.length];

    // 空间中27个方块的三维坐标偏移
    int[] neighborsDir = {-1, -1, -1, -1, -1, 0, -1, -1, 1, -1, 0, -1, -1, 0, 0, -1, 0, 1, -1, 1, -1, -1, 1, 0, -1, 1, 1, 0, -1, -1, 0, -1, 0, 0, -1, 1, 0, 0, -1, 0, 0, 0, 0, 0, 1, 0, 1, -1, 0, 1, 0, 0, 1, 1, 1, -1, -1, 1, -1, 0, 1, -1, 1, 1, 0, -1, 1, 0, 0, 1, 0, 1, 1, 1, -1, 1, 1, 0, 1, 1, 1};
    int[] neighbors = new int[pos.length / 3 + 1];

    BoxShader boxShader;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        initAll();

        boxShader = new BoxShader();

        program = ShaderUtils.loadProgramGroup3D();

//        //分配内存空间,每个浮点型占4字节空间
        posBuffer = ByteBuffer
                .allocateDirect(3 * 4 * MAX_COUNT)
                .order(ByteOrder.nativeOrder());

        vao = new int[1];
        glGenVertexArrays(1, vao, 0);
        glBindVertexArray(vao[0]);

        vbo = new int[1];
        glGenBuffers(1, vbo, 0);
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
//        glBufferData(GL_ARRAY_BUFFER, vertices.length * 4, vertexBuffer, GL_STATIC_DRAW);
        glBufferData(GL_ARRAY_BUFFER, MAX_COUNT * 4 * 3, posBuffer, GL_STREAM_DRAW);

        // Load the vertex data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * 4, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

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

    @Override
    public void onDrawFrame(GL10 gl) {
        super.onDrawFrame(gl);
//        update();
        updateAll();

        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // 刷新vbo数据
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, MAX_COUNT * 4 * 3,  posBuffer);
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


        glBindVertexArray(vao[0]);
        glDrawArrays(GL_POINTS, 0, MAX_COUNT);

        // 先画不透明的物体
        // 设置标志位
        glDepthMask(false);
        // 再画半透明物体
        boxShader.draw(modelMat, viewMat, projectionMat);
        // 重置标志位
        glDepthMask(true);
    }

    void initAll() {
        for (int i = 0; i < pos.length; i++) {
            pos[i] = r.nextInt(BOUND);
            vel[i] = r.nextFloat() - 0.5f;
        }

        for (int i = 0; i < pos.length - 2; i+=3) {
            controlSpeed(i);
            Part p = findPart(i);
            p.set.add(i);
        }
    }

    void controlSpeed(int i) {
        float vx = vel[i];
        float vy = vel[i + 1];
        float vz = vel[i + 2];
        float v = (float) Math.sqrt(vx * vx + vy * vy + vz * vz);
        float mul = 1f;
        if (v >= vmax) {
            mul = vmax / v;
        } else if (v < vmin) {
            mul = vmin / v;
        }
        vel[i] *= mul;
        vel[i + 1] *= mul;
        vel[i + 2] *= mul;
    }

    Part findPart(int posIndex) {
        float x = pos[posIndex];
        float y = pos[posIndex + 1];
        float z = pos[posIndex + 2];
        int px = (int)x / partW;
        int py = (int)y / partW;
        int pz = (int)z / partW;
        int partIndex = px + py * PARTS + pz * PARTS * PARTS;
        Part p = partMap.get(partIndex);
        if (p == null) {
            p = new Part();
            partMap.put(partIndex, p);
            Log.d("chao", "create part " + partIndex);
        }
        return p;
    }

    void findNeighborParts(int posIndex) {
        float x = pos[posIndex];
        float y = pos[posIndex + 1];
        float z = pos[posIndex + 2];
        int px = (int)x / partW;
        int py = (int)y / partW;
        int pz = (int)z / partW;
        int iNeighbor = 0;
        for (int i = 0; i < neighborsDir.length - 2; i+=3) {
            int rx = px + neighborsDir[i];
            int ry = py + neighborsDir[i + 1];
            int rz = pz + neighborsDir[i + 2];
            if (rx >= 0 && rx < PARTS && ry >= 0 && ry < PARTS && rz >= 0 && rz < PARTS) {
                int partIndex = rx + ry * PARTS + rz * PARTS * PARTS;
                Part p = partMap.get(partIndex);
                if (p != null) {
                    for (Integer iPos: p.set) {
                        neighbors[iNeighbor++] = iPos;
                    }
                }
            }
        }
        neighbors[iNeighbor] = -1; // 结尾标志
    }

    void updateAll() {
        long time = System.currentTimeMillis();

        for (int i = 0; i < pos.length - 2; i+=3) {

            findNeighborParts(i);

            // 计算群速度：可见的别的点的速度和
            float vxSum = 0;
            float vySum = 0;
            float vzSum = 0;
            float count = 0;
            for (Integer j : neighbors) {
                if (j == -1) {
                    break;
                }
                if (j == i) {
                    continue;
                }
                float dx = pos[i] - pos[j];
                float dy = pos[i + 1] - pos[j + 1];
                float dz = pos[i + 2] - pos[j + 2];
                float dis = dx * dx + dy * dy + dz * dz;
                if (dis < visiDis) {
                    vxSum += -dx;
                    vySum += -dy;
                    vzSum += -dz;
                    count++;
                } else if (dis < closeDis) {
                    vxSum -= dx;
                    vySum -= dy;
                    vzSum -= dz;
                    count++;
                }
            }

            // 改变速度
            if (count > 0) {
                float vxAve = vxSum / count;
                float vyAve = vySum / count;
                float vzAve = vzSum / count;
                vel[i]     = vel[i]     * 0.8f + vxAve * 0.2f;
                vel[i + 1] = vel[i + 1] * 0.8f + vyAve * 0.2f;
                vel[i + 2] = vel[i + 2] * 0.8f + vzAve * 0.2f;
                controlSpeed(i);
            }

            // 更新位置，保存到temp里
            float x1 = pos[i] + dt * vel[i];
            float y1 = pos[i + 1] + dt * vel[i + 1];
            float z1 = pos[i + 2] + dt * vel[i + 2];
            boolean out = false;
            if (x1 < 0 || x1 >= BOUND) {
                vel[i] = -vel[i];
                out = true;
            }
            if (y1 < 0 || y1 >= BOUND) {
                vel[i + 1] = -vel[i + 1];
                out = true;
            }
            if (z1 < 0 || z1 >= BOUND) {
                vel[i + 2] = -vel[i + 2];
                out = true;
            }
            if (out) { // 将要出界，那么位置不变
                tempPos[i] = pos[i];
                tempPos[i + 1] = pos[i + 1];
                tempPos[i + 2] = pos[i + 2];
            } else {
                tempPos[i] = x1;
                tempPos[i + 1] = y1;
                tempPos[i + 2] = z1;
            }
        }

        // 更新Part，写入新速度
        for (int i = 0; i < pos.length - 2; i+=3) {
            Part p0 = findPart(i);
            pos[i] = tempPos[i];
            pos[i + 1] = tempPos[i + 1];
            pos[i + 2] = tempPos[i + 2];
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
//        Log.d("chao", "updae time " + time);
    }

    static class Part {

        public Part() {
            set = new HashSet<>();
        }

        HashSet<Integer> set;

    }

}