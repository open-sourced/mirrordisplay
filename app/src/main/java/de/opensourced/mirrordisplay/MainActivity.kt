package de.opensourced.mirrordisplay

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.widget.EditText
import de.opensourced.mirrordisplay.Objects.PreferencesManager
import java.util.prefs.Preferences
import android.widget.Toast
import android.R.attr.data
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern
import android.R.attr.data




class MainActivity : AppCompatActivity() {

    lateinit var preferencesManager: PreferencesManager

    fun startMirrorDisplay(v: View) {
        if (validate()) {
            preferencesManager.preferences.darkSkyApiKey = inputDarkSkyApi.text.toString()
            preferencesManager.preferences.weatherLatitude = inputWeatherLatitude.text.toString()
            preferencesManager.preferences.weatherLongitude = inputWeatherLongitude.text.toString()
            preferencesManager.save()
            val intent = Intent(this, MirrorDisplay::class.java)
            startActivity(intent)
        }
    }

    private fun validate(): Boolean {
        val regexLongLat = Pattern.compile("^\\-?\\d+(\\.\\d+)*").toRegex()
        if(!inputWeatherLatitude.text.matches(regexLongLat)) {
            Toast.makeText(this, "Latitude invalid, e.g. 50.6666 required!",
                    Toast.LENGTH_LONG).show()
            return false
        }
        if(!inputWeatherLongitude.text.matches(regexLongLat)) {
            Toast.makeText(this, "Longitude invalid, e.g. 50.6666 required!",
                    Toast.LENGTH_LONG).show()
            return false
        }
        if(inputDarkSkyApi.text.isEmpty()) {
            Toast.makeText(this, "DarkSky API key missing!",
                    Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferencesManager = PreferencesManager(this)
        inputDarkSkyApi.setText(preferencesManager.preferences.darkSkyApiKey)
        inputWeatherLatitude.setText(preferencesManager.preferences.weatherLatitude)
        inputWeatherLongitude.setText(preferencesManager.preferences.weatherLongitude)
    }
}
