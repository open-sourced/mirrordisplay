package de.opensourced.mirrordisplay.services

import android.content.Context
import de.opensourced.mirrordisplay.models.CalendarEvent
import de.opensourced.mirrordisplay.util.CalendarUtil
import java.util.*


class AgendaService(private val context: Context, callbackOnUpdate: Runnable,
                    callbackOnError: Runnable) : BaseService(callbackOnUpdate, callbackOnError) {

    private val timeZoneCalendar = "GMT"

    val events = ArrayList<CalendarEvent>()

    override fun getInterval(): Long {
        return 3 * 60 * 1000
    }

    override fun work() {
        val start = Calendar.getInstance(TimeZone.getTimeZone(timeZoneCalendar))
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.add(Calendar.SECOND, -1)
        val end = Calendar.getInstance(TimeZone.getTimeZone(timeZoneCalendar))
        end.set(Calendar.HOUR_OF_DAY, 0)
        end.set(Calendar.MINUTE, 0)
        end.set(Calendar.SECOND, 0)
        end.add(Calendar.SECOND, 1)
        end.add(Calendar.DAY_OF_YEAR, 1)
        events.clear()
        events.addAll(CalendarUtil.getCalendarEventsByStart(context, null, start, end))
    }

}