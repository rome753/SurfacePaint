package com.example.opengles3camera.yuv

import android.media.Image
import android.opengl.GLES20.*
import android.opengl.GLES30
import com.example.opengles3camera.ShaderUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer

class YUVShader {

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

    var tex: IntArray = IntArray(3) // yuv


    fun initAll() {
        program = ShaderUtils.loadProgramYUV()
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

        glUseProgram(program)
        glGenTextures(3, tex, 0)
        for (i in 0..2) {
            glActiveTexture(GL_TEXTURE0 + i)
            glBindTexture(GL_TEXTURE_2D, tex[i])
            // 为当前绑定的纹理对象设置环绕、过滤方式
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
//            val bitmap: Bitmap = ShaderUtils.loadImageAssets("face.png")
//            GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
//            glGenerateMipmap(GL_TEXTURE_2D)

            val loc0 = glGetUniformLocation(program, "tex$i")
            glUniform1i(loc0, tex[i])
        }


        // Load the vertex data
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * 4, 0)
        glEnableVertexAttribArray(0)
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * 4, 3 * 4)
        glEnableVertexAttribArray(1)
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * 4, 6 * 4)
        glEnableVertexAttribArray(2)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        GLES30.glBindVertexArray(0)
    }

    fun draw(image: Image) {
        val planes = image.planes

        val p0 = planes[0]
        val p1 = planes[1]
        val p2 = planes[2]

        val b0 = p0.buffer
        val b1 = p1.buffer
        val b2 = p2.buffer

        val r0 = b0.remaining()
        val r1 = b1.remaining()
        val r2 = b2.remaining()

        val w0 = p0.rowStride
        var h0 = r0 / w0
        if (r0 % w0 > 0) h0++
        val w1 = p1.rowStride
        var h1 = r1 / w1
        if (r1 % w1 > 1) h1++
        val w2 = p2.rowStride
        var h2 = r2 / w2
        if (r2 % w2 > 2) h2++

        glUseProgram(program)

        glActiveTexture(tex[0])
        glBindTexture(GL_TEXTURE_2D, tex[0])
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, w0, h0, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, b0)

        glActiveTexture(tex[1])
        glBindTexture(GL_TEXTURE_2D, tex[1])
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, w1, h1, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, b1)

        glActiveTexture(tex[2])
        glBindTexture(GL_TEXTURE_2D, tex[2])
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, w2, h2, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, b2)

        glDrawElements(GL_TRIANGLES, vertices.size, GL_UNSIGNED_INT, 0)
    }

}