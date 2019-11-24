package com.example.myfirstapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ActivityRecognitionClientActivity extends AppCompatActivity {

    public static final int ACTIVITY_RECOGNITION_CLIENT_REQUEST = 1;
    public static final String TAG = "ActivityRecClientAct";

    private ActivityRecognitionClient client = null;
    private PendingIntent pendingIntent = null;
    private Intent intent = null;
    private WakeLock wakeLock = null;
    private boolean isCharging = false;
    private BroadcastReceiver powerStatusReceiver;

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

        // Make the activity scrollable
        ((TextView)findViewById(R.id.txtRecognizedActivity)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView)findViewById(R.id.txtSentCacheContent)).setMovementMethod(new ScrollingMovementMethod());

        setWakeLock();
        registerPowerConnectionReceiver();
    }

    private void setWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyApp::MyWakelockTag");

        Log.d(TAG, "onCreate: PARTIAL_WAKE_LOCK supported? : " + powerManager.isWakeLockLevelSupported(PowerManager.PARTIAL_WAKE_LOCK));
    }

    public void activateAPI(View view) {
        Log.d(TAG, "Activate API button");

        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }

        if (this.pendingIntent == null) {
            Log.d(TAG, "Creating service");
            this.createService();
        }
        findViewById(R.id.activateActivityRecognition).setEnabled(false);
        findViewById(R.id.activateActivityRecognition2).setEnabled(true);
    }

    public void deactivateAPI(View view) {
        Log.d(TAG, "Deactivate API button");
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

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

    private void registerPowerConnectionReceiver() {
        this.powerStatusReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);

                // Are we charging / charged?
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                        status == BatteryManager.BATTERY_STATUS_FULL;

                if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                    Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
                    sendData();
                } else {
                    intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
                    Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter powerConnectedFilter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        this.getApplicationContext().registerReceiver(this.powerStatusReceiver, powerConnectedFilter);

        IntentFilter powerDisconnectedFilter = new IntentFilter(Intent.ACTION_POWER_DISCONNECTED);
        this.getApplicationContext().registerReceiver(this.powerStatusReceiver, powerDisconnectedFilter);
    }

    private void sendData() {
        String cacheContent = readCache();
        if (!cacheContent.isEmpty()) {
            TextView cacheView = findViewById(R.id.txtSentCacheContent);
            cacheView.append("------SEND START------\n");
            cacheView.append(cacheContent);
            cacheView.append("------SEND END--------\n");
            clearCache();
        }
    }

    private String readCache() {
        StringBuilder sb = new StringBuilder();
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "ActivityRecognition");

            File gpxfile = new File(root, "MyData.csv");
            FileReader reader = new FileReader(gpxfile);

            try (BufferedReader bufferedReader = new BufferedReader(reader)) {
                String sCurrentLine;
                while ((sCurrentLine = bufferedReader.readLine()) != null)
                {
                    sb.append(sCurrentLine).append("\n");
                }
            }
        } catch (FileNotFoundException ex) {
            return ""; // cache empty
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private void clearCache() {
        File root = new File(Environment.getExternalStorageDirectory(), "ActivityRecognition");
        File gpxfile = new File(root, "MyData.csv");
        gpxfile.delete(); // Clear the cache
    }

    private void unregisterPowerConnectionReceiver() {
        this.getApplicationContext().unregisterReceiver(this.powerStatusReceiver);
    }
}
