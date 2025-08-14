package com.yuyan.imemodule.utils.thread

import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.max

class ThreadPoolUtils private constructor() {

    companion object {
        /**
         * 默认线程池
         */
        private var DEFAULT_EXECUTOR: ThreadPoolExecutor? = null

        /**
         * 单线程线程池
         */
        private var SINGLETON_EXECUTOR: ThreadPoolExecutor? = null

        /**
         * 定时任务线程池
         */
        private var SCHEDULED_EXECUTOR: ScheduledThreadPoolExecutor? = null

        /**
         * 定时任务线程池 (创建守护线程执行)
         */
        private var SCHEDULED_DAEMON_EXECUTOR: ScheduledThreadPoolExecutor? = null

        init {
            val cpuCount = Runtime.getRuntime().availableProcessors()
            val corePoolSize = max((cpuCount * 2).toDouble(), 4.0).toInt()
            DEFAULT_EXECUTOR = ThreadPoolExecutor(
                corePoolSize, corePoolSize * 2, 30, TimeUnit.SECONDS, LinkedBlockingQueue(),
                NamingThreadFactory("ThreadPoolUtils")
            ) { r, executor ->
                try {
                    executor.queue.put(r)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            SCHEDULED_EXECUTOR = ScheduledThreadPoolExecutor(
                corePoolSize,
                NamingThreadFactory("ThreadPoolUtils-scheduled", false)
            )
            SCHEDULED_DAEMON_EXECUTOR = ScheduledThreadPoolExecutor(
                corePoolSize,
                NamingThreadFactory("ThreadPoolUtils-scheduled-daemon", true)
            )
            SINGLETON_EXECUTOR = newSingletonExecutor("ThreadPoolUtils-singleton")
        }

        fun execute(runnable: Runnable?) {
            DEFAULT_EXECUTOR!!.execute(runnable)
        }

        fun executeSingleton(runnable: Runnable?) {
            SINGLETON_EXECUTOR!!.execute(runnable)
        }

        /**
         * 调用线程池执行定时任务
         *
         * @param runnable 任务
         * @param delay    启动延迟
         * @param unit     启动延迟的时间单位
         */
        fun schedule(runnable: Runnable?, delay: Long, unit: TimeUnit?): ScheduledFuture<*> {
            return SCHEDULED_EXECUTOR!!.schedule(runnable, delay, unit)
        }

        /**
         * 调用线程池执行定时的周期任务
         *
         * @param runnable     任务
         * @param initialDelay 启动延迟, millseconds
         * @param period       执行周期
         * @param unit         执行周期时间单位
         */
        fun scheduleAtFixedRate(
            runnable: Runnable?,
            initialDelay: Long,
            period: Long,
            unit: TimeUnit?
        ): ScheduledFuture<*> {
            return SCHEDULED_EXECUTOR!!.scheduleAtFixedRate(runnable, initialDelay, period, unit)
        }

        /**
         * 调用线程池执行定时任务 (在守护线程中执行)
         *
         * @param runnable 任务
         * @param delay    启动延迟
         * @param unit     启动延迟的时间单位
         */
        fun scheduleInDaemon(
            runnable: Runnable?,
            delay: Long,
            unit: TimeUnit?
        ): ScheduledFuture<*> {
            return SCHEDULED_DAEMON_EXECUTOR!!.schedule(runnable, delay, unit)
        }

        /**
         * 调用线程池执行定时的周期任务 (在守护线程中执行)
         *
         * @param runnable     任务
         * @param initialDelay 启动延迟, millseconds
         * @param period       执行周期
         * @param unit         执行周期时间单位
         */
        fun scheduleAtFixedRateInDaemon(
            runnable: Runnable?,
            initialDelay: Long,
            period: Long,
            unit: TimeUnit?
        ): ScheduledFuture<*> {
            return SCHEDULED_DAEMON_EXECUTOR!!.scheduleAtFixedRate(
                runnable,
                initialDelay,
                period,
                unit
            )
        }

        /**
         * 创建一个单线程线程池
         *
         * @param threadPoolName 线程池名称
         * @return 新创建的单线程线程池
         */
        fun newSingletonExecutor(threadPoolName: String?): ThreadPoolExecutor {
            return ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue(),
                NamingThreadFactory(threadPoolName!!)
            )
        }

        /**
         * 创建一个定时任务程线程池
         *
         * @param threadPoolName 线程池名称
         * @param daemon         是否为守护线程
         * @return 新创建的定时任务线程池
         */
        fun newScheduledExecutor(
            threadPoolName: String?,
            daemon: Boolean
        ): ScheduledThreadPoolExecutor {
            return ScheduledThreadPoolExecutor(
                1, NamingThreadFactory(
                    threadPoolName!!, daemon
                )
            )
        }

        /**
         * 取消任务
         *
         * @param future 待取消任务
         */
        fun cancel(future: Future<*>) {
            future.cancel(true)
        }
    }
}
