package de.opensourced.mirrordisplay.Objects

import com.google.gson.Gson
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.annotations.SerializedName


class PreferencesManager(val c: Context) {

    private val keyContent: String = "PREFERENCES_MIRROR_DISPLAY"
    private val keyAppSettings: String = "PREFERENCES_MIRROR_DISPLAY_APP_SETTINGS"

    val preferences: Preferences

    data class Preferences(
            @SerializedName("darkSkyApiKey") var darkSkyApiKey: String = "",
            @SerializedName("weatherLatitude") var weatherLatitude: String = "",
            @SerializedName("weatherLongitude") var weatherLongitude: String = "",
            @SerializedName("rssUrl") var rssUrl: String = "",
            @SerializedName("rssNumber") var rssNumber: Int = 8
    )

    init {
        preferences = load(c)
    }

    fun save() {
        val prefsEditor = c.getSharedPreferences(keyAppSettings, MODE_PRIVATE).edit()
        val gson = Gson()
        val json = gson.toJson(preferences)
        prefsEditor.putString(keyContent, json)
        prefsEditor.apply()
    }

    private fun load(c: Context): Preferences {
        val gson = Gson()
        val json = c.getSharedPreferences(keyAppSettings, MODE_PRIVATE).getString(keyContent, "")
        return gson.fromJson(json, Preferences::class.java) ?: Preferences()
    }
}