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
    private var speed = 0.0 // m/s

    val speedKMH: Double
        get() = speed * 3.6

    private var locationBuffer: ArrayList<Location> = ArrayList()
    private val BUFFER_TIME = 2500 // 2 seconds of points in buffer

    fun startTracking() {
        if (!isTracking) {
            // permission check
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            locationBuffer.clear()
        }
    }

    override fun onLocationChanged(location: Location) {
        if (isTracking) {
            addLocationToBuffer(location)

            speed = if (locationBuffer.size > 2) {
                val distance = getAverageDistanceFromBuffer() // metres
                val time = (locationBuffer.last().time - locationBuffer.first().time)/1000 // seconds

                distance.toDouble() / time.toDouble() // thanks grade-8 physics
            } else {
                0.0
            }
        }
    }

    // add location to buffer and also delete locations older than set time
    private fun addLocationToBuffer(location: Location) {
        locationBuffer.add(location)
        locationBuffer
                .filter { (location.time - it.time) > BUFFER_TIME }
                .forEach { locationBuffer.remove(it) }
    }

    private fun getAverageDistanceFromBuffer(): Long {
        var totalDistance = 0F
        for (i in locationBuffer.indices) {
            if (i == 0) continue // skipping first

            val distanceBetweenTwo = floatArrayOf(0F, 0F, 0F)
            Location.distanceBetween(locationBuffer[i-1].latitude, locationBuffer[i-1].longitude,
                    locationBuffer[i].latitude, locationBuffer[i].longitude, distanceBetweenTwo)

            totalDistance += distanceBetweenTwo[0] // computed distance is stored in first index of array
        }
        return (totalDistance / locationBuffer.size).toLong()
    }


    /* functions below are unused */
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
