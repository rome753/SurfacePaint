package cc.rome753.surfacepaint.opengl;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class DiceActivity extends Activity {

    private GLSurfaceView glSurfaceView;
    private DiceRenderer diceRenderer;
    private boolean rendererSet = false;

    private SensorManager sensorManager;

    float lastX = 0, lastY = 0;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {


            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                try {
                    if (Math.abs(lastX - x) > 0.5 || Math.abs(lastY - y) > 0.5) {
                        diceRenderer.rotate(x / 2 + lastX, y / 2 + lastY, z);
                    }
                } catch (Exception e) {
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
        diceRenderer = new DiceRenderer(this);
        glSurfaceView.setRenderer(diceRenderer);

        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        setContentView(glSurfaceView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        if (rendererSet) {
            glSurfaceView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        if (rendererSet) {
            glSurfaceView.onResume();
        }
    }
}