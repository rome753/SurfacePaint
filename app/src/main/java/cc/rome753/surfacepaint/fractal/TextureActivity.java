package cc.rome753.surfacepaint.fractal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.util.Pair;
import android.view.TextureView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class TextureActivity extends BaseActivity {

    TextureView texture;

    @Override
    protected View initContentView() {
        texture = new TextureView(this);
        texture.setSurfaceTextureListener(mListener);
        return texture;
    }

    TextureView.SurfaceTextureListener mListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            isAvailable.set(true);

            //draw background
            Canvas canvas = texture.lockCanvas();
            canvas.drawColor(Color.BLACK);
            texture.unlockCanvasAndPost(canvas);

            go();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            isAvailable.set(false);
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    protected void go() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        final ArrayList<Rect> list = new ArrayList<>(xCount * yCount);

        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                Rect r = new Rect(i * xSize, j * ySize, (i + 1) * xSize, (j + 1) * ySize);
                list.add(r);

            }
        }

        final BlockingQueue<Pair<Integer, Bitmap>> queue = new ArrayBlockingQueue<>(10);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while(isAvailable.get()){
                    try {
                        Pair<Integer, Bitmap> pair = queue.take();
                        drawBitmap(texture, list.get(pair.first), pair.second);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        Collections.shuffle(list);
        for (int i = 0;i<list.size();i++) {
            final int finalI = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = calculateBitmap(list.get(finalI));
                    try {
                        queue.put(new Pair<>(finalI, bitmap));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    synchronized void syncDraw(Rect r, Bitmap bitmap){
        drawBitmap(texture, r, bitmap);
    }

}
