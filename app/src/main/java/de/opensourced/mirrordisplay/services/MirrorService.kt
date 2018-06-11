package de.opensourced.mirrordisplay.services

interface MirrorService {

    fun getIntervalMilliseconds() : Long {
        return 100
    }

    fun startService()

    fun stopService()
}