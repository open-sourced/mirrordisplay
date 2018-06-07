package de.opensourced.mirrordisplay.services

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import java.text.SimpleDateFormat
import java.util.*

class TimeService(context: Context, callbackOnUpdate: Runnable, callbackOnError: Runnable) :
        BaseService(callbackOnUpdate, callbackOnError) {


    private var currentTime = Calendar.getInstance().time
    private val dateFormat: SimpleDateFormat
    private val timeFormat: SimpleDateFormat
    var dateString = ""
    var timeString = ""

    init {
        val currentLocale = ConfigurationCompat.getLocales(context.getResources().configuration)[0]
        dateFormat = SimpleDateFormat("dd MMMM yyyy", currentLocale)
        timeFormat = SimpleDateFormat("HH:mm", currentLocale)
    }

    override fun getInterval(): Long {
        return 10
    }

    override fun work() {
        currentTime = Calendar.getInstance().time
        dateString = dateFormat.format(currentTime.time)
        timeString = timeFormat.format(currentTime.time)
    }

}