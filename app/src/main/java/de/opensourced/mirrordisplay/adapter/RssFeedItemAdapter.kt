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
import de.opensourced.mirrordisplay.models.RssFeedData
import de.opensourced.mirrordisplay.util.Constants.Companion.RSSFEED_TIME_FORMAT
import kotlinx.android.synthetic.main.rssfeed_item.view.*
import java.util.*
import kotlin.collections.HashSet

class RssFeedItemAdapter(private val context: Context, private val items: HashSet<RssFeedData>)
    : RecyclerView.Adapter<ViewHolderRssFeed>() {


    private val currentLocale : Locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRssFeed {
        return ViewHolderRssFeed(LayoutInflater.from(context)
                .inflate(R.layout.rssfeed_item, parent, false))
    }

    override fun onBindViewHolder(viewHolderRssFeed: ViewHolderRssFeed, position: Int) {
        val cal = Calendar.getInstance(currentLocale)
        val rssFeedItem = items.elementAt(position)
        cal.timeInMillis = rssFeedItem.time
        val pubDate = DateFormat.format(RSSFEED_TIME_FORMAT, cal).toString()
        viewHolderRssFeed.calendarItemTvTitle.text = rssFeedItem.title
        viewHolderRssFeed.calendarItemTvTime.text = pubDate
    }

    override fun getItemCount(): Int {
        return items.size
    }

}

class ViewHolderRssFeed(view: View) : RecyclerView.ViewHolder(view) {
    val calendarItemTvTitle: TextView = view.rssfeedItem_tvTitle
    val calendarItemTvTime: TextView = view.rssfeedItem_tvTime
}