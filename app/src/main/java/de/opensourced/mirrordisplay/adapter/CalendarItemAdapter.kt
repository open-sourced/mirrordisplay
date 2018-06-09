package de.opensourced.mirrordisplay.adapter

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.opensourced.mirrordisplay.R
import de.opensourced.mirrordisplay.models.CalendarEvent
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.util.*

class CalendarItemAdapter(private val context: Context, private val items: ArrayList<CalendarEvent>)
    : RecyclerView.Adapter<ViewHolderCalenderItem>() {

    private val CALENDAR_TIME_FORMAT = "hh:mm:ss"

    private val currentLocale : Locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalenderItem {
        return ViewHolderCalenderItem(LayoutInflater.from(context)
                .inflate(R.layout.calendar_item, parent, false))
    }

    override fun onBindViewHolder(holderCalenderItem: ViewHolderCalenderItem, position: Int) {
        val cal = Calendar.getInstance(currentLocale)
        val calendarEvent = items[position]
        cal.timeInMillis = calendarEvent.start
        val startDate = DateFormat.format(CALENDAR_TIME_FORMAT, cal).toString()
        cal.timeInMillis = calendarEvent.end
        val endDate = DateFormat.format(CALENDAR_TIME_FORMAT, cal).toString()
        holderCalenderItem.calendarItem_tvTitle.text = calendarEvent.title
        holderCalenderItem.calendarItem_tvTime.text = "$startDate - $endDate"
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolderCalenderItem(view: View) : RecyclerView.ViewHolder(view) {
    val calendarItem_tvTitle: TextView = view.calendarItem_tvTitle
    val calendarItem_tvTime: TextView = view.calendarItem_tvTime
}