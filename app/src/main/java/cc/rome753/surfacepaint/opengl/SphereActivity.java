package cc.rome753.surfacepaint.opengl;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class SphereActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private SphereRenderer openGLRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

        glSurfaceView.setEGLContextClientVersion(2);

        // Assign our renderer.
        openGLRenderer = new SphereRenderer(this);
        glSurfaceView.setRenderer(openGLRenderer);

        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
    }

}