package com.aidandavisdev.aidandavis.simplespeedo

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

/**
 * Created by Aidan Davis on 5/11/2017.
 */

abstract class SpeedTracker(context: Context, private val isTrackingStatus: Boolean) : LocationListener {
    private val mLocationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var isTracking = false

    var speedMPS = 0.0 // m/s
    val speedKMH: Double
        get() = speedMPS * 3.6
    val speedMPH: Double
        get() = speedMPS * 2.2

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (!isTracking) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
            if (isTrackingStatus) registerGnssCallback()
            isTracking = true
        }
    }

    /* Deciding to have 0.0 as default speed. Might not be a good idea but can change later down the track.
     * Ideally the view would say 'not tracking' rather than display 0.0 Km/H
     */
    fun stopTracking() {
        if (isTracking) {
            mLocationManager.removeUpdates(this)
            if (isTrackingStatus) unregisterGnssCallback()
            isTracking = false
            speedMPS = 0.0
            locationBuffer.clear()
        }
    }

    private lateinit var mGnssStatusCallback: GnssStatus.Callback

    @SuppressLint("MissingPermission", "NewApi")
    private fun registerGnssCallback() {
        mGnssStatusCallback = object : GnssStatus.Callback() {
            override fun onFirstFix(ttffMillis: Int) {
                super.onFirstFix(ttffMillis)
                onGPSFix()
            }
            override fun onStarted() {
                super.onStarted()
                onGPSWaiting()
            }
        }
        mLocationManager.registerGnssStatusCallback(mGnssStatusCallback)
    }

    @SuppressLint("NewApi")
    private fun unregisterGnssCallback() {
        mLocationManager.unregisterGnssStatusCallback(mGnssStatusCallback)
    }

    private var locationBuffer: ArrayList<Location> = ArrayList()
    private val BUFFER_SIZE = 5

    override fun onLocationChanged(location: Location) {
        locationBuffer.add(location)
        if (isTracking) {
            // various? buffer algorithms here...
            if (locationBuffer.size > BUFFER_SIZE) {
                val buffer = locationBuffer.subList(locationBuffer.lastIndex - BUFFER_SIZE, locationBuffer.lastIndex)
                speedMPS = calculateSpeedManually(buffer)
                onSpeedChanged()
            }
        }
    }

    private fun calculateSpeedManually(buffer: List<Location>): Double {
        val distance = getAverageDistanceFromBuffer(buffer) // metres
        val time = (buffer.last().time - buffer.first().time) / 1000 // seconds

        return if ((System.currentTimeMillis() - buffer.last().time) > 10000) {
            // speed goes to 0 if no points received in last 10 seconds
            onGPSWaiting()
            0.0
        } else {
            (distance / time.toDouble()) // thanks grade-8 physics
        }
    }

    private fun getAverageDistanceFromBuffer(buffer: List<Location>): Double {
        val totalDistance = buffer.indices
                .filter { it != 0 } // skip first element
                .map { buffer[it - 1].distanceTo(buffer[it]) } // distance between this element and one prior
                .sum()
        return (totalDistance / buffer.size).toDouble()
    }

    // anonymous functions so calling class knows when speed is changed, or gps is fixed, etc
    abstract fun onSpeedChanged()
    abstract fun onGPSDisabled()
    abstract fun onGPSWaiting()
    abstract fun onGPSFix()

    override fun onProviderDisabled(s: String) {
        onGPSDisabled()
    }


    /* unused but required. functionality this should provide is done by the GNSS callback */
    override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {
    }

    override fun onProviderEnabled(s: String) {
    }
}
