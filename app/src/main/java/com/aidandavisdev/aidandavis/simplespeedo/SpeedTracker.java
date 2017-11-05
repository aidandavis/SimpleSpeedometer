package com.aidandavisdev.aidandavis.simplespeedo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Aidan Davis on 5/11/2017.
 */

public class SpeedTracker implements LocationListener {
    private LocationManager mLocationManager;
    private Context mContext;

    private boolean isTracking = false;
    private double speed = 0.0;

    public SpeedTracker(Context context) {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
    }

    public void startTracking() {
        if (!isTracking) {
            // permission check
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Location Permission Not Given :(", Toast.LENGTH_SHORT);
                return;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            isTracking = true;
        }
    }

    /* Deciding to have 0.0 as default speed. Might not be a good idea but can change later down the track.
     * Ideally the view would say 'not tracking' rather than display 0.0 Km/H
     */
    public void stopTracking() {
        if (isTracking) {
            mLocationManager.removeUpdates(this);
            isTracking = false;
            speed = 0.0;
        }
    }

    public double getSpeedKMH() {
        return speed * 3.6;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (isTracking) {
            speed = location.getSpeed();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // do nothing for now?
    }

    @Override
    public void onProviderEnabled(String s) {
        // don't need to do anything?
    }

    @Override
    public void onProviderDisabled(String s) {
        // don't need to do anything here either?
    }
}
