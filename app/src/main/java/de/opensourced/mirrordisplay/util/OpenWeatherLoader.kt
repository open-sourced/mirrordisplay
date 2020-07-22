package de.opensourced.mirrordisplay.util

import android.util.Log
import de.opensourced.mirrordisplay.exceptions.WeatherRequestFailedException
import de.opensourced.mirrordisplay.models.OpenWeatherData
import de.opensourced.mirrordisplay.models.OpenWeatherForecast
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL


class OpenWeatherLoader(private val apiKey: String, private val lat: String, private val long: String,
                        private val resultHandler: (OpenWeatherForecast) -> Unit) {

    @Throws(WeatherRequestFailedException::class)
    fun requestWeather() {
        val url = URL("http://api.openweathermap.org/data/2.5/onecall?lat=$lat&lon=$long&\n" +
                              "exclude=hourly,minutely&appid=$apiKey&units=metric ")
        try {
            val request = Request.Builder()
                    .url(url)
                    .build();
            val response = OkHttpClient().newCall(request).execute()
            val body = response.body()
            if (body != null) {
                val json = body.string()
                resultHandler(parseJSONData(JSONObject(json)))
            } else {
                handleError(null, "No json data received from request to $url")
            }
        } catch (e: Exception) {
            handleError(e, "JSON request for weather to $url resulted in error.")
        }
    }

    private fun handleError(exception: Throwable?, message: String) {
        Log.e("Error", message, exception)
        throw WeatherRequestFailedException(message, exception)
    }

    private fun parseJSONData(jsonData: JSONObject): OpenWeatherForecast {
        val dailyForecasts = jsonData.getJSONArray("daily")
        return OpenWeatherForecast(
                parseDaily(dailyForecasts.get(0) as JSONObject),
                parseDaily(dailyForecasts.get(1) as JSONObject),
                parseDaily(dailyForecasts.get(2) as JSONObject),
                parseDaily(dailyForecasts.get(3) as JSONObject));
    }

    private fun parseDaily(dailyJson: JSONObject): OpenWeatherData {
        val precipitation = dailyJson.getDouble("pop")
        val sunrise = dailyJson.getString("sunrise")
        val sunset = dailyJson.getString("sunset")
        val humidity = dailyJson.getDouble("humidity")
        val windSpeed = dailyJson.getDouble("wind_speed")
        val pressure = dailyJson.getInt("pressure")
        val weather = dailyJson.getJSONArray("weather").get(0) as JSONObject
        val description = weather.getString("description")
        val icon = weather.getString("icon")
        val temperatures = dailyJson.getJSONObject("temp")
        val temperaturesFeel = dailyJson.getJSONObject("feels_like")
        val tempMin = temperatures.getDouble("min")
        val tempMax = temperatures.getDouble("max")
        val tempFeel = temperaturesFeel.getDouble("day")
        val uvi = dailyJson.getDouble("uvi")
        val time = dailyJson.getLong("dt")
        return OpenWeatherData(tempMin,
                               tempMax,
                               tempFeel,
                               precipitation,
                               humidity,
                               pressure,
                               sunrise,
                               sunset,
                               windSpeed,
                               description,
                               uvi,
                               icon,
                               time)
    }

}
