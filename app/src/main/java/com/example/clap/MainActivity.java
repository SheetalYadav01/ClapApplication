package com.example.clap;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;  // ✅ Add this line
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private TextView clapCountText;
    private MediaPlayer clapSound;
    private int clapCount = 0;
    private boolean isNear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clapCountText = findViewById(R.id.clapCountText);
        clapSound = MediaPlayer.create(this, R.raw.clap);
        ImageView clapImage = findViewById(R.id.clapImage);  // ✅ No error now

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (proximitySensor == null) {
            Toast.makeText(this, "Proximity sensor not available!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float distance = event.values[0];

        if (distance < proximitySensor.getMaximumRange()) {
            isNear = true;
        } else {
            if (isNear) {
                clapCount++;
                updateClapCount();
                playClapSound();
                isNear = false;
            }
        }
    }

    private void updateClapCount() {
        clapCountText.setText("Claps: " + clapCount);
        if (clapCount % 5 == 0) {
            Toast.makeText(this, "👏 " + clapCount + " claps!", Toast.LENGTH_SHORT).show();
        }
    }

    private void playClapSound() {
        if (clapSound != null) {
            if (clapSound.isPlaying()) {
                clapSound.stop();
                clapSound.release();
                clapSound = MediaPlayer.create(this, R.raw.clap);
            }
            clapSound.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }

    @Override
    protected void onDestroy() {
        if (clapSound != null) {
            clapSound.release();
            clapSound = null;
        }
        super.onDestroy();
    }
}
