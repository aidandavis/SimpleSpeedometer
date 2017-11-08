package com.aidandavisdev.aidandavis.simplespeedo

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView

import com.domain.aidandavis.simplespeedo.R

class MainActivity : AppCompatActivity() {
    private var speedText: TextView? = null
    private var speedTracker: SpeedTracker? = null

    private var updater: Handler? = null
    private val updateTask = object : Runnable {
        override fun run() {
            updateSpeedDisplay()
            updater!!.postDelayed(this, 333) // time between updates
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar

        toolbar.setTitle(R.string.toolbar_title)

        speedText = findViewById(R.id.speedDisplay) as TextView
        speedTracker = SpeedTracker(baseContext)

        updater = Handler()
    }

    override fun onResume() {
        super.onResume()

        speedTracker!!.startTracking()
        startUpdatingDisplay()
    }

    override fun onPause() {
        super.onPause()

        stopUpdatingDisplay()
        speedTracker!!.stopTracking()
    }

    private fun startUpdatingDisplay() {
        updater!!.post(updateTask)
    }

    private fun stopUpdatingDisplay() {
        updater!!.removeCallbacks(updateTask)
    }

    private fun updateSpeedDisplay() {
        val speed = speedTracker!!.speedKMH
        speedText!!.text = "$speed km/h"
    }
}
