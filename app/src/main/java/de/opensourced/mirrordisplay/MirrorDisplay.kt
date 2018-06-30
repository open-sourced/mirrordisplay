package de.opensourced.mirrordisplay

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.os.ConfigurationCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.johnhiott.darkskyandroidlib.models.DataPoint
import de.opensourced.mirrordisplay.Objects.PreferencesManager
import de.opensourced.mirrordisplay.services.AgendaService
import kotlinx.android.synthetic.main.activity_mirror_display.*
import de.opensourced.mirrordisplay.services.ForecastService
import de.opensourced.mirrordisplay.services.TimeService
import de.opensourced.mirrordisplay.util.Constants.Companion.PERCENT
import de.opensourced.mirrordisplay.util.Constants.Companion.TEMPERATURE_METRIC
import de.opensourced.mirrordisplay.util.Constants.Companion.VELOCITY
import de.opensourced.mirrordisplay.util.WeatherIconGenerator
import java.util.*
import kotlin.math.roundToInt
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import de.opensourced.mirrordisplay.adapter.CalendarItemAdapter
import de.opensourced.mirrordisplay.adapter.RssfeedItemAdapter
import de.opensourced.mirrordisplay.models.CalendarEvent
import de.opensourced.mirrordisplay.models.RssFeedData
import de.opensourced.mirrordisplay.services.RssService
import kotlin.collections.ArrayList

class MirrorDisplay : AppCompatActivity() {

