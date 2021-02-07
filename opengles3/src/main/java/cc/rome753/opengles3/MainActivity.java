package cc.rome753.opengles3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import cc.rome753.opengles3.shader.FractorRender;
import cc.rome753.opengles3.shader.LightingRender;
import cc.rome753.opengles3.shader.OurCamera;
import cc.rome753.opengles3.shader.ParticleSystemRenderer;
import cc.rome753.opengles3.shader.Simple3DRender;

public class MainActivity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;
    OurCamera ourCamera;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        setContentView(glSurfaceView);

        glSurfaceView.setEGLContextClientVersion(3);

        Simple3DRender render;

//        glSurfaceView.setRenderer(new LightingRender());
//        glSurfaceView.setRenderer(render = new Simple3DRender());
//        glSurfaceView.setRenderer(new SimpleRender());
//        glSurfaceView.setRenderer(new ParticleSystemRenderer(this));
        glSurfaceView.setRenderer(new FractorRender());

//        ourCamera = render.getOurCamera();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                ourCamera.ProcessMouseScroll((1 - factor) * 2000);
                Log.d("chao", "scaleGestureDetector.onScale " + factor);

                float dx = detector.getFocusX() - fx;
                float dy = detector.getFocusY() - fy;
                ourCamera.ProcessMouseMovement(-dx, dy, true);
                fx = detector.getFocusX();
                fy = detector.getFocusY();
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.d("chao", "scaleGestureDetector.onScaleBegin");
                fx = detector.getFocusX();
                fy = detector.getFocusY();
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                Log.d("chao", "scaleGestureDetector.onScaleEnd");
            }
        });
    }

    float fx, fy;

    ScaleGestureDetector scaleGestureDetector;

    float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (ourCamera == null) {
            return false;
        }
        // 多指缩放视图
        if (event.getPointerCount() > 1) {
            return scaleGestureDetector.onTouchEvent(event);
        }
        // 单指移动视角
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX();
                y = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - x;
                float dy = event.getRawY() - y;
                // dt用固定值，如果用时间差值会造成视图莫名其妙的跳动
                float dt = 1f / 60;

                if (Math.abs(dx) > 5) {
                    ourCamera.ProcessKeyboard(dx > 0 ? OurCamera.Camera_Movement.LEFT : OurCamera.Camera_Movement.RIGHT, dt);
                }
                if (Math.abs(dy) > 5) {
                    ourCamera.ProcessKeyboard(dy > 0 ? OurCamera.Camera_Movement.BACKWARD : OurCamera.Camera_Movement.FORWARD, dt);
                }
                x = event.getRawX();
                y = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
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