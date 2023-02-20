package cc.rome753.opengles3.egl;

import android.os.Handler;
import android.os.HandlerThread;

public class EglThread {
    Handler mHandler;
    EglCore mEglCore;

    public EglThread(String name) {
        HandlerThread ht = new HandlerThread(name);
        ht.start();

        mHandler = new Handler(ht.getLooper());
        mHandler.post(() -> {
            mEglCore = new EglCore();
            mEglCore.makeCurrent();
        });
    }

    public void post(Runnable r) {
        if (mHandler != null) {
            mHandler.post(r);
        }
    }


    public void destroy() {
        if (mHandler != null) {
            mHandler.post(() -> {
                mEglCore.release();
                mHandler.getLooper().quitSafely();
            });
        }
    }


}
