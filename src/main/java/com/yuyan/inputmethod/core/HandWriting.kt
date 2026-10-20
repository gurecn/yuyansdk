package com.yuyan.inputmethod.core

import android.content.Context

class HandWriting private constructor(private var handle: Long) {

    class Output(val shape: LongArray, val data: FloatArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Output) return false
            return shape.contentEquals(other.shape) && data.contentEquals(other.data)
        }

        override fun hashCode(): Int = 31 * shape.contentHashCode() + data.contentHashCode()

        override fun toString(): String =
            "Output(shape=${shape.contentToString()}, size=${data.size})"
    }

    fun infer(input: FloatArray, shape: LongArray): Output {
        val current = handle
        val outShape = LongArray(MAX_OUTPUT_RANK) { -1L }
        val data = nativeRun(current, input, shape, outShape)
            ?: throw IllegalStateException(
                nativeLastError().ifEmpty { "native 推理失败且未返回错误信息" }
            )
        val rank = outShape.indexOfFirst { it < 0 }.let { if (it < 0) outShape.size else it }
        return Output(outShape.copyOf(rank), data)
    }

    fun release() {
        val current = handle
        handle = 0L
        if (current != 0L) {
            nativeRelease(current)
        }
    }

    companion object {
        const val LIB_NAME = "yuyanwriting"
        private const val MAX_OUTPUT_RANK = 4

        const val POWER_MODE_HIGH = 0
        const val POWER_MODE_LOW = 1
        const val POWER_MODE_FULL = 2
        const val POWER_MODE_NO_BIND = 3

        private val loaded: Boolean by lazy {
            try {
                System.loadLibrary(LIB_NAME)
                true
            } catch (t: Throwable) {
                false
            }
        }

        fun isAvailable(): Boolean = loaded


        fun create(
            context: Context,
            modelPath: String,
            cpuThreads: Int,
            powerMode: Int = POWER_MODE_HIGH
        ): HandWriting {
            val handle = nativeCreate(context, modelPath, cpuThreads, powerMode)
            check(handle != 0L) {
                nativeLastError().ifEmpty { "创建 predictor 失败且未返回错误信息" }
            }
            return HandWriting(handle)
        }

        @JvmStatic
        private external fun nativeCreate(
            context: Context,
            modelPath: String,
            threads: Int,
            powerMode: Int
        ): Long

        @JvmStatic
        private external fun nativeRun(
            handle: Long,
            input: FloatArray,
            shape: LongArray,
            outShape: LongArray
        ): FloatArray?

        @JvmStatic
        private external fun nativeRelease(handle: Long)

        @JvmStatic
        private external fun nativeLastError(): String
    }
}