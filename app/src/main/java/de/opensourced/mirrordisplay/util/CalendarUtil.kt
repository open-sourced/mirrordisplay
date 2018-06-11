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
            val EVENT_PROJECTION = arrayOf(
                    CalendarContract.Calendars._ID,
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                    CalendarContract.Calendars.CALENDAR_COLOR
            )
            val calendars = ArrayList<CalendarModel>()
            val cr = context.getContentResolver()
            val uri = CalendarContract.Calendars.CONTENT_URI
            try {
                val cur = cr.query(uri, EVENT_PROJECTION, null, null, null)
                while (cur.moveToNext()) {
                    calendars.add(CalendarModel(cur.getString(1), cur.getInt(2), cur.getLong(0)))
                }
            } catch (ex: SecurityException) {
                Log.w("CalendarUtil", ex)
            } finally {
                return calendars
            }

        }

        /**
         * Fetches calendar events in given timeframe, that means start and end time of event must be in timeframe
         *
         * [calendarId] might be null to lookup events from all available calendars or
         * specified to only query a specific calendar
         * [start] and [end] can also be null to have no time restriction
         */
        fun getCalendarEventsInTimeframe(context: Context, calendarId: Int?, start: Calendar?, end: Calendar?): List<CalendarEvent> {
            val timeSelectionStr =
                    (if (start != null) "dtstart >" + start.getTimeInMillis() else "") +
                            (if (start != null && end != null) " AND " else "") +
                            (if (end != null) "dtend <" + end.getTimeInMillis() else "")

            val events = ArrayList<CalendarEvent>()
            val cursor = context.getContentResolver().query(
                    Uri.parse("content://com.android.calendar/events"),
                    arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                    (if (calendarId != null) "(calendar_id=$calendarId) " else "") +
                            (if ((start != null || end != null) && calendarId != null) " AND " else "") +
                            if (!timeSelectionStr.isEmpty()) "($timeSelectionStr)" else "",
                    null,
                    null
            )
            cursor.moveToFirst()
            val length = cursor.getCount()
            for (i in 0 until length) {
                val ce = CalendarEvent(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getLong(4),
                        cursor.getString(5)
                )
                if (cursor.getString(1).isNotEmpty()) {
                    events.add(ce)
                }
                cursor.moveToNext()
            }
            cursor.close()
            return events
        }

        /**
         * Fetches events with start time in given timeframe
         *
         * [calendarId] might be null to lookup events from all available calendars or
         * specified to only query a specific calendar
         * [start] and [end] can also be null to have no time restriction
         */
        fun getCalendarEventsByStart(context: Context, calendarId: Int?, start: Calendar?, end: Calendar?): List<CalendarEvent> {
            val timeSelectionStr =
                    (if (start != null) "dtstart >" + start.getTimeInMillis() else "") +
                            (if (start != null && end != null) " AND " else "") +
                            (if (end != null) "dtstart <" + end.getTimeInMillis() else "")

            val events = ArrayList<CalendarEvent>()
            val cursor = context.getContentResolver().query(
                    Uri.parse("content://com.android.calendar/events"),
                    arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                    (if (calendarId != null) "(calendar_id=$calendarId) " else "") +
                            (if ((start != null || end != null) && calendarId != null) " AND " else "") +
                            if (!timeSelectionStr.isEmpty()) "($timeSelectionStr)" else "",
                    null,
                    null
            )
            cursor.moveToFirst()
            val length = cursor.getCount()
            for (i in 0 until length) {
                val ce = CalendarEvent(
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getLong(3),
                        cursor.getLong(4),
                        cursor.getString(5)
                )
                if (cursor.getString(1).isNotEmpty()) {
                    events.add(ce)
                }
                cursor.moveToNext()
            }
            cursor.close()
            return events
        }
    }
}