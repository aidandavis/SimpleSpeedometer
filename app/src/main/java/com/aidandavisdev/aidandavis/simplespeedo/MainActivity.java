package com.aidandavisdev.aidandavis.simplespeedo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.domain.aidandavis.simplespeedo.R;

public class MainActivity extends AppCompatActivity {
    private TextView speedText;
    private SpeedTracker speedTracker;

    private Handler updater;
    private final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            updateSpeedDisplay();
            updater.postDelayed(this, 333); // time between updates
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.toolbar_title);

        speedText = (TextView) findViewById(R.id.speedDisplay);
        speedTracker = new SpeedTracker(getBaseContext());

        updater = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();

        speedTracker.startTracking();
        startUpdatingDisplay();
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopUpdatingDisplay();
        speedTracker.stopTracking();
    }

    private void startUpdatingDisplay() {
        updater.post(updateTask);
    }

    private void stopUpdatingDisplay() {
        updater.removeCallbacks(updateTask);
    }

    private void updateSpeedDisplay() {
        speedText.setText(String.valueOf(speedTracker.getSpeedKMH()));
    }
}
