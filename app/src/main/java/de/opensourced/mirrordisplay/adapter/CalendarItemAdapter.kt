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
import de.opensourced.mirrordisplay.util.Constants.Companion.CALENDAR_TIME_FORMAT
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.util.*
import kotlin.collections.HashSet

class CalendarItemAdapter(private val context: Context, private val items: HashSet<CalendarEvent>)
    : RecyclerView.Adapter<ViewHolderCalenderItem>() {


    private val currentLocale : Locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCalenderItem {
        return ViewHolderCalenderItem(LayoutInflater.from(context)
                .inflate(R.layout.calendar_item, parent, false))
    }

    override fun onBindViewHolder(holderCalenderItem: ViewHolderCalenderItem, position: Int) {
        val cal = Calendar.getInstance(currentLocale)
        val calendarEvent = items.elementAt(position)
        cal.timeInMillis = calendarEvent.start
        val startDate = DateFormat.format(CALENDAR_TIME_FORMAT, cal).toString()
        cal.timeInMillis = calendarEvent.end
        val endDate = DateFormat.format(CALENDAR_TIME_FORMAT, cal).toString()
        holderCalenderItem.calendarItemTvTitle.text = calendarEvent.title
        if(calendarEvent.start >0 && calendarEvent.start >0) {

            holderCalenderItem.calendarItemTvTime.text = "$startDate - $endDate"
        }else{
            holderCalenderItem.calendarItemTvTime.text = ""
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolderCalenderItem(view: View) : RecyclerView.ViewHolder(view) {
    val calendarItemTvTitle: TextView = view.calendarItem_tvTitle
    val calendarItemTvTime: TextView = view.calendarItem_tvTime
}