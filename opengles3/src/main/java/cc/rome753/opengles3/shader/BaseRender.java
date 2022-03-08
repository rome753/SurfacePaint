package cc.rome753.opengles3.shader;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BaseRender implements GLSurfaceView.Renderer {

    OurCamera ourCamera = new OurCamera(new float[]{0.0f, 0.0f, 3.0f});

    public OurCamera getOurCamera() {
        return null;
    }

    float rx = 0;
    float ry = 0;

    public void rotModel(float dx, float dy) {
        rx += dx / 5f;
        rx %= 360;
        ry += dy / 5f;
        ry %= 360;
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
