/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package cc.rome753.surfacepaint.opengl;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class OpenGLActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private OpenGLRenderer openGLRenderer;
    private boolean rendererSet = false;

    private SensorManager sensorManager;

    float lastX=0, lastY=0;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {


            if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                try {
                    if(Math.abs(lastX - x) > 0.5
                     || Math.abs(lastY - y) > 0.5) {
                        openGLRenderer.rotate(x/2 + lastX, y/2 + lastY, z);
                    }
                }catch (Exception e){
                }
                lastX = x;
                lastY = y;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurfaceView = new GLSurfaceView(this);

            glSurfaceView.setEGLContextClientVersion(2);

            // Assign our renderer.
            openGLRenderer = new OpenGLRenderer(this);
            glSurfaceView.setRenderer(openGLRenderer);

//        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        sensorManager.unregisterListener(sensorEventListener);
//        if (rendererSet) {
//            glSurfaceView.onPause();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        sensorManager.registerListener(sensorEventListener,
//                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//                SensorManager.SENSOR_DELAY_GAME);
//        if (rendererSet) {
//            glSurfaceView.onResume();
//        }
//    }
}