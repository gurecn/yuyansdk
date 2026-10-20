package com.yuyan.imemodule.handwriting.internal

import android.content.Context
import android.content.res.AssetManager
import com.yuyan.imemodule.handwriting.Candidate
import com.yuyan.inputmethod.core.HandWriting
import java.io.File
import java.io.IOException

internal class Predictor(
    private val context: Context,
    private val cpuThreads: Int = DEFAULT_CPU_THREADS,
    private val powerMode: Int = DEFAULT_POWER_MODE
) {

    companion object {
        const val REC_MODEL_ASSET = "hw/rec.nb"

        const val DICT_ASSET = "hw/ocrv5.txt"

        const val DEFAULT_CPU_THREADS = 4

        const val DEFAULT_POWER_MODE = HandWriting.POWER_MODE_HIGH

        private const val MIN_MODEL_BYTES = 100 * 1024

        private const val MODEL_DIR_NAME = "handwriting"

        private const val COPY_BUFFER_BYTES = 64 * 1024

        @JvmStatic
        fun materializeModel(context: Context): File? {
            val assets = context.assets
            val expected = assetLength(assets, REC_MODEL_ASSET)
            if (expected == null || expected < MIN_MODEL_BYTES) return null

            val dir = File(context.filesDir, MODEL_DIR_NAME)
            if (!dir.exists() && !dir.mkdirs()) return null

            val target = File(dir, REC_MODEL_ASSET.substringAfterLast('/'))
            if (target.length() == expected) return target

            return try {
                assets.open(REC_MODEL_ASSET).use { input ->
                    target.outputStream().use { out ->
                        val buffer = ByteArray(COPY_BUFFER_BYTES)
                        while (true) {
                            val read = input.read(buffer)
                            if (read <= 0) break
                            out.write(buffer, 0, read)
                        }
                    }
                }
                target
            } catch (e: IOException) {
                target.delete()
                null
            }
        }

        private fun assetLength(assets: AssetManager, path: String): Long? = try {
            assets.openFd(path).use { it.length }
        } catch (e: IOException) {
            try {
                assets.open(path).use { it.available().toLong() }
            } catch (e2: IOException) {
                null
            }
        }
    }

    private val preprocessor = ImagePreprocessor()

    private var runtime: HandWriting? = null
    private var decoder: CtcDecoder? = null
    private var expectedClasses = 0


    fun isReady(): Boolean = runtime != null && decoder != null

    fun load(): Boolean {
        if (!HandWriting.isAvailable()) {
            return false
        }
        val modelFile = materializeModel(context) ?: return false
        if (modelFile.length() < MIN_MODEL_BYTES) {
            return false
        }

        val dictionary = readDictionary()
        if (dictionary == null || dictionary.size < 1000) {
            return false
        }

        return try {
            runtime = HandWriting.create(context, modelFile.absolutePath, cpuThreads, powerMode)
            decoder = CtcDecoder(dictionary)
            expectedClasses = dictionary.size + 2
            true
        } catch (e: Exception) {
            runtime = null
            decoder = null
            false
        }
    }

    fun predict(input: RecInput): List<Candidate> {
        val runtime = this.runtime ?: return emptyList()
        val decoder = this.decoder ?: return emptyList()

        return try {
            val output = synchronized(this) {
                runtime.infer(input.data, input.shape())
            }
            val shape = output.shape
            val classes = if (shape.size >= 2) {
                shape[shape.size - 1].toInt().coerceAtLeast(2)
            } else {
                expectedClasses
            }
            val timesteps = if (shape.size >= 2) {
                shape[shape.size - 2].toInt().coerceAtLeast(1)
            } else {
                output.data.size / classes
            }
            decoder.decodeBeam(output.data, timesteps, classes)
                .filter { it.text.isNotBlank() }
                .map { Candidate(it.text, it.confidence) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun warmUp() {
        try {
            predict(preprocessor.renderStrokesForRec(emptyList()))
        } catch (e: Exception) {
        }
    }

    private fun readDictionary(): List<String>? = try {
        context.assets.open(DICT_ASSET).bufferedReader().use { reader ->
            reader.readLines().map { it.trimEnd('\r') }
        }.let { lines ->
            var end = lines.size
            while (end > 0 && lines[end - 1].isEmpty()) end--
            lines.subList(0, end).toList()
        }
    } catch (e: IOException) {
        null
    }
}
