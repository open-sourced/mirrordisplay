package de.opensourced.mirrordisplay.services

import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean


abstract class BaseService(val callbackOnUpdate: Runnable, val callbackOnError: Runnable) : MirrorService {

    private val executorService: ScheduledThreadPoolExecutor = ScheduledThreadPoolExecutor(1)
    private lateinit var serviceTaskFuture: ScheduledFuture<*>
    val isRunning: AtomicBoolean
    var lastException: RuntimeException? = null

    init {
        isRunning = AtomicBoolean(false)
    }

    override fun getInterval(): Long {
        return 100
    }

    abstract fun work()

    override fun startService() {
        if (isRunning.compareAndSet(false, true)) {
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
    }

    override fun stopService() {
        if (isRunning.compareAndSet(true, false)) {
            serviceTaskFuture.cancel(true)
        }
    }
}