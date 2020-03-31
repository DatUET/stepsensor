package com.example.stepsensor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.stepsensor.App.CHANNEL_ID;

public class StepService extends Service {
    SensorManager sensorManager;
    String text = "";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final int height = intent.getIntExtra("height", 0);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .build();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor count = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (count != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (true) {
                        float step = event.values[0];
                        double distance = height / 100000.0 * step * 0.25;
                        double calories = step * 0.05;
                        String caloriesStr = String.format("%.2f", calories);
                        String distanceStr = String.format("%.2f", distance);
                        text = step + " | " + caloriesStr + " | " + distanceStr;
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, count, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(StepService.this, "Sensor not found", Toast.LENGTH_LONG).show();
        }

        startForeground(1, notification);

        return START_NOT_STICKY;
    }
}