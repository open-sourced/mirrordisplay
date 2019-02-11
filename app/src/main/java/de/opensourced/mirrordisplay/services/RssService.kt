package de.opensourced.mirrordisplay.services

import android.content.Context
import de.opensourced.mirrordisplay.models.RssFeedData
import com.prof.rssparser.Article
import android.util.Log
import android.widget.Toast
import com.prof.rssparser.OnTaskCompleted
import com.prof.rssparser.Parser
import java.time.Instant
import kotlin.collections.ArrayList


class RssService(private val context: Context, val feedUrl: String, val numberOfVisibleFeedItems: Int,
                 callbackOnUpdate: Runnable, callbackOnError: Runnable)
    : BaseService(callbackOnUpdate, callbackOnError) {

    val feedData = ArrayList<RssFeedData>()

    override fun getIntervalMilliseconds(): Long {
        return 5 * 60 * 1000
    }

    override fun work() {
        val parser = Parser()
        parser.execute(feedUrl)
        parser.onFinish(object : OnTaskCompleted {
            override fun onError(e: Exception) {
                Log.w("RssService", "RssFeed fetch failed for url: $feedUrl")
                callbackOnError.run()
            }

            override fun onTaskCompleted(list: MutableList<Article>) {
                list.sortByDescending { it.pubDate }
                feedData.clear()
                list.subList(0, numberOfVisibleFeedItems - 1).forEach {
                    val title = it.title
                    val time = it.pubDate?.time
                    if (time != null && title != null) {
                        feedData.add(RssFeedData(title, time))
                    }
                }
                callbackOnUpdate.run()
            }

        })
    }

}