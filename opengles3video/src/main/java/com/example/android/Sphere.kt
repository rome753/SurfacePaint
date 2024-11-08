package com.example.android

import android.opengl.GLES20
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Sphere {
    var R = 0.5f
    var layers = 100
    var sectors = 360
    var alpha = (Math.PI / layers).toFloat()
    var beta = (Math.PI * 2 / sectors).toFloat()
    var sUnit = 1f / sectors
    var tUnit = 1f / layers

    var vertexBuffer: FloatBuffer? = null
    var vao: IntArray = IntArray(1)

    fun draw() {
        GLES30.glBindVertexArray(vao[0])
        for (i in 0 until layers) {
            GLES20.glDrawArrays(
                GLES20.GL_TRIANGLE_STRIP,
                i * (sectors + 1) * 2, (sectors + 1) * 2
            )
        }
    }

    companion object {
        private const val BYTES_PER_FLOAT = 4
        private const val POSITION_COMPONENT_COUNT = 3
        private const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        private const val COMPONENT_COUNT =
            POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT
        private const val STRIDE =
            (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

        fun getStripArray(array: FloatArray, rows: Int, cols: Int, stride: Int): FloatArray {
            val stripArray = FloatArray((rows - 1) * (cols * 2) * stride)
            for (i in 0 until rows - 1) {
                for (j in 0 until cols) {
                    var srcPos = (i * cols + j) * stride
                    var destPos = srcPos * 2
                    System.arraycopy(array, srcPos, stripArray, destPos, stride)
                    destPos = destPos + stride
                    srcPos = ((i + 1) * cols + j) * stride
                    System.arraycopy(array, srcPos, stripArray, destPos, stride)
                }
            }
            return stripArray
        }
    }

    init {
        var vertices = FloatArray((layers + 1) * (sectors + 1) * COMPONENT_COUNT)
        var index = -1
        for (i in 0 until layers + 1) {
            val r = (R * Math.sin((i * alpha).toDouble())).toFloat()
            val y = (R * Math.cos((i * alpha).toDouble())).toFloat()
            val t = i * tUnit
            for (j in 0 until sectors + 1) {
                val z = (r * Math.sin((j * beta).toDouble())).toFloat()
                val x = (r * Math.cos((j * beta).toDouble())).toFloat()
                val s = 1f - j * sUnit
                vertices[++index] = x
                vertices[++index] = y
                vertices[++index] = z
                vertices[++index] = s
                vertices[++index] = t
            }
        }

        vertices = getStripArray(vertices, layers + 1, sectors + 1, COMPONENT_COUNT)


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
        GLES20.glGenBuffers(1, vbo, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0])
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            vertices.size * 4,
            vertexBuffer,
            GLES20.GL_STATIC_DRAW
        )
        GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, 5 * 4, 0)
        GLES20.glEnableVertexAttribArray(0)
        GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 5 * 4, 3 * 4)
        GLES20.glEnableVertexAttribArray(1)

    }
}