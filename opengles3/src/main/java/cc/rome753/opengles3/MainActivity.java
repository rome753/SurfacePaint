package cc.rome753.opengles3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import cc.rome753.opengles3.shader.LightingRender;
import cc.rome753.opengles3.shader.OurCamera;
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
        glSurfaceView.setRenderer(render = new Simple3DRender());
//        glSurfaceView.setRenderer(new SimpleRender());
//        glSurfaceView.setRenderer(new ParticleSystemRenderer(this));

        ourCamera = render.getOurCamera();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                ourCamera.ProcessMouseScroll((1 - factor) * 2000);
                Log.d("chao", "scaleGestureDetector.onScale " + factor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                Log.d("chao", "scaleGestureDetector.onScaleBegin");
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }

    ScaleGestureDetector scaleGestureDetector;

    float x, y;
    long t;

    // 移动方向，在第一次ACTION_MOVE触发确定
    Boolean isMoveHorizontal;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 多指缩放视图
        if (event.getPointerCount() > 1) {
            return scaleGestureDetector.onTouchEvent(event);
        }
        // 单指移动视角
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMoveHorizontal = null;
                x = event.getRawX();
                y = event.getRawY();
                t = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getRawX() - x;
                float dy = event.getRawY() - y;
                float dt = (System.currentTimeMillis() - t) / 500f;
                if (isMoveHorizontal == null) {
                    int trigger = 20;
                    // 防止第一次移动距离太小，分不清方向
                    if (Math.abs(dx) < trigger && Math.abs(dy) < trigger) break;
                    isMoveHorizontal = Math.abs(dx) > Math.abs(dy);
                }
//                ourCamera.ProcessMouseMovement(-dx, dy, true);
                if (isMoveHorizontal) {
                    ourCamera.ProcessKeyboard(dx > 0 ? OurCamera.Camera_Movement.LEFT : OurCamera.Camera_Movement.RIGHT, dt);
                } else {
                    ourCamera.ProcessKeyboard(dy > 0 ? OurCamera.Camera_Movement.BACKWARD : OurCamera.Camera_Movement.FORWARD, dt);
                }
                x = event.getRawX();
                y = event.getRawY();
                t = System.currentTimeMillis();
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