package cc.rome753.opengles3;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        setContentView(glSurfaceView);

        glSurfaceView.setEGLContextClientVersion(3);
        glSurfaceView.setRenderer(new MyRender());


    }

    private class MyRender implements GLSurfaceView.Renderer {

        private float[] vertexPoints = new float[]{
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        int program;
        FloatBuffer vertexBuffer;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //分配内存空间,每个浮点型占4字节空间
            vertexBuffer = ByteBuffer.allocateDirect(vertexPoints.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            //传入指定的坐标数据
            vertexBuffer.put(vertexPoints);
            vertexBuffer.position(0);

            program = ShaderUtils.loadProgram();


            glClearColor(0.5f, 1f, 0.5f, 0.5f);
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
            glVertexAttribPointer ( 0, 3, GL_FLOAT, false, 0, vertexBuffer );
            glEnableVertexAttribArray ( 0 );

            glDrawArrays ( GL_TRIANGLES, 0, 3 );
        }
    }
}