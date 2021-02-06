package cc.rome753.opengles3;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

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
import static android.opengl.GLES30.glUseProgram;
import static android.opengl.GLES30.glVertexAttribPointer;
import static android.opengl.GLES30.glViewport;

public class SimpleRender implements GLSurfaceView.Renderer {

        float vertices[] = {
                // 位置              // 颜色
                0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,   // 右下
                -0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,   // 左下
                0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f    // 顶部
        };

        final int indices[] = { // 注意索引从0开始!
                0, 1, 2, // 第一个三角形
        };

        int program;
        FloatBuffer vertexBuffer;
        FloatBuffer colorBuffer;
        IntBuffer intBuffer;
        int[] vao;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            program = ShaderUtils.loadProgram();
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


//            intBuffer = ByteBuffer.allocate(indices.length * 4)
//                    .order(ByteOrder.nativeOrder())
//                    .asIntBuffer(); // 这样创建会变成null，native找不到，崩溃
            intBuffer = IntBuffer.allocate(indices.length * 4);
            intBuffer.put(indices);
            intBuffer.position(0);

            int[] ebo = new int[1];
            glGenBuffers(1, ebo, 0);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.length * 4, intBuffer, GL_STATIC_DRAW);

            // Load the vertex data
            glVertexAttribPointer ( 0, 3, GL_FLOAT, false, 6 * 4, 0 );
            glEnableVertexAttribArray ( 0 );

            glVertexAttribPointer ( 1, 3, GL_FLOAT, false, 6 * 4, 3 * 4 );
            glEnableVertexAttribArray ( 1 );

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);

            glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // Clear the color buffer
            glClear ( GL_COLOR_BUFFER_BIT );

            // Use the program object
            glUseProgram ( program );
            glBindVertexArray(vao[0]);

//            glDrawArrays ( GL_TRIANGLES, 0, vertices.length );
            glDrawElements ( GL_TRIANGLES, vertices.length, GL_UNSIGNED_INT, 0 );

        }
    }