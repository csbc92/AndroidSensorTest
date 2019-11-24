package com.example.myfirstapp;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecognitionIntentService extends IntentService {

    public static final String TAG = "RecognitionIntService";

    public RecognitionIntentService() {
        super("RecognitionIntentService");
        Log.d(TAG, "Created RecognitionIntentService");
    }

    /*@Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onHandleIntent was called");

        if (!ActivityRecognitionResult.hasResult(intent)) {
            Log.d(TAG, "ActivityRecognitionResult has no result");
            return;
        }


        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        DetectedActivity detectedActivity = result.getMostProbableActivity();
        int confidence = detectedActivity.getConfidence();

        switch (detectedActivity.getType()) {
            case DetectedActivity.IN_VEHICLE:
                Log.d(TAG, "IN_VEHICLE: " + confidence);
                break;

            case DetectedActivity.ON_BICYCLE:
                Log.d(TAG, "ON_BICYCLE: " + confidence);
                break;

            case DetectedActivity.ON_FOOT:
                Log.d(TAG, "ON_FOOT: " + confidence);
                break;

            case DetectedActivity.RUNNING:
                Log.d(TAG, "RUNNING: " + confidence);
                break;

            case DetectedActivity.STILL:
                Log.d(TAG, "STILL: " + confidence);
                break;

            case DetectedActivity.TILTING:
                Log.d(TAG, "TILTING: " + confidence);
                break;

            case DetectedActivity.UNKNOWN:
                Log.d(TAG, "UNKNOWN: " + confidence);
                break;

            case DetectedActivity.WALKING:
                Log.d(TAG, "WALKING: " + confidence);
                break;

        }
    }*/

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent was called");

        if (!ActivityRecognitionResult.hasResult(intent)) {
            Log.d(TAG, "ActivityRecognitionResult has no result");
            return;
        }


        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

        DetectedActivity detectedActivity = result.getMostProbableActivity();
        int confidence = detectedActivity.getConfidence();
        StringBuilder sb = new StringBuilder();
        String type = "";

        switch (detectedActivity.getType()) {
            case DetectedActivity.IN_VEHICLE:
                Log.d(TAG, "IN_VEHICLE: " + confidence);
                sb.append("IN_VEHICLE: ").append(confidence);
                type = "IN_VEHICLE";
                break;

            case DetectedActivity.ON_BICYCLE:
                Log.d(TAG, "ON_BICYCLE: " + confidence);
                sb.append("ON_BICYCLE: ").append(confidence);
                type = "ON_BICYCLE";
                break;

            case DetectedActivity.ON_FOOT:
                Log.d(TAG, "ON_FOOT: " + confidence);
                sb.append("ON_FOOT: ").append(confidence);
                type = "ON_FOOT";
                break;

            case DetectedActivity.RUNNING:
                Log.d(TAG, "RUNNING: " + confidence);
                sb.append("RUNNING: ").append(confidence);
                type = "RUNNING";
                break;

            case DetectedActivity.STILL:
                Log.d(TAG, "STILL: " + confidence);
                sb.append("STILL: ").append(confidence);
                type = "STILL";
                break;

            case DetectedActivity.TILTING:
                Log.d(TAG, "TILTING: " + confidence);
                sb.append("TILTING: ").append(confidence);
                type = "TILTING";
                break;

            case DetectedActivity.UNKNOWN:
                Log.d(TAG, "UNKNOWN: " + confidence);
                sb.append("UNKNOWN: ").append(confidence);
                type = "UNKNOWN";
                break;

            case DetectedActivity.WALKING:
                Log.d(TAG, "WALKING: " + confidence);
                sb.append("WALKING: ").append(confidence);
                type = "WALKING";
                break;

        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        sb.append(" ").append(format.format(new Date()));

        String toWrite = format.format(new Date()) + "," + type + "," + confidence + "\n";
        writeToFile(toWrite);
        Bundle bundle = intent.getExtras();
        notify(bundle, sb.toString());
    }

    private void notify(Bundle bundle, String message) {
        if (bundle != null) {
            Messenger messenger = (Messenger) bundle.get("messenger");
            Message msg = Message.obtain();
            bundle.putString("ACTIVITY_MESSAGE", message);
            msg.setData(bundle); //put the data here
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }
        }
    }

    private void writeToFile(String content) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "ActivityRecognition");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, "MyData.csv");
            FileWriter writer = new FileWriter(gpxfile, true);
            writer.write(content);
            writer.flush();
            writer.close();

            System.out.println("Wrote following to file: " + content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