    private lateinit var forecastService: ForecastService
    private lateinit var timeService: TimeService
    private lateinit var agendaService: AgendaService
    private lateinit var rssService: RssService
    private lateinit var currentLocale: Locale
    private val calenderEvents: ArrayList<CalendarEvent> = ArrayList()
    private val rssFeedData: ArrayList<RssFeedData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.decorView.systemUiVisibility = 0x10
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // Set view
        setContentView(R.layout.activity_mirror_display)
        currentLocale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val preferencesManager = PreferencesManager(this)
        //Setup calendar view
        viewCalendar.layoutManager = LinearLayoutManager(this)
        val calendarItemAdapter = CalendarItemAdapter(this, calenderEvents)
        viewCalendar.adapter = calendarItemAdapter
        //Setup rss view
        viewRssfeed.layoutManager = LinearLayoutManager(this)
        val rssFeedItemAdapter = RssfeedItemAdapter(this, rssFeedData)
        viewRssfeed.adapter = rssFeedItemAdapter
        // Timeservice
        timeService = TimeService(
                this,
                Runnable {
                    runOnUiThread({
                        displayTime(timeService.timeString, timeService.dateString)
                    })
                },
                Runnable {
                    Log.e("TimeService", "Error: " + timeService.lastException)
                }
        )
        timeService.startService()
        // Weatherservice
        val weatherIconGenerator = WeatherIconGenerator()
        forecastService = ForecastService(
                preferencesManager.preferences.weatherLatitude,
                preferencesManager.preferences.weatherLongitude,
                preferencesManager.preferences.darkSkyApiKey,
                Runnable { runOnUiThread({ displayWeather(weatherIconGenerator) }) },
                Runnable { Log.e("ForecastService", "Error: " + forecastService.lastException) }
        )
        forecastService.startService()
        // Agendaservice
        agendaService = AgendaService(
                this,
                Runnable { runOnUiThread({ displayCalendar() }) },
                Runnable { Log.e("AgendaService", "Error: " + agendaService.lastException) }
        )
        agendaService.startService()
        // Rssservice
        rssService = RssService(
                this,
                preferencesManager.preferences.rssUrl,
                preferencesManager.preferences.rssNumber,
                Runnable { runOnUiThread({ displayRssFeed() }) },
                Runnable { Log.e("RssService", "Error: " + rssService.lastException) }
        )
        rssService.startService()
    }

    private fun displayWeather(weatherIconGenerator: WeatherIconGenerator) {
        forecastService.lastWeatherResponse?.let {
            val currently = it.currently
            imageWeatherCurrent.setImageResource(weatherIconGenerator.getIcon(currently.icon))
            containerWeatherCurrent.visibility = View.VISIBLE
            txtTempCurrent.text = String.format("%d $TEMPERATURE_METRIC (%d $TEMPERATURE_METRIC)",
                    currently.temperature.roundToInt(),
                    currently.apparentTemperature.roundToInt()
            )
            txtPrecipitationCurrent.text = String.format(
                    "%d %$PERCENT",
                    (currently.precipProbability.toDouble() * 100).toInt()
            )
            txtHumidityCurrent.text = String.format(
                    "%d %$PERCENT",
                    (currently.humidity.toDouble() * 100).toInt()
            )
            txtWindCurrent.text = String.format("%.2f $VELOCITY", currently.windSpeed.toDouble() * 3600 / 1000)
            val precipType = currently.precipType
            if(precipType != null && (precipType.equals("snow") || precipType.equals("sleet"))) {
                imageWeatherSnow.visibility = View.VISIBLE
            }else{
                imageWeatherSnow.visibility = View.INVISIBLE
            }

            renderWeather(
                    weatherIconGenerator,
                    containerWeatherDay1,
                    it.daily.data[1],
                    txtTitleDay1,
                    txtTempDay1,
                    txtPrecipitationDay1,
                    txtHumidityDay1,
                    txtWindDay1,
                    imageWeatherDay1
            )
            renderWeather(
                    weatherIconGenerator,
                    containerWeatherDay2,
                    it.daily.data[2],
                    txtTitleDay2,
                    txtTempDay2,
                    txtPrecipitationDay2,
                    txtHumidityDay2,
                    txtWindDay2,
                    imageWeatherDay2
            )
            renderWeather(
                    weatherIconGenerator,
                    containerWeatherDay3,
                    it.daily.data[3],
                    txtTitleDay3,
                    txtTempDay3,
                    txtPrecipitationDay3,
                    txtHumidityDay3,
                    txtWindDay3,
                    imageWeatherDay3
            )
        }
    }

    private fun renderWeather(weatherIconGenerator: WeatherIconGenerator, container: LinearLayout,
                              weatherData: DataPoint?, txtWeekday: TextView, txtTemperature: TextView,
                              txtPrecipitation: TextView, txtHumidity: TextView, txtWind: TextView,
                              imgWeatherIcon: ImageView) {
        weatherData?.let {
            container.visibility = View.VISIBLE
            val weekdayDayAfterTomorrow = String.format(currentLocale, "%tA", it.time * 1000L)
            txtWeekday.text = weekdayDayAfterTomorrow
            txtTemperature.text = String.format("%d $TEMPERATURE_METRIC / %d $TEMPERATURE_METRIC", it.temperatureMin.roundToInt(), it.temperatureMax.roundToInt())
            txtPrecipitation.text = String.format("%d %$PERCENT", (it.precipProbability.toDouble() * 100).toInt())
            txtHumidity.text = String.format("%d %$PERCENT", (it.humidity.toDouble() * 100).toInt())
            txtWind.text = String.format("%.2f $VELOCITY", it.windSpeed.toDouble() * 3600 / 1000)
            imgWeatherIcon.setImageResource(weatherIconGenerator.getIcon(it.icon))
        }
    }

    private fun displayTime(timeStr: String, dateStr: String) {
        txtTime.text = timeStr
        txtDate.text = dateStr
    }

    private fun displayCalendar() {
        calenderEvents.clear()
        calenderEvents.addAll(agendaService.events)
        if (calenderEvents.isEmpty()) {
            calenderEvents.add(CalendarEvent("keine Termine vorhanden.", "", 0, 0, ""))
        }
        viewCalendar.adapter.notifyDataSetChanged()
    }

    private fun displayRssFeed() {
        rssFeedData.clear()
        rssFeedData.addAll(rssService.feedData)
        viewRssfeed.adapter.notifyDataSetChanged()
    }

}
