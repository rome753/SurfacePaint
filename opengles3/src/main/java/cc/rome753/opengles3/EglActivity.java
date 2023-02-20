package cc.rome753.opengles3;

import android.graphics.Bitmap;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLES20;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.nio.IntBuffer;

import cc.rome753.opengles3.egl.EglThread;
import cc.rome753.opengles3.shader.SimpleRender;

public class EglActivity extends AppCompatActivity {

    ImageView imageView;

    EglThread eglThread;

    SimpleRender simpleRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(imageView);

        imageView.setBackgroundColor(Color.BLUE);

        simpleRender = new SimpleRender();

        eglThread = new EglThread("chao-gl-thread");

        imageView.post(() -> {
            int w = imageView.getWidth();
            int h = imageView.getHeight();
            eglThread.post(() -> {
                simpleRender.onSurfaceCreated(null, null);
                simpleRender.onSurfaceChanged(null, w, h);
                while(!EglActivity.this.isFinishing() && !EglActivity.this.isDestroyed()) {
                    simpleRender.onDrawFrame(null);
                    Bitmap bitmap = getBitmapFromGL(w, h);
                    imageView.post(() -> {
                        imageView.setImageBitmap(bitmap);
                    });

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        });
    }

    Bitmap getBitmapFromGL(int w, int h) {
        IntBuffer intBuffer =  IntBuffer.allocate(w * h);
        GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, intBuffer);
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(intBuffer);
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        eglThread.destroy();
    }
}