package de.opensourced.mirrordisplay.services

import de.opensourced.mirrordisplay.exceptions.WeatherRequestFailedException
import de.opensourced.mirrordisplay.models.OpenWeatherForecast
import de.opensourced.mirrordisplay.util.OpenWeatherLoader


class ForecastService(private val lat: String, private val long: String, private val openWeatherApiKey: String,
                      callbackOnUpdate: Runnable, callbackOnError: Runnable) : BaseService(callbackOnUpdate,
                                                                                           callbackOnError) {

    var latestWeatherForecast: OpenWeatherForecast? = null
    var lastError: WeatherRequestFailedException? = null

    override fun getIntervalMilliseconds(): Long {
        return 30 * 60 * 1000
    }

    override fun work() {
        val openWeatherLoader = OpenWeatherLoader(openWeatherApiKey,
                                                  lat,
                                                  long) {
            latestWeatherForecast = it
        }
        try {
            openWeatherLoader.requestWeather()
        } catch (e: WeatherRequestFailedException) {
            lastError = e
        }
    }
}