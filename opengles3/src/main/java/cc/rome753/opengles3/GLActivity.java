package cc.rome753.opengles3;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cc.rome753.opengles3.shader.AudioRender;
import cc.rome753.opengles3.shader.BaseRender;
import cc.rome753.opengles3.shader.ComputeRender;
import cc.rome753.opengles3.shader.FractalRender;
import cc.rome753.opengles3.shader.Group3DRender;
import cc.rome753.opengles3.shader.GroupRender;
import cc.rome753.opengles3.shader.LightingRender;
import cc.rome753.opengles3.shader.OurCamera;
import cc.rome753.opengles3.shader.ParticleSystemRenderer;
import cc.rome753.opengles3.shader.FrameBufferRender;
import cc.rome753.opengles3.shader.Simple3DRender;
import cc.rome753.opengles3.shader.SimpleRender;
import cc.rome753.opengles3.shader.TransformFeedbackRenderer;

public class GLActivity extends AppCompatActivity {

    GLSurfaceView glSurfaceView;
    OurCamera ourCamera;
    BaseRender render;

    TextView fpsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glSurfaceView = new GLSurfaceView(this);
        setContentView(glSurfaceView);

        FrameLayout content = findViewById(android.R.id.content);
        fpsTextView = new TextView(this);
        fpsTextView.setTextColor(Color.RED);
        content.addView(fpsTextView);

        glSurfaceView.setEGLContextClientVersion(3);

        String renderName = getIntent().getStringExtra("render");
        switch (renderName) {
            case "Simple":
                glSurfaceView.setRenderer(render = new SimpleRender());
                break;
            case "Simple3D":
                glSurfaceView.setRenderer(render = new Simple3DRender());
                break;
            case "FrameBuffer":
                glSurfaceView.setRenderer(render = new FrameBufferRender());
                break;
            case "Fractal":
                glSurfaceView.setRenderer(render = new FractalRender());
                break;
            case "Audio":
            case "Record":
                glSurfaceView.setRenderer(render = new AudioRender());
                break;
            case "Lighting":
                glSurfaceView.setRenderer(render = new LightingRender());
                break;
            case "Group":
                glSurfaceView.setRenderer(render = new GroupRender());
                glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                break;
            case "Group3D":
                glSurfaceView.setRenderer(render = new Group3DRender());
                glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                break;
            case "ParticleSystem":
                glSurfaceView.setRenderer(render = new ParticleSystemRenderer());
                break;
            case "TransformFeedback":
                glSurfaceView.setRenderer(render = new TransformFeedbackRenderer());
                glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (render instanceof TransformFeedbackRenderer) {
                            ((TransformFeedbackRenderer) render).updateCenter(event.getX(), event.getY());
                        }
                        return true;
                    }
                });
                break;
            case "Compute":
                glSurfaceView.setRenderer(render = new ComputeRender());
                glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                break;
        }

        render.setFpsTextView(fpsTextView);
        ourCamera = render.getOurCamera();

        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float factor = detector.getScaleFactor();
                ourCamera.ProcessMouseScroll((1 - factor) * 2000);
                Log.d("chao", "scaleGestureDetector.onScale " + factor);

                float dx = detector.getFocusX() - fx;
                float dy = detector.getFocusY() - fy;
                ourCamera.ProcessMouseMovement(-dx / 2, dy / 2, true);
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

                // 防止多指跳动
                if (Math.abs(dx) > 100 || Math.abs(dy) > 100) return true;

                if (render instanceof Simple3DRender || render instanceof FrameBufferRender) {
                    ourCamera.ProcessKeyboard(dx > 0 ? OurCamera.Camera_Movement.LEFT : OurCamera.Camera_Movement.RIGHT, Math.abs(dx) / 1000);
                    ourCamera.ProcessKeyboard(dy > 0 ? OurCamera.Camera_Movement.BACKWARD : OurCamera.Camera_Movement.FORWARD, Math.abs(dy) / 1000);
                }
                x = event.getRawX();
                y = event.getRawY();


                render.rotModel(dx, dy);

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