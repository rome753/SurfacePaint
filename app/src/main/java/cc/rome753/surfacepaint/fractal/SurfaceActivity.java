package cc.rome753.surfacepaint.fractal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rome753 on 2016/12/14.
 */

public class SurfaceActivity extends BaseActivity {

    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        holder.addCallback(mCallback);
    }

    @Override
    protected View initContentView() {
        SurfaceView surface = new SurfaceView(this);
        holder = surface.getHolder();
        return surface;
    }

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            isAvailable.set(true);

            //draw background
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.BLACK);
            holder.unlockCanvasAndPost(canvas);

            canvas = holder.lockCanvas(new Rect(0,0,100,100));
            canvas.drawColor(Color.RED);
            holder.unlockCanvasAndPost(canvas);
            go();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            isAvailable.set(false);
        }
    };

    protected void go() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        ArrayList<Rect> list = new ArrayList<>(xCount * yCount);

        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                Rect r = new Rect(i * xSize, j * ySize, (i + 1) * xSize, (j + 1) * ySize);
                list.add(r);

            }
        }

        //打乱排序
        Collections.shuffle(list);

        for (final Rect r : list) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = calculateBitmap(r);
                    syncDraw(r, bitmap);
                }
            });
        }
    }

    synchronized void syncDraw(Rect r, Bitmap bitmap){
        drawBitmap(holder, r, bitmap);
    }
}
