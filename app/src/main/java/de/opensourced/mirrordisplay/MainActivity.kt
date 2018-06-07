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


class MainActivity : AppCompatActivity() {

    lateinit var preferencesManager: PreferencesManager

    fun startMirrorDisplay(v : View) {
        preferencesManager.preferences.darkSkyApiKey = inputDarkSkyApi.text.toString()
        preferencesManager.save()
        val intent = Intent(this, MirrorDisplay::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferencesManager = PreferencesManager(this)
        inputDarkSkyApi.setText(preferencesManager.preferences.darkSkyApiKey)
    }
}
