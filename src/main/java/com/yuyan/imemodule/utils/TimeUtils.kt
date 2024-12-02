package com.yuyan.imemodule.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时间工具类
 */
object TimeUtils {
    @JvmField
    val DEFAULT_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    /**
     * long time to string
     */
    @JvmStatic
    fun getTime(timeInMillis: Long, dateFormat: SimpleDateFormat): String {
        return dateFormat.format(Date(timeInMillis))
    }

    private val currentTimeInLong: Long
        /**
         * get current time in milliseconds
         */
        get() = System.currentTimeMillis()

    /**
     * get current time in milliseconds, format is [.DEFAULT_DATE_FORMATTER]
     */
    @JvmStatic
    fun getCurrentTimeInString(dateFormat: SimpleDateFormat): String {
        return getTime(currentTimeInLong, dateFormat)
    }
}
