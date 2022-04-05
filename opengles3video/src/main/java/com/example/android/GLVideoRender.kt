package com.example.android

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.util.concurrent.Executors
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLVideoRender: GLSurfaceView.Renderer {

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

    var sphere: Sphere? = null

    var ourCamera: OurCamera = OurCamera(floatArrayOf(0.0f, 0.0f, 3.0f))

    var rx = 0f
    var ry = 0f

    fun rotModel(dx: Float, dy: Float) {
        rx += dx / 5f
        rx %= 360f
        ry += dy / 5f
        ry %= 360f
    }

    var modelMat = FloatArray(16)
    var viewMat = FloatArray(16)
    var projectionMat = FloatArray(16)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

        sphere = Sphere()

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
        glEnable(GL_DEPTH_TEST)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        this.width = width.toFloat()
        this.height = height.toFloat()
    }

    var width: Float = 1f
    var height: Float = 1f

    var transform = FloatArray(16)

    override fun onDrawFrame(gl: GL10?) {
        // Clear the color buffer
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT);

        surfaceTexture?.updateTexImage()
        surfaceTexture?.getTransformMatrix(transform)

        // Use the program object
        glUseProgram(program)
//        glBindTexture(GL_TEXTURE_2D, tex[0])
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0])


        //        Matrix.setIdentityM(modelMat, 0);

//        Matrix.setIdentityM(modelMat, 0);
        Matrix.setIdentityM(viewMat, 0)
        Matrix.setIdentityM(projectionMat, 0)

        Matrix.perspectiveM(
            projectionMat,
            0,
            OurCamera.radians(ourCamera.Zoom),
            width / height,
            0.1f,
            100.0f
        )
        ourCamera.GetViewMatrix(viewMat)

        val loc1 = glGetUniformLocation(program, "view")
        glUniformMatrix4fv(loc1, 1, false, viewMat, 0)
        val loc2 = glGetUniformLocation(program, "projection")
        glUniformMatrix4fv(loc2, 1, false, projectionMat, 0)
        Matrix.setIdentityM(modelMat, 0)
        Matrix.translateM(modelMat, 0, 0f, 0f, 0f)
        Matrix.rotateM(modelMat, 0, rx, 0.0f, 1.0f, 0.0f)
        Matrix.rotateM(modelMat, 0, ry, 1.0f, 0.0f, 0.0f)
        val loc3 = glGetUniformLocation(program, "model")
        glUniformMatrix4fv(loc3, 1, false, modelMat, 0)

//        glDrawElements(GL_TRIANGLES, vertices.size, GL_UNSIGNED_INT, 0)
        sphere?.draw()
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

}