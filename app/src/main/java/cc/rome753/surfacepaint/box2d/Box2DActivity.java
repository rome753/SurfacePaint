package cc.rome753.surfacepaint.box2d;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import cc.rome753.surfacepaint.R;

public class Box2DActivity extends Activity {


    private PhysicsLayout physicsLayout;
    private SensorManager sensorManager;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0] * 10;
                float y = event.values[1] * 10;
                physicsLayout.setGravity(-x, y);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box2d);
        physicsLayout = findViewById(R.id.phy);
        for(int i = 0; i < 10; i++) {
            View view = new View(this);
            view.setBackgroundResource(R.drawable.soccer);

            physicsLayout.addView(view, new FrameLayout.LayoutParams(100, 100));
        }
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);

    }
}