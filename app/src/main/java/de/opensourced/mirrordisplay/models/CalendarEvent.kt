package de.opensourced.mirrordisplay.models

import java.sql.Timestamp

class CalendarEvent(
        val title : String,
        val note : String,
        val start : Long,
        val end : Long,
        val location : String
)