package com.yuyan.imemodule.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时间工具类
 * User:Gaolei  gurecn@gmail.com
 * Date:2017/12/6
 * I'm glad to share my knowledge with you all.
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
