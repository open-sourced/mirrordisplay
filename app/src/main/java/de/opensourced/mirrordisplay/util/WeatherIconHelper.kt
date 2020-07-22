package de.opensourced.mirrordisplay.util

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import de.opensourced.mirrordisplay.R

class WeatherIconHelper {

    fun loadIcon(icon: String, imageView: ImageView) {
        val iconUrl = "http://openweathermap.org/img/wn/$icon@2x.png"
        Picasso.get()
                .load(iconUrl)
                .resize(200, 200)
                .placeholder(R.drawable.ic_weather_sunny)
                .error(R.drawable.ic_error)
                .into(imageView, object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: java.lang.Exception?) {
                        Log.d("Error", "Error while loading image with Picasso", e)
                    }
                })
    }
}
