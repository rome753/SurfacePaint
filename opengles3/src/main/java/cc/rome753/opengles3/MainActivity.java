package cc.rome753.opengles3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cc.rome753.opengles3.shader.LightingRender;
import cc.rome753.opengles3.shader.Simple3DRender;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        setContentView(glSurfaceView);

        glSurfaceView.setEGLContextClientVersion(3);

        glSurfaceView.setRenderer(new LightingRender());
//        glSurfaceView.setRenderer(new Simple3DRender());
//        glSurfaceView.setRenderer(new SimpleRender());
//        glSurfaceView.setRenderer(new ParticleSystemRenderer(this));

    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }
}