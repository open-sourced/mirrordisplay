package de.opensourced.mirrordisplay.services

import android.content.Context
import de.opensourced.mirrordisplay.models.CalendarEvent
import de.opensourced.mirrordisplay.util.CalendarUtil
import java.util.*


class AgendaService(private val context: Context, callbackOnUpdate: Runnable,
                    callbackOnError: Runnable) : BaseService(callbackOnUpdate, callbackOnError) {

    val events = ArrayList<CalendarEvent>()

    override fun getInterval(): Long {
        return 30 * 60 * 1000
    }

    override fun work() {
        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        val end = Calendar.getInstance()
        end.set(Calendar.HOUR_OF_DAY, 0)
        end.set(Calendar.MINUTE, 0)
        end.set(Calendar.SECOND, 0)
        end.add(Calendar.DAY_OF_YEAR, 1)
        events.clear()
        events.addAll(CalendarUtil.getCalendarEvents(context, null, start, end))
    }

}