package com.example.myfirstapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_client);


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

        // Make the activity scrollable
        ((TextView)findViewById(R.id.txtRecognizedActivity)).setMovementMethod(new ScrollingMovementMethod());

    }

    public void activateAPI(View view) {
        Log.d(TAG, "Activate API button");

        if (this.pendingIntent == null) {
            Log.d(TAG, "Creating service");
            this.createService();
        }
        findViewById(R.id.activateActivityRecognition).setEnabled(false);
        findViewById(R.id.activateActivityRecognition2).setEnabled(true);
    }

    public void deactivateAPI(View view) {
        Log.d(TAG, "Deactivate API button");
        if (this.pendingIntent != null) {
            Log.d(TAG, "Removing service");
            client.removeActivityUpdates(this.pendingIntent);
            TextView txtRecognizedActivity = findViewById(R.id.txtRecognizedActivity);
            txtRecognizedActivity.setText("");
            this.pendingIntent = null;
        }
        findViewById(R.id.activateActivityRecognition).setEnabled(true);
        findViewById(R.id.activateActivityRecognition2).setEnabled(false);
    }

    private void createService() {
        ActivityRecognitionClient client = ActivityRecognition.getClient(this.getApplicationContext());


        this.pendingIntent = PendingIntent.getService(this, this.ACTIVITY_RECOGNITION_CLIENT_REQUEST, this.intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Register the Intent to be called whenever there is changes in the Activity (Still, Walking, etc) of the mobile phone
        Task longRunning = client.requestActivityUpdates(10*1000, this.pendingIntent);

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
