package de.opensourced.mirrordisplay

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.os.ConfigurationCompat
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.opensourced.mirrordisplay.Objects.PreferencesManager
import de.opensourced.mirrordisplay.services.AgendaService
import kotlinx.android.synthetic.main.activity_mirror_display.*
import de.opensourced.mirrordisplay.services.ForecastService
import de.opensourced.mirrordisplay.services.TimeService
import de.opensourced.mirrordisplay.util.Constants.Companion.PERCENT
import de.opensourced.mirrordisplay.util.Constants.Companion.TEMPERATURE_METRIC
import de.opensourced.mirrordisplay.util.Constants.Companion.VELOCITY
import de.opensourced.mirrordisplay.util.WeatherIconHelper
import java.util.*
import kotlin.math.roundToInt
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Window
import android.view.WindowManager
import de.opensourced.mirrordisplay.adapter.CalendarItemAdapter
import de.opensourced.mirrordisplay.adapter.RssFeedItemAdapter
import de.opensourced.mirrordisplay.models.CalendarEvent
import de.opensourced.mirrordisplay.models.OpenWeatherData
import de.opensourced.mirrordisplay.models.RssFeedData
import de.opensourced.mirrordisplay.services.RssService
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashSet

class MirrorDisplay : AppCompatActivity() {

    private lateinit var forecastService: ForecastService
    private lateinit var timeService: TimeService
    private lateinit var agendaService: AgendaService
    private lateinit var rssService: RssService
    private lateinit var currentLocale: Locale
    private val calenderEvents: HashSet<CalendarEvent> = LinkedHashSet()
    private val rssFeedData: HashSet<RssFeedData> = LinkedHashSet()

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
        val rssFeedItemAdapter = RssFeedItemAdapter(this, rssFeedData)
        viewRssfeed.adapter = rssFeedItemAdapter
        // Services
        timeService = TimeService(
                this,
                Runnable {
                    runOnUiThread {
                        displayTime(timeService.timeString, timeService.dateString)
                    }
                },
                Runnable {
                    Log.e("TimeService", "Error: " + timeService.lastException)
                }
                                 )
        timeService.startService()
        val weatherIconGenerator = WeatherIconHelper()
        forecastService = ForecastService(
                preferencesManager.preferences.weatherLatitude,
                preferencesManager.preferences.weatherLongitude,
                preferencesManager.preferences.weatherApiKey,
                Runnable { runOnUiThread { displayWeather(weatherIconGenerator) } },
                Runnable { Log.e("ForecastService", "Error: " + forecastService.lastException) }
                                         )
        forecastService.startService()
        agendaService = AgendaService(
                this,
                Runnable { runOnUiThread { displayCalendar() } },
                Runnable { Log.e("AgendaService", "Error: " + agendaService.lastException) }
                                     )
        agendaService.startService()
        rssService = RssService(
                this,
                preferencesManager.preferences.rssUrl,
                preferencesManager.preferences.rssNumber,
                Runnable { runOnUiThread { displayRssFeed() } },
                Runnable { Log.e("RssService", "Error: " + rssService.lastException) }
                               )
        rssService.startService()
    }

    private fun displayWeather(weatherIconHelper: WeatherIconHelper) {
        val latestWeatherForecast = forecastService.latestWeatherForecast
        latestWeatherForecast?.today?.let {
            weatherIconHelper.loadIcon(it.iconId, imageWeatherCurrent)

            containerWeatherCurrent.visibility = View.VISIBLE
            txtTempCurrent.text = String.format("%d $TEMPERATURE_METRIC / %d $TEMPERATURE_METRIC (%d $TEMPERATURE_METRIC)",
                                                it.temperatureMin.roundToInt(),
                                                it.temperatureMax.roundToInt(),
                                                it.temperatureFeel.roundToInt()
                                               )
            txtPrecipitationCurrent.text = String.format(
                    "%d %$PERCENT",
                    it.precipitation.toInt()
                                                        )
            txtHumidityCurrent.text = String.format(
                    "%d %$PERCENT",
                    it.humidity.toInt()
                                                   )
            txtWindCurrent.text = String.format("%.2f $VELOCITY", it.windSpeed * 3600 / 1000)
            txtUVICurrent.text = String.format("%.2f UVI", it.uvi)
        }
        latestWeatherForecast?.tomorrow?.let {
            renderWeather(weatherIconHelper,
                          containerWeatherDay1,
                          it,
                          txtTitleDay1,
                          txtTempDay1,
                          txtPrecipitationDay1,
                          txtHumidityDay1,
                          txtWindDay1,
                          txtUVIDay1,
                          imageWeatherDay1)
        }
        latestWeatherForecast?.dayAfterTomorrow?.let {
            renderWeather(weatherIconHelper,
                          containerWeatherDay2,
                          it,
                          txtTitleDay2,
                          txtTempDay2,
                          txtPrecipitationDay2,
                          txtHumidityDay2,
                          txtWindDay2,
                          txtUVIDay2,
                          imageWeatherDay2)
        }
        latestWeatherForecast?.dayAfterTheDayTomorrow?.let {
            renderWeather(weatherIconHelper,
                          containerWeatherDay3,
                          it,
                          txtTitleDay3,
                          txtTempDay3,
                          txtPrecipitationDay3,
                          txtHumidityDay3,
                          txtWindDay3,
                          txtUVIDay3,
                          imageWeatherDay3)
        }
    }

    private fun renderWeather(weatherIconHelper: WeatherIconHelper, container: LinearLayout,
                              weatherData: OpenWeatherData?, txtWeekday: TextView, txtTemperature: TextView,
                              txtPrecipitation: TextView, txtHumidity: TextView, txtWind: TextView, txtUvi: TextView,
                              imgWeatherIcon: ImageView) {
        weatherData?.let {
            container.visibility = View.VISIBLE
            val weekdayDayAfterTomorrow = String.format(currentLocale, "%tA", it.time * 1000L)
            txtWeekday.text = weekdayDayAfterTomorrow
            txtTemperature.text = String.format("%d $TEMPERATURE_METRIC / %d $TEMPERATURE_METRIC (%d $TEMPERATURE_METRIC)",
                                                it.temperatureMin.roundToInt(),
                                                it.temperatureMax.roundToInt(),
                                                it.temperatureFeel.roundToInt())
            txtPrecipitation.text = String.format("%d %$PERCENT", it.precipitation.toInt())
            txtHumidity.text = String.format("%d %$PERCENT", it.humidity.toInt())
            txtWind.text = String.format("%.2f $VELOCITY", it.windSpeed * 3600 / 1000)
            txtUvi.text = String.format("%.2f UVI", it.uvi)
            weatherIconHelper.loadIcon(it.iconId, imgWeatherIcon)
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
            calenderEvents.add(CalendarEvent(getString(R.string.EMPTY_AGENDA), "", 0, 0, ""))
        }
        viewCalendar.adapter?.notifyDataSetChanged()
    }

    private fun displayRssFeed() {
        rssFeedData.clear()
        rssFeedData.addAll(rssService.feedData)
        viewRssfeed.adapter?.notifyDataSetChanged()
    }

}
