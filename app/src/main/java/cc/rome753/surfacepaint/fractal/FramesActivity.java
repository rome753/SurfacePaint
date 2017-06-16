package cc.rome753.surfacepaint.fractal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FramesActivity extends TextureActivity {

    ExecutorService service;

    Paint paint;

    float FACTOR_RE;
    float FACTOR_IM;

    @Override
    protected void resetParameters() {

//        C = new Complex(-0.75f, 0f);
//        C = new Complex(0.285f, 0f);
        C = new Complex(0f, -2f);

        width = 360;
        height = 640;

        xCount = 1;
        yCount = 1;

        FACTOR_RE = 0f;
        FACTOR_IM = 0.02f;
    }

    @Override
    protected View initContentView() {
        super.initContentView();
        texture.setScaleX(getScreenWidth(this) / width);
        texture.setScaleY(getScreenHeight(this) / height);
        return texture;
    }

    protected void go() {

//        final PriorityBlockingQueue<Pair<Integer, Bitmap>> queue = new PriorityBlockingQueue<>(50, new Comparator<Pair<Integer, Bitmap>>() {
//            @Override
//            public int compare(Pair<Integer, Bitmap> o1, Pair<Integer, Bitmap> o2) {
//                if(o1.first.intValue() == o2.first.intValue()) return 0;
//                return o1.first < o2.first ? -1 : 1;
//            }
//        });

        final ConcurrentHashMap<Integer, Bitmap> map = new ConcurrentHashMap<>(10);

        service = Executors.newFixedThreadPool(6);

        service.execute(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                paint = new Paint();
                paint.setColor(Color.RED);
                while (isAvailable.get()) {
                    int waitTime = map.size() == 0 ? 1000 : 1000 / map.size();
                    SystemClock.sleep(waitTime);
//                        Pair<Integer, Bitmap> pair = queue.take();

                    Bitmap bitmap = map.remove(i);
                    if (bitmap != null) {
                        Log.e("test", "take<<<<<<<<<<" + i);
                        String text = "(" + String.format("%.5f", C.re + FACTOR_RE * i)
                                +" , " + String.format("%.5f", C.im + FACTOR_IM * i)
                                + ") buffered: " + map.size();

                        drawBitmapAndText(bitmap, text);
                        i++;
                    }

                }
            }
        });

        for (int i = 0; i < 500; i++) {
            final int I = i;
            service.execute(new Runnable() {
                @Override
                public void run() {
                    float re = C.re + FACTOR_RE * I;
                    float im = C.im + FACTOR_IM * I;
                    Bitmap bitmap = calculateBitmap(new Rect(0, 0, width, height), re, im);
//                    queue.put(new Pair<>(I, bitmap));
                    map.put(I, bitmap);
                    Log.e("test", I + " put>>>>>>>>>>" + "........" + map.size());
                }
            });
        }

    }

    void drawBitmapAndText(Bitmap bitmap, String text) {
        if (isAvailable.get()) {
            Rect r = new Rect(0, 0, width, height);
            Canvas canvas = texture.lockCanvas(r);
            canvas.drawBitmap(bitmap, r.left, r.top, null);
            canvas.drawText(text, 0, 10, paint);
            texture.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    protected void onDestroy() {
        service.shutdownNow();
        super.onDestroy();
    }
}
