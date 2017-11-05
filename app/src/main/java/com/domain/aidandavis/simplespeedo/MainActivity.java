package com.domain.aidandavis.simplespeedo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView speedText;
    SpeedTracker speedTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.toolbar_title);

        speedText = (TextView) findViewById(R.id.speedDisplay);
        speedTracker = new SpeedTracker(getBaseContext());
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

        speedTracker.stopTracking();
        stopUpdatingDisplay();
    }

    private void startUpdatingDisplay() {

    }

    private void stopUpdatingDisplay() {
        
    }
}
