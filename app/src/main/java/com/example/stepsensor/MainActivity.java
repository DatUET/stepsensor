package com.example.stepsensor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    TextView stepCountTextView, caloriesCountTextView, distanceCountTextView;
    SensorManager sensorManager;
    boolean isRunning = false;
    EditText heightEdit;
    Button okButton;
    int height;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addControl();
        addEvent();
    }

    private void addControl() {
        stepCountTextView = findViewById(R.id.stepCountTextView);
        caloriesCountTextView = findViewById(R.id.caloriesCountTextView);
        distanceCountTextView = findViewById(R.id.distanceCountTextView);
        heightEdit = findViewById(R.id.heightEdit);
        okButton = findViewById(R.id.okButton);
        preferences = getSharedPreferences("stepsensor", MODE_PRIVATE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    private void addEvent() {
        height = preferences.getInt("height", 0);
        if (height == 0) {
            height = 168;
        }
        if (height > 0) {
            heightEdit.setText(height + "");
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String height = heightEdit.getText().toString();
                heightEdit.setFocusable(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (!TextUtils.isEmpty(height.trim())) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("height", Integer.parseInt(height));
                    editor.apply();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
        Sensor count = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (count != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    if (isRunning) {
                        float step = event.values[0];
                        double distance = height / 100000.0 * step * 0.25;
                        double calories = step * 0.05;
                        String caloriesStr = String.format("%.2f", calories);
                        String distanceStr = String.format("%.2f", distance);

                        stepCountTextView.setText(step + "");
                        caloriesCountTextView.setText(caloriesStr);
                        distanceCountTextView.setText(distanceStr);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            }, count, SensorManager.SENSOR_DELAY_FASTEST);
        } else {
            Toast.makeText(MainActivity.this, "Sensor not found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }
}
