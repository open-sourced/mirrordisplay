package de.opensourced.mirrordisplay.services

import com.johnhiott.darkskyandroidlib.ForecastApi
import com.johnhiott.darkskyandroidlib.RequestBuilder
import com.johnhiott.darkskyandroidlib.models.Request
import com.johnhiott.darkskyandroidlib.models.WeatherResponse
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response

class ForecastService(lat: String, long: String, darkSkyApiKey: String, callbackOnUpdate: Runnable,
                      callbackOnError: Runnable) : BaseService(callbackOnUpdate, callbackOnError) {

    private val weatherRequestBuilder: RequestBuilder
    private val weatherRequest: Request
    var lastWeatherResponse: WeatherResponse? = null
    var lastError: RetrofitError? = null

    init {
        ForecastApi.create(darkSkyApiKey)
        weatherRequestBuilder = RequestBuilder()
        weatherRequest = Request()
        weatherRequest.setLat(lat)
        weatherRequest.setLng(long)
        weatherRequest.setUnits(Request.Units.SI)
        weatherRequest.setLanguage(Request.Language.ENGLISH)
    }

    override fun getInterval(): Long {
        return 30 * 60 * 1000
    }

    override fun work() {
        weatherRequestBuilder.getWeather(weatherRequest, object : Callback<WeatherResponse> {
            override fun success(weatherResponse: WeatherResponse, response: Response) {
                lastWeatherResponse = weatherResponse
                callbackOnUpdate.run()
            }

            override fun failure(retrofitError: RetrofitError) {
                lastError = retrofitError
                callbackOnError.run()
            }
        })
    }
}