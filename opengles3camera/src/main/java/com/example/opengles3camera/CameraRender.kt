package com.example.opengles3camera

import android.graphics.Bitmap
import android.graphics.SurfaceTexture
import android.opengl.*
import android.opengl.GLES20.*
import android.view.Surface
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.core.util.Consumer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraRender: GLSurfaceView.Renderer, Preview.SurfaceProvider {

    var surfaceTexture: SurfaceTexture? = null

    private val executor = Executors.newSingleThreadExecutor()

    var vertices = floatArrayOf( //     ---- 位置 ----       ---- 颜色 ----     - 纹理坐标 -
        -1f, -1f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,  // 左下
        1f, -1f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,  // 右下
        -1f, 1f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, // 左上
        1f, 1f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f  // 右上
    )

    val indices = intArrayOf( // 注意索引从0开始!
        0, 1, 2,  // 第一个三角形
        1, 2, 3 // 第二个三角形
    )

    var program = 0
    var vertexBuffer: FloatBuffer? = null
    var intBuffer: IntBuffer? = null
    var vao: IntArray = IntArray(1)
    var tex: IntArray = IntArray(1)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        program = ShaderUtils.loadProgram()
        //分配内存空间,每个浮点型占4字节空间
        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        //传入指定的坐标数据
        vertexBuffer!!.put(vertices)
        vertexBuffer!!.position(0)
        vao = IntArray(1)
        GLES30.glGenVertexArrays(1, vao, 0)
        GLES30.glBindVertexArray(vao[0])
        val vbo = IntArray(1)
        glGenBuffers(1, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(GL_ARRAY_BUFFER, vertices.size * 4, vertexBuffer, GL_STATIC_DRAW)

        intBuffer = IntBuffer.allocate(indices.size * 4)
        intBuffer!!.put(indices)
        intBuffer!!.position(0)
        val ebo = IntArray(1)
        glGenBuffers(1, ebo, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo[0])
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices.size * 4, intBuffer, GL_STATIC_DRAW)

//        glGenTextures(1, tex, 0)
//        glActiveTexture(GL_TEXTURE0)
//        glBindTexture(GL_TEXTURE_2D, tex[0])
//        // 为当前绑定的纹理对象设置环绕、过滤方式
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
//        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
//        val bitmap: Bitmap = ShaderUtils.loadImageAssets("face.png")
//        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
//        glGenerateMipmap(GL_TEXTURE_2D)

        tex = createOESTexture()
        surfaceTexture = SurfaceTexture(tex[0])
        surfaceTexture?.setOnFrameAvailableListener {
//            requestRender()
        }

        glUseProgram(program)
        val loc0 = glGetUniformLocation(program, "texture1")
        glUniform1i(loc0, 0)

        // Load the vertex data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4)
        glEnableVertexAttribArray(2)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
        glClearColor(0.5f, 0.5f, 0.5f, 0.5f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    var transform = FloatArray(16)

    override fun onDrawFrame(gl: GL10?) {
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT)

        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(transform)

        // Use the program object
        glUseProgram(program)
        glBindTexture(GL_TEXTURE_2D, tex[0])

//        Matrix.setIdentityM(transform, 0)
//        Matrix.translateM(transform, 1, 1f, 0f, 0f);

        var loc = glGetUniformLocation(program, "transform")
        glUniformMatrix4fv(loc, 1, false, transform, 0)
        GLES30.glBindVertexArray(vao[0])

//            glDrawArrays ( GL_TRIANGLES, 0, vertices.length );
        glDrawElements(GL_TRIANGLES, vertices.size, GL_UNSIGNED_INT, 0)
    }

    fun createOESTexture(): IntArray {
        val arr = IntArray(1)
        glGenTextures(1, arr, 0)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, arr[0])
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())
        return arr
    }


    override fun onSurfaceRequested(request: SurfaceRequest) {
        val size = request.resolution
        surfaceTexture?.setDefaultBufferSize(size.width, size.height)
        val surface = Surface(surfaceTexture)
        request.provideSurface(surface, executor, Consumer {
            surfaceTexture?.release()
            surface.release()
        })
    }

}