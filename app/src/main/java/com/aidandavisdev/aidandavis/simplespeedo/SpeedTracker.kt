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

    private var locationBuffer: ArrayList<Location> = ArrayList()
    private val BUFFER_SIZE = 5

    override fun onLocationChanged(location: Location) {
        locationBuffer.add(location)
        if (isTracking) {
            // various? buffer algorithms here...
            if (locationBuffer.size > BUFFER_SIZE) {
                val buffer = locationBuffer.subList(locationBuffer.lastIndex - BUFFER_SIZE, locationBuffer.lastIndex) // FOR NOW, last 10 points

                speed = if (location.hasSpeed()) {
                    location.speed.toDouble()
                } else {
                    calculateSpeedManually(buffer)
                }
            }
        }
    }

    private fun calculateSpeedManually(buffer: List<Location>): Double {
        val distance = getAverageDistanceFromBuffer(buffer) // metres
        val time = (buffer.last().time - buffer.first().time) / 1000 // seconds

        return (distance / time.toDouble()) // thanks grade-8 physics
    }

    private fun getAverageDistanceFromBuffer(buffer: List<Location>): Double {
        val totalDistance = buffer.indices
                .filter {
                    it != 0 // skipping first
                }
                .map { buffer[it - 1].distanceTo(buffer[it]) }
                .sum()
        return (totalDistance / buffer.size).toDouble()
    }


    /* functions below are unused but required */
    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {
    }

    override fun onProviderEnabled(s: String) {
    }

    override fun onProviderDisabled(s: String) {
    }
}
