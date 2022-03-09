package cc.rome753.liquidapp;

import android.app.Application;

public class App extends Application {

    private static App app;

    public static App getApp() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
