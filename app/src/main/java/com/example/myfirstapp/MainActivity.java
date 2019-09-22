package com.example.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void locationScreen(View view) {
        Intent intent = new Intent(this, LocationActivity.class);
        startActivity(intent);
    }

    public void gravitySensorScreen(View view) {
        Intent intent = new Intent(this, GravitySensorActivity.class);
        startActivity(intent);
    }

    public void lightSensorScreen(View view) {
        Intent intent = new Intent(this, LightSensorActivity.class);
        startActivity(intent);
    }

    public void proximitySensorScreen(View view) {
        Intent intent = new Intent(this, ProximitySensor.class);
        startActivity(intent);
    }
}
