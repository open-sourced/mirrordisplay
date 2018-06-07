package de.opensourced.mirrordisplay.services

interface MirrorService {

    fun getInterval() : Long {
        return 100
    }

    fun startService()

    fun stopService()
}