package de.opensourced.mirrordisplay.services

import android.content.Context
import de.opensourced.mirrordisplay.models.RssFeedData
import com.prof.rssparser.Article
import android.util.Log
import android.widget.Toast
import com.prof.rssparser.Parser
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
        parser.onFinish(object : Parser.OnTaskCompleted {

            override fun onTaskCompleted(list: ArrayList<Article>) {
                list.sortByDescending { it.pubDate }
                feedData.clear()
                list.subList(0, numberOfVisibleFeedItems - 1).forEach({
                    feedData.add(RssFeedData(it.title, it.pubDate.time))
                })
                callbackOnUpdate.run()
            }

            override fun onError() {
                Log.w("RssService", "RssFeed fetch failed for url: $feedUrl")
                callbackOnError.run()
            }
        })
    }

}