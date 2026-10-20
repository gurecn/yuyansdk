package com.yuyan.imemodule.handwriting

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.yuyan.imemodule.handwriting.internal.Predictor
import com.yuyan.imemodule.handwriting.internal.RecEngine
import com.yuyan.inputmethod.core.HandWriting
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object HandwritingRecognizer {

    private val cpuThreads: Int = resolveCpuThreads()
    private val powerMode: Int = HandWriting.POWER_MODE_HIGH

    private val worker: ExecutorService by lazy {
        Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "hw-rec").apply { isDaemon = true }
        }
    }
    private val mainHandler = Handler(Looper.getMainLooper())
    private var engine: RecEngine? = null

    val isReady: Boolean
        get() = engine?.isReady == true

    private fun resolveCpuThreads(): Int {
        val cores = Runtime.getRuntime().availableProcessors()
        return when {
            cores >= 6 -> 4
            cores >= 4 -> 3
            else -> 2
        }
    }

    fun prepareModel(context: Context): Boolean =
        Predictor.materializeModel(context.applicationContext) != null

    fun initialize(context: Context): Boolean = synchronized(this) {
        val existing = engine
        if (existing != null && existing.isReady) return true

        val target = existing ?: RecEngine(
            context.applicationContext,
            cpuThreads,
            powerMode
        ).also { engine = it }
        val loaded = target.load()
        loaded
    }

    fun initializeAsync(context: Context, callback: ((Boolean) -> Unit)? = null) {
        if (isReady) {
            callback?.let { mainHandler.post { it(true) } }
            return
        }
        val appContext = context.applicationContext
        worker.execute {
            val ok = initialize(appContext)
            callback?.let { mainHandler.post { it(ok) } }
        }
    }

    fun recognizeBlocking(strokes: List<Stroke>): List<Candidate> {
        val target = engine ?: return emptyList()
        if (!target.isReady || strokes.isEmpty()) return emptyList()
        return try {
            target.recognize(strokes)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
