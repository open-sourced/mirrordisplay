package de.opensourced.mirrordisplay.util

import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import de.opensourced.mirrordisplay.models.CalendarEvent
import de.opensourced.mirrordisplay.models.CalendarModel
import java.util.*

class CalendarUtil {
    companion object {
        fun getCalendars(context: Context): List<CalendarModel> {
            val eventProjection = arrayOf(
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR
            )
            val calendars = ArrayList<CalendarModel>()
            val resolverCalendars = context.contentResolver
            val uri = CalendarContract.Calendars.CONTENT_URI
            try {
                val cursorCalendars = resolverCalendars.query(uri, eventProjection, null, null, null)
                while (cursorCalendars != null && cursorCalendars.moveToNext()) {
                    calendars.add(CalendarModel(cursorCalendars.getString(1), cursorCalendars.getInt(2), cursorCalendars.getLong(0)))
                }
                cursorCalendars?.close()
            } catch (ex: SecurityException) {
                Log.w("CalendarUtil", ex)
            } finally {
                return calendars
            }

        }

        /**
         * Fetches calendar events in given time frame, that means start and end time of event must be in time frame
         *
         * [calendarId] might be null to lookup events from all available calendars or
         * specified to only query a specific calendar
         * [start] and [end] can also be null to have no time restriction
         */
        fun getCalendarEventsInTimeframe(
                context: Context,
                calendarId: Int?,
                start: Calendar?,
                end: Calendar?
        ): List<CalendarEvent> {
            val timeSelectionStr =
                    (if (start != null) "dtstart >" + start.timeInMillis else "") +
                            (if (start != null && end != null) " AND " else "") +
                            (if (end != null) "dtend <" + end.timeInMillis else "")

            val events = ArrayList<CalendarEvent>()
            val cursorEvents = context.contentResolver.query(
                    Uri.parse("content://com.android.calendar/events"),
                    arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                    (if (calendarId != null) "(calendar_id=$calendarId) " else "") +
                            (if ((start != null || end != null) && calendarId != null) " AND " else "") +
                            if (!timeSelectionStr.isEmpty()) "($timeSelectionStr)" else "",
                    null,
                    null
            )
            if (cursorEvents != null) {
                cursorEvents.moveToFirst()
                val length = cursorEvents.count
                for (i in 0 until length) {
                    val ce = CalendarEvent(
                            cursorEvents.getString(1),
                            cursorEvents.getString(2),
                            cursorEvents.getLong(3),
                            cursorEvents.getLong(4),
                            cursorEvents.getString(5)
                    )
                    if (cursorEvents.getString(1).isNotEmpty()) {
                        events.add(ce)
                    }
                    cursorEvents.moveToNext()
                }
                cursorEvents.close()
            }
            return events
        }

        /**
         * Fetches events with start time in given timeframe
         *
         * [calendarId] might be null to lookup events from all available calendars or
         * specified to only query a specific calendar
         * [start] and [end] can also be null to have no time restriction
         */
        fun getCalendarEventsByStart(
                context: Context,
                calendarId: Int?,
                start: Calendar?,
                end: Calendar?
        ): List<CalendarEvent> {
            val timeSelectionStr =
                    (if (start != null) "dtstart >" + start.timeInMillis else "") +
                            (if (start != null && end != null) " AND " else "") +
                            (if (end != null) "dtstart <" + end.timeInMillis else "")

            val events = ArrayList<CalendarEvent>()
            val cursorEvents = context.contentResolver.query(
                    Uri.parse("content://com.android.calendar/events"),
                    arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                    (if (calendarId != null) "(calendar_id=$calendarId) " else "") +
                            (if ((start != null || end != null) && calendarId != null) " AND " else "") +
                            if (timeSelectionStr.isNotEmpty()) "($timeSelectionStr)" else "",
                    null,
                    null
            )
            if (cursorEvents != null) {
                cursorEvents.moveToFirst()
                for (i in 0 until cursorEvents.count) {
                    val ce = CalendarEvent(
                            cursorEvents.getString(1),
                            cursorEvents.getString(2),
                            cursorEvents.getLong(3),
                            cursorEvents.getLong(4),
                            cursorEvents.getString(5)
                    )
                    if (cursorEvents.getString(1).isNotEmpty()) {
                        events.add(ce)
                    }
                    cursorEvents.moveToNext()
                }
                cursorEvents.close()
            }
            return events
        }
    }
}