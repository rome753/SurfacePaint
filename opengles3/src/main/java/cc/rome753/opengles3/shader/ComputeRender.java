package cc.rome753.opengles3.shader;

import android.opengl.GLES31;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ComputeRender extends BaseRender {

    int program;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        program = ShaderUtils.loadProgramCompute();
        GLES31.glDispatchCompute(32, 1, 1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
    }

}
