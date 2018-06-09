package de.opensourced.mirrordisplay

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import de.opensourced.mirrordisplay.Objects.PreferencesManager
import android.widget.Toast
import android.content.Intent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private lateinit var preferencesManager: PreferencesManager

    fun startMirrorDisplay(v: View) {
        if (validate()) {
            preferencesManager.preferences.darkSkyApiKey = inputDarkSkyApi.text.toString()
            preferencesManager.preferences.weatherLatitude = inputWeatherLatitude.text.toString()
            preferencesManager.preferences.weatherLongitude = inputWeatherLongitude.text.toString()
            preferencesManager.preferences.rssUrl = inputRssUrl.text.toString()
            preferencesManager.preferences.rssNumber = inputRssNumber.text.toString().toInt()
            preferencesManager.save()
            val intent = Intent(this, MirrorDisplay::class.java)
            startActivity(intent)
        }
    }

    private fun validate(): Boolean {
        val regexLongLat = Pattern.compile("^-?\\d+(\\.\\d+)*").toRegex()
        val regexUrl = Pattern.compile("^[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]").toRegex()
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
        if(!inputRssUrl.text.matches(regexUrl)) {
            Toast.makeText(this, "RSS url invalid!",
                    Toast.LENGTH_LONG).show()
            return false
        }
        if(inputRssNumber.text.toString().toIntOrNull() == null) {
            Toast.makeText(this, "RSS number of feeds is not a valid number!",
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
        inputRssUrl.setText(preferencesManager.preferences.rssUrl)
        inputRssNumber.setText(preferencesManager.preferences.rssNumber.toString())
    }
}
