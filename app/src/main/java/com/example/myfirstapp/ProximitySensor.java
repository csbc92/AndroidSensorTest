package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import static android.hardware.SensorManager.SENSOR_DELAY_NORMAL;

public class ProximitySensor extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity_sensor);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sensorManager.registerListener(this, sensor, SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float proximityData = event.values[0];
        String proximityInterpretation;

        if (proximityData > 0) {
            proximityInterpretation = "Away";
        } else {
            proximityInterpretation = "Near";
        }

        TextView txtProximity = findViewById(R.id.txtProximity);

        txtProximity.setText(proximityInterpretation);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
