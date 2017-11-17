package com.aidandavisdev.aidandavis.simplespeedo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.WindowManager
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var speedText: TextView
    private lateinit var blackButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var speedFormatText: TextView
    private lateinit var mainLayout: RelativeLayout

    private var isBlack = false
    enum class SpeedFormat {
        KMH, MPH, MPS
    }
    private var speedFormat = SpeedFormat.KMH

    enum class GpsStatus {
        DISABLED, WAITING, FIXED
    }

    private  lateinit var speedTracker: SpeedTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main_layout) as RelativeLayout
        toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitle(R.string.toolbar_title)

        speedText = findViewById(R.id.speed_display) as TextView
        speedText.setOnClickListener({ cycleSpeedFormat() })

        blackButton = findViewById(R.id.blackButton) as Button
        blackButton.setOnClickListener({ blackButtonPressed() })

        speedFormatText = findViewById(R.id.speed_format) as TextView
        speedFormatText.setOnClickListener({ cycleSpeedFormat() })

        speedTracker = object : SpeedTracker(this, (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {
            override fun onGPSDisabled() {
                setStatusDisplay(GpsStatus.DISABLED)
            }

            override fun onGPSWaiting() {
                setStatusDisplay(GpsStatus.WAITING)
            }

            override fun onGPSFix() {
                setStatusDisplay(GpsStatus.FIXED)
            }

            override fun onSpeedChanged() {
                updateSpeedDisplay()
            }
        }
    }

    private fun setStatusDisplay(gpsStatus: GpsStatus) = when (gpsStatus) {
        GpsStatus.FIXED -> toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
        GpsStatus.WAITING -> toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.gps_waiting))
        GpsStatus.DISABLED -> toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.gps_disabled))
    }

    private fun cycleSpeedFormat() {
        speedFormat = when (speedFormat) {
            SpeedFormat.KMH -> SpeedFormat.MPH
            SpeedFormat.MPH -> SpeedFormat.MPS
            SpeedFormat.MPS -> SpeedFormat.KMH
        }
        setSpeedFormatText()
    }

    private fun setSpeedFormatText() {
        when (speedFormat) {
            SpeedFormat.KMH -> speedFormatText.text = getString(R.string.kmh)
            SpeedFormat.MPH -> speedFormatText.text = getString(R.string.mph)
            SpeedFormat.MPS -> speedFormatText.text = getString(R.string.mps)
        }
    }

    private fun blackButtonPressed() {
        var textColour = ContextCompat.getColor(this, R.color.black)
        var backgroundColour = ContextCompat.getColor(this, R.color.white)

        if (!isBlack) {
            textColour = ContextCompat.getColor(this, R.color.white)
            backgroundColour = ContextCompat.getColor(this, R.color.black)
            blackButton.setText(R.string.whiiite)
            isBlack = true
        } else {
            blackButton.setText(R.string.blaaack)
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
            val fineRequestCode = 1
            val finePermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            ActivityCompat.requestPermissions(this, finePermission, fineRequestCode)

            // Saves having to deal with callbacks for if/if not user said yes...
            // (Normally, you are supposed to get the callback and then not call methods in the app
            // that require the permission. But, this entire app needs the permission so I'm just
            // not going to run the app unless the permission is there).
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        requestPermission()
        speedTracker.startTracking()
        setStatusDisplay(GpsStatus.WAITING)
    }

    override fun onPause() {
        super.onPause()
        speedTracker.stopTracking()
    }

    private fun updateSpeedDisplay() {
        val speed = when (speedFormat) {
            SpeedFormat.KMH -> speedTracker.speedKMH
            SpeedFormat.MPH -> speedTracker.speedMPH
            SpeedFormat.MPS -> speedTracker.speedMPS
        }
        speedText.text = getString(R.string.speed_text_format).format(speed)
    }
}
