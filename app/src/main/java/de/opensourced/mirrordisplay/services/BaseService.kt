package de.opensourced.mirrordisplay.services

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean


abstract class BaseService(val callbackOnUpdate: Runnable, val callbackOnError: Runnable) : MirrorService {

    private val executorService: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private lateinit var serviceTaskFuture: ScheduledFuture<*>
    val isRunning: AtomicBoolean = AtomicBoolean(false)
    var lastException: Throwable? = null

    override fun getIntervalMilliseconds(): Long {
        return 100
    }

    abstract fun work()

    override fun startService() {
        if (isRunning.compareAndSet(false, true)) {
            serviceTaskFuture = executorService.scheduleWithFixedDelay(
                    {
                        try {
                            work()
                        } catch (ex: Throwable) {
                            lastException = ex
                            callbackOnError.run()
                        } finally {
                            callbackOnUpdate.run()
                        }
                    },
                    0,
                    getIntervalMilliseconds(),
                    TimeUnit.MILLISECONDS
            )
        }
    }

    override fun stopService() {
        if (isRunning.compareAndSet(true, false)) {
            serviceTaskFuture.cancel(true)
        }
    }
}