package de.opensourced.mirrordisplay.exceptions

import java.lang.Exception

class WeatherRequestFailedException(message: String, throwable: Throwable?) : Exception(message, throwable) {
}