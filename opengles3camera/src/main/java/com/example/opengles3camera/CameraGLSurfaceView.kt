package com.example.opengles3camera

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class CameraGLSurfaceView(context: Context?) : GLSurfaceView(context), GLSurfaceView.Renderer {

    var textureId = 0
    var surfaceTexture: SurfaceTexture? = null

    constructor(context: Context?, attrs: AttributeSet) : this(context)

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glDisable(GL_DITHER)
        glClearColor(0f, 0f, 0f, 0f)
        glEnable(GL_CULL_FACE)

        textureId = createOESTextureId()
        surfaceTexture = SurfaceTexture(textureId)
        surfaceTexture?.setOnFrameAvailableListener { requestRender() }


    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    override fun onDrawFrame(gl: GL10?) {
        TODO("Not yet implemented")
    }

    fun createOESTextureId(): Int {
        val arr = IntArray(1)
        glGenTextures(1, arr, 0)
        glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, arr[0])
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE.toFloat())
        glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE.toFloat())
        return arr[0]
    }

}