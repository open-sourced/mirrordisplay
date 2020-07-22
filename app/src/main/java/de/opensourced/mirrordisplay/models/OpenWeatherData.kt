package de.opensourced.mirrordisplay.models

data class OpenWeatherData(val temperatureMin: Double,
                           val temperatureMax: Double,
                           val temperatureFeel: Double,
                           val precipitation: Double,
                           val humidity: Double,
                           val pressure: Int,
                           val sunrise: String,
                           val sunset: String,
                           val windSpeed: Double,
                           val description: String,
                           val uvi: Double,
                           val iconId: String,
                           val time : Long
)