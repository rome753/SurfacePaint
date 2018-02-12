package cc.rome753.surfacepaint.fractal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * methods
 */
public abstract class BaseActivity extends AppCompatActivity {

    public final int ITERATE_TIMES = 64;

    protected int width;
    protected int height;

    protected int xCount;
    protected int yCount;

    protected int xSize;
    protected int ySize;

    protected Complex C;

    /**
     * is SurfaceView / TextureView available
     */
    AtomicBoolean isAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init parameters
        C = new Complex(0.285f, 0.01f);

        width = getScreenWidth(this);
        height = getScreenHeight(this);

        xCount = 8;
        yCount = 8;

        resetParameters();

        xSize = width / xCount;
        ySize = height / yCount;

        // init view
        View contentView = initContentView();
        LayoutParams params = new LayoutParams(width, height);
        params.gravity = Gravity.CENTER;
        contentView.setLayoutParams(params);

        setContentView(contentView);

        View view = (View) contentView.getParent();
        view.setBackgroundColor(Color.BLACK);

        isAvailable = new AtomicBoolean();
        isAvailable.set(false);
    }

    protected abstract View initContentView();

    /**
     * reset C, width, height, xCount, yCount
     */
    protected void resetParameters() {
    }

    /**
     * draw bitmap in SurfaceView
     * @param holder SurfaceHolder
     * @param r rect
     * @param bitmap Bitmap
     */
    void drawBitmap(SurfaceHolder holder, Rect r, Bitmap bitmap) {
        if (isAvailable.get()) {
            Canvas canvas = holder.lockCanvas(new Rect(r));
            canvas.drawBitmap(bitmap, r.left, r.top, null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * draw bitmap in TextureView
     * @param texture TextureView
     * @param r rect
     * @param bitmap Bitmap
     */
    void drawBitmap(TextureView texture, Rect r, Bitmap bitmap) {
        if (isAvailable.get()) {
            Canvas canvas = texture.lockCanvas(new Rect(r));
            canvas.drawBitmap(bitmap, r.left, r.top, null);
            texture.unlockCanvasAndPost(canvas);
        }
    }

    /**
     * calculate bitmap in rect
     * @param r rect
     * @return bitmap in rect
     */
    public Bitmap calculateBitmap(Rect r) {
        return calculateBitmap(r, C.re, C.im);
    }

    /**
     * calculate bitmap in rect, change Complex c
     * @param r rect
     * @param re c.re real
     * @param im c.im imagined
     * @return bitmap in rect
     */
    public Bitmap calculateBitmap(Rect r, float re, float im) {
        Complex z = new Complex(0f,0f);
        Complex c = new Complex(re, im);
        Bitmap bitmap = Bitmap.createBitmap(r.width(), r.height(), Bitmap.Config.RGB_565);
        for (int i = r.left - width / 2; i < r.right - width / 2; i++) {
            for (int j = r.top - height / 2; j < r.bottom - height / 2; j++) {
                z.re = i * 2f / width;
                z.im = j * 3f / height;

                int k = 0;
                for (; k < ITERATE_TIMES; k++) {
                    if (z.square() > 4) break;
                    z.mul(z);
                    z.add(c);
                }

                int color = generateColor(k);

                bitmap.setPixel((i + width / 2) % r.width(), (j + height / 2) % r.height(), color);
            }
        }
        return bitmap;
    }

    /**
     * map iterate times to rgb color
     * @param k iterate times
     * @return Color
     */
    protected int generateColor(int k) {
        int r, g, b;

        if (k < 16) {
            g = 0;
            b = 16 * k - 1;
            r = b;
        } else if (k < 32) {
            g = 16 * (k - 16);
            b = 16 * (32 - k) - 1;
            r = g;
        } else if (k < 64) {
            g = 8 * (64 - k) - 1;
            r = g;
            b = 0;
        } else { // range is 64 - 127
            r = 0;
            g = 0;
            b = 0;
        }
        return Color.argb(255, r, g, b);
    }

    public int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    public int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

}
