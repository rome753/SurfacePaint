package cc.rome753.opengles3;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.*;

public class SimpleRender implements GLSurfaceView.Renderer {

        private final float[] vertexPoints = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        int program;
        FloatBuffer vertexBuffer;
        FloatBuffer colorBuffer;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //分配内存空间,每个浮点型占4字节空间
            vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            //传入指定的坐标数据
            vertexBuffer.put(vertexPoints);
            vertexBuffer.position(0);

            int[] vbo = new int[1];
            glGenBuffers(1, vbo, 0);
            glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
            glBufferData(GL_ARRAY_BUFFER, vertexPoints.length * 4, vertexBuffer, GL_STATIC_DRAW);

            program = ShaderUtils.loadProgram();

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

            // Load the vertex data
//            glVertexAttribPointer ( 0, 3, GL_FLOAT, false, 0, vertexBuffer );
            glVertexAttribPointer ( 0, 3, GL_FLOAT, false, 12, 0 );
            glEnableVertexAttribArray ( 0 );

            glDrawArrays ( GL_TRIANGLES, 0, 3 );

        }
    }