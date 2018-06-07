package de.opensourced.mirrordisplay.Objects

import com.google.gson.Gson
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.annotations.SerializedName


class PreferencesManager(val c: Context) {

    private val KEY_CONTENT : String = "PREFERENCES_MIRROR_DISPLAY"
    private val KEY_APP_SETTINGS : String = "PREFERENCES_MIRROR_DISPLAY_APP_SETTINGS"

    val preferences : Preferences

    data class Preferences(
            @SerializedName("darkSkyApiKey") var darkSkyApiKey : String ="",
            @SerializedName("weatherLatitude") var weatherLatitude : String ="",
            @SerializedName("weatherLongitude") var weatherLongitude : String =""
    )

    init {
        preferences = load(c)
    }

    fun save() {
        val prefsEditor = c.getSharedPreferences(KEY_APP_SETTINGS, MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(preferences)
        prefsEditor.putString(KEY_CONTENT, json)
        prefsEditor.apply()
    }

    private fun load(c : Context) : Preferences  {
        val gson = Gson()
        val json = c.getSharedPreferences(KEY_APP_SETTINGS, MODE_PRIVATE).getString(KEY_CONTENT, "")
        return gson.fromJson(json, Preferences::class.java) ?: Preferences()
    }
}