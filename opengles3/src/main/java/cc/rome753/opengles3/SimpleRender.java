package cc.rome753.opengles3;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES30.*;

public class SimpleRender implements GLSurfaceView.Renderer {

    float vertices[] = {
//     ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,   // 右上
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,   // 右下
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,   // 左下
            -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f    // 左上
    };

    final int indices[] = { // 注意索引从0开始!
            0, 1, 3, // 第一个三角形
            1, 2, 3  // 第二个三角形
    };

    int program;
    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    IntBuffer intBuffer;
    int[] vao;
    int[] tex;

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

        tex = new int[2];
        glGenTextures(2, tex, 0);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tex[0]);
        // 为当前绑定的纹理对象设置环绕、过滤方式
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        Bitmap bitmap = Utils.loadImageAssets("wall.jpg");
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);
        glGenerateMipmap(GL_TEXTURE_2D);

        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, tex[1]);
        // 为当前绑定的纹理对象设置环绕、过滤方式
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        Bitmap bitmap1 = Utils.loadImageAssets("face.png");
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap1, 0);
        glGenerateMipmap(GL_TEXTURE_2D);

        glUseProgram(program);
        int loc0 = glGetUniformLocation(program, "texture1");
        glUniform1i(loc0, 0);
        int loc1 = glGetUniformLocation(program, "texture2");
        glUniform1i(loc1, 1);

        // Load the vertex data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4);
        glEnableVertexAttribArray(2);

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
        glClear(GL_COLOR_BUFFER_BIT);

        // Use the program object
        glUseProgram(program);
        glBindTexture(GL_TEXTURE_2D, tex[0]);
        glBindTexture(GL_TEXTURE_2D, tex[1]);
        glBindVertexArray(vao[0]);

//            glDrawArrays ( GL_TRIANGLES, 0, vertices.length );
        glDrawElements(GL_TRIANGLES, vertices.length, GL_UNSIGNED_INT, 0);

    }
}