package com.yuyan.imemodule.libs.expression

import java.util.EmptyStackException

internal class ArrayStack @JvmOverloads constructor(initialCapacity: Int = 5) {
    private var data: DoubleArray
    private var idx: Int

    init {
        require(initialCapacity > 0) { "Stack's capacity must be positive" }
        data = DoubleArray(initialCapacity)
        idx = -1
    }

    fun push(value: Double) {
        if (idx + 1 == data.size) {
            val temp = DoubleArray((data.size * 1.2).toInt() + 1)
            System.arraycopy(data, 0, temp, 0, data.size)
            data = temp
        }
        data[++idx] = value
    }

    fun pop(): Double {
        if (idx == -1) {
            throw EmptyStackException()
        }
        return data[idx--]
    }

    fun size(): Int {
        return idx + 1
    }
}
