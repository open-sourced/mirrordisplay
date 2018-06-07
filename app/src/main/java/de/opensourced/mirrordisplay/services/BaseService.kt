package de.opensourced.mirrordisplay.services

import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit


abstract class BaseService(val callbackOnUpdate: Runnable, val callbackOnError: Runnable) : MirrorService {

    private val executorService: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    private lateinit var serviceTaskFuture: ScheduledFuture<*>
    var lastException: RuntimeException? = null

    override fun getInterval(): Long {
        return 100
    }

    abstract fun work()

    override fun startService() {
        serviceTaskFuture = executorService.scheduleWithFixedDelay(
                {
                    try {
                        work()
                    } catch (ex: RuntimeException) {
                        lastException = ex
                        callbackOnError.run()
                    } finally {
                        callbackOnUpdate.run()
                    }
                },
                0,
                getInterval(),
                TimeUnit.SECONDS
        )
    }

    override fun stopService() {
        serviceTaskFuture.cancel(true)
    }
}