package cc.rome753.opengles3.shader;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.TextView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cc.rome753.opengles3.MainActivity;
import cc.rome753.opengles3.R;

public class BaseRender implements GLSurfaceView.Renderer {

    private static final int ONE_SEC = 1000000000;
    // Measure the frame rate
    long totalFrames = -10000;
    private int mFrames;
    private long mStartTime;
    private long mTime;

    TextView fpsTextView;
    OurCamera ourCamera = new OurCamera(new float[]{0.0f, 0.0f, 3.0f});

    public OurCamera getOurCamera() {
        return null;
    }

    public void setFpsTextView(TextView fpsTextView) {
        this.fpsTextView = fpsTextView;
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
        long time = System.nanoTime();
        if (time - mTime > ONE_SEC) {
            if (totalFrames < 0) {
                totalFrames = 0;
                mStartTime = time - 1;
            }
            final float fps = mFrames / ((float) time - mTime) * ONE_SEC;
            float avefps = totalFrames / ((float) time - mStartTime) * ONE_SEC;
            mTime = time;
            mFrames = 0;

            TextView tv = fpsTextView;
            if (tv != null) {
                tv.post(() -> {
                    tv.setText("fps: " + fps + "\nave: " + avefps);
                });
            }
        }
        mFrames++;
        totalFrames++;
    }
}
