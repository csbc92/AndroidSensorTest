package com.example.myfirstapp;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

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

        switch (detectedActivity.getType()) {
            case DetectedActivity.IN_VEHICLE:
                Log.d(TAG, "IN_VEHICLE: " + confidence);
                sb.append("IN_VEHICLE: ").append(confidence);
                break;

            case DetectedActivity.ON_BICYCLE:
                Log.d(TAG, "ON_BICYCLE: " + confidence);
                sb.append("ON_BICYCLE: ").append(confidence);
                break;

            case DetectedActivity.ON_FOOT:
                Log.d(TAG, "ON_FOOT: " + confidence);
                sb.append("ON_FOOT: ").append(confidence);
                break;

            case DetectedActivity.RUNNING:
                Log.d(TAG, "RUNNING: " + confidence);
                sb.append("RUNNING: ").append(confidence);
                break;

            case DetectedActivity.STILL:
                Log.d(TAG, "STILL: " + confidence);
                sb.append("STILL: ").append(confidence);
                break;

            case DetectedActivity.TILTING:
                Log.d(TAG, "TILTING: " + confidence);
                sb.append("TILTING: ").append(confidence);
                break;

            case DetectedActivity.UNKNOWN:
                Log.d(TAG, "UNKNOWN: " + confidence);
                sb.append("UNKNOWN: ").append(confidence);
                break;

            case DetectedActivity.WALKING:
                Log.d(TAG, "WALKING: " + confidence);
                sb.append("WALKING: ").append(confidence);
                break;

        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        sb.append(" ").append(format.format(new Date()));

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Messenger messenger = (Messenger) bundle.get("messenger");
            Message msg = Message.obtain();
            bundle.putString("ACTIVITY_MESSAGE", sb.toString());
            msg.setData(bundle); //put the data here
            try {
                messenger.send(msg);
            } catch (RemoteException e) {
                Log.i("error", "error");
            }
        }
    }
}
