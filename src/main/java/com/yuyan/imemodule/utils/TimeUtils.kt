package com.yuyan.imemodule.utils

import com.yuyan.imemodule.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时间工具类
 */
object TimeUtils {
    @JvmField
    val DEFAULT_DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    // 系统时间与版本构建相差天数
    fun getBuildDiffDays():Int {
        val starDay = DEFAULT_DATE_FORMATTER.parse(BuildConfig.AppBuildTime) //构建时间
        val endDay = Date()  //当前时间
        val dayNum =  if(starDay != null ) {
            (endDay.time - starDay.time) / (24 * 60 * 60 * 1000)
        } else 0
        return  dayNum.toInt()
    }
}
