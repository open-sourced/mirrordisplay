package de.opensourced.mirrordisplay.util

import android.support.annotation.DrawableRes
import de.opensourced.mirrordisplay.R
import java.util.HashMap


/**
 * Ported from Java to Kotlin
 *
 * Original source from Speculum App by
 *
 * @author Niels Masdorp (NielsMasdorp)
 */
class WeatherIconGenerator {

    private lateinit var iconMap: HashMap<String, Int>

    init {
        initMap()
    }

    /**
     * Initialize the weather icon map.
     * Every icon has a String identifier mapped to a resource id.
     */
    private fun initMap() {
        iconMap = HashMap()
        iconMap["tornado"] = R.drawable.ic_weather_tornado
        iconMap["thunderstorm"] = R.drawable.ic_weather_lightning
        iconMap["wind"] = R.drawable.ic_weather_windy
        iconMap["rain"] = R.drawable.ic_weather_pouring
        iconMap["hail"] = R.drawable.ic_weather_hail
        iconMap["snow"] = R.drawable.ic_weather_snowy
        iconMap["sleet"] = R.drawable.ic_weather_sleet
        iconMap["fog"] = R.drawable.ic_weather_fog
        iconMap["partly-cloudy-night"] = R.drawable.ic_weather_cloudy_night
        iconMap["clear-night"] = R.drawable.ic_weather_night
        iconMap["clear-day"] = R.drawable.ic_weather_sunny
        iconMap["partly-cloudy-day"] = R.drawable.ic_weather_partlycloudy
        iconMap["cloudy"] = R.drawable.ic_weather_cloudy
    }

    /**
     * Get an icon for a weather type.
     *
     * @param icon weather type (e.g "tornado")
     * @return icon resource
     */
    @DrawableRes
    fun getIcon(icon: String): Int {
        return iconMap[icon] ?: R.drawable.ic_weather_sunny

    }
}
