package com.example.stepsensor;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
    SharedPreferences preferences;
    int height;
    SensorEventListener sensorEventListener;
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
        sensorManager.unregisterListener(sensorEventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = getSharedPreferences("stepsensor", MODE_PRIVATE);
        height = preferences.getInt("height", 0);
        if (height == 0) {
            height = 168;
        }
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Senser Service")
                .setContentText("Đang đếm bước chân")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build();
        sensorEventListener = new SensorEventListener() {
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
        };
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor count = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (count != null) {
            sensorManager.registerListener(sensorEventListener, count, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(StepService.this, "Sensor not found", Toast.LENGTH_LONG).show();
        }

        startForeground(1, notification);

        return START_NOT_STICKY;
    }
}