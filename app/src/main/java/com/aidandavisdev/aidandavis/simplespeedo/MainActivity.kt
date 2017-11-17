package com.aidandavisdev.aidandavis.simplespeedo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var speedText: TextView
    private lateinit var speedTracker: SpeedTracker

    private lateinit var blackButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var speedFormatText: TextView
    private lateinit var mainLayout: RelativeLayout

    private var isBlack = false

    private var updater: Handler? = null
    private val updateTask = object : Runnable {
        override fun run() {
            updateSpeedDisplay()
            updater!!.postDelayed(this, 333) // time between updates
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // get permission if not there
        requestPermission()

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.toolbar_title)

        speedText = findViewById(R.id.speed_display) as TextView
        speedTracker = SpeedTracker(baseContext)

        updater = Handler()

        blackButton = findViewById(R.id.blackButton) as Button
        blackButton.setOnClickListener({ blackButtonPressed() })

        speedFormatText = findViewById(R.id.speed_format) as TextView
        mainLayout = findViewById(R.id.main_layout) as RelativeLayout
    }

    private fun blackButtonPressed() {
        var textColour = ContextCompat.getColor(this, R.color.black)
        var backgroundColour = ContextCompat.getColor(this, R.color.white)

        if (!isBlack) {
            textColour = ContextCompat.getColor(this, R.color.white)
            backgroundColour = ContextCompat.getColor(this, R.color.black)

            isBlack = true
        } else {
            isBlack = false
        }

        // background
        mainLayout.setBackgroundColor(backgroundColour)
        // text
        speedText.setTextColor(textColour)
        speedFormatText.setTextColor(textColour)
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            var fineRequestCode = 1
            var finePermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, finePermission, fineRequestCode )

            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        speedTracker.startTracking()
        startUpdatingDisplay()
    }

    override fun onPause() {
        super.onPause()

        stopUpdatingDisplay()
        speedTracker.stopTracking()
    }

    private fun startUpdatingDisplay() {
        updater!!.post(updateTask)
    }

    private fun stopUpdatingDisplay() {
        updater!!.removeCallbacks(updateTask)
    }

    private fun updateSpeedDisplay() {
        speedText.text = "%.2f".format(speedTracker.speedKMH)
    }
}
