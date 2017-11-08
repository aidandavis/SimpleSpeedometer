package com.aidandavisdev.aidandavis.simplespeedo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.widget.Toast

/**
 * Created by Aidan Davis on 5/11/2017.
 */

class SpeedTracker(private val mContext: Context) : LocationListener {
    private val mLocationManager: LocationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var isTracking = false
    private var speed = 0.0

    val speedKMH: Double
        get() = speed * 3.6

    fun startTracking() {
        if (!isTracking) {
            // permission check
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Location Permission Not Given :(", Toast.LENGTH_SHORT).show()
                return
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            isTracking = true
        }
    }

    /* Deciding to have 0.0 as default speed. Might not be a good idea but can change later down the track.
     * Ideally the view would say 'not tracking' rather than display 0.0 Km/H
     */
    fun stopTracking() {
        if (isTracking) {
            mLocationManager.removeUpdates(this)
            isTracking = false
            speed = 0.0
        }
    }



    override fun onLocationChanged(location: Location) {
        if (isTracking) {
            //speed = location.getSpeed();


        }
    }

    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {
        // do nothing for now?
    }
    override fun onProviderEnabled(s: String) {
        // don't need to do anything?
    }
    override fun onProviderDisabled(s: String) {
        // don't need to do anything here either?
    }
}
