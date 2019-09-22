package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;
import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class AccelerometerSensorActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorManager.registerListener(this, sensor, SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        TextView accelerationX = findViewById(R.id.txtAccelerationX);
        TextView accelerationY = findViewById(R.id.txtAccelerationY);
        TextView accelerationZ = findViewById(R.id.txtAccelerationZ);

        String unit = " m/s2";

        accelerationX.setText(x + unit);
        accelerationY.setText(y + unit);
        accelerationZ.setText(z + unit);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
