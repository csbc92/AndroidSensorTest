package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ActivityRecognitionClientActivity extends AppCompatActivity {

    public static final int ACTIVITY_RECOGNITION_CLIENT_REQUEST = 1;
    public static final String TAG = "ActivityRecClientAct";

    private ActivityRecognitionClient client = null;
    private PendingIntent pendingIntent = null;
    private Intent intent = null;
    private WakeLock wakeLock = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_client);

        TextView txtRecognizedAcivity = findViewById(R.id.txtRecognizedActivity);
        txtRecognizedAcivity.setMovementMethod(new ScrollingMovementMethod());

        this.client = ActivityRecognition.getClient(this.getApplicationContext());
        // Create an Intent for the ActivityRecognitionClient
        this.intent = new Intent(this, RecognitionIntentService.class);
        Handler h = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle reply = msg.getData();
                String activityMessage = reply.getString("ACTIVITY_MESSAGE");
                TextView txtRecognizedAcivity = findViewById(R.id.txtRecognizedActivity);
                txtRecognizedAcivity.append(activityMessage + "\n");
            }
        };
        this.intent.putExtra("messenger", new Messenger(h));
    }

    public void activateAPI(View view) {
        Log.d(TAG, "Activate API button");

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");

        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        if (this.pendingIntent == null) {
            Log.d(TAG, "Creating service");
            this.createService();
        }
    }

    public void deactivateAPI(View view) {
        Log.d(TAG, "Deactivate API button");
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        if (this.pendingIntent != null) {
            Log.d(TAG, "Removing service");
            client.removeActivityUpdates(this.pendingIntent);
            TextView txtRecognizedAcivity = findViewById(R.id.txtRecognizedActivity);
            txtRecognizedAcivity.setText("");
            this.pendingIntent = null;
        }
    }

    private void createService() {
        ActivityRecognitionClient client = ActivityRecognition.getClient(this.getApplicationContext());


        this.pendingIntent = PendingIntent.getService(this, this.ACTIVITY_RECOGNITION_CLIENT_REQUEST, this.intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Register the Intent to be called whenever there is changes in the Activity (Still, Walking, etc) of the mobile phone
        Task longRunning = client.requestActivityUpdates(0, this.pendingIntent);

        longRunning.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d(TAG, "requestActivityUpdates() completed successfully: ");
            }
        });

        longRunning.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "requestActivityUpdates() failed: " + e);
            }
        });
    }
}
