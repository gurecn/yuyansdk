package com.yuyan.imemodule.utils.thread

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author yueban
 * @date 2019-07-18
 * @email fbzhh007@gmail.com
 */
class NamingThreadFactory @JvmOverloads constructor(poolName: String, daemon: Boolean = false) :
    ThreadFactory {
    private val group: ThreadGroup?
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String
    private val daemon: Boolean

    init {
        val securityManager = System.getSecurityManager()
        group = if (securityManager != null) securityManager.threadGroup else Thread.currentThread().getThreadGroup()
        namePrefix = "$poolName-thread-"
        this.daemon = daemon
    }

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0)
        t.setDaemon(daemon)
        if (t.priority != Thread.NORM_PRIORITY) {
            t.setPriority(Thread.NORM_PRIORITY)
        }
        return t
    }
}
