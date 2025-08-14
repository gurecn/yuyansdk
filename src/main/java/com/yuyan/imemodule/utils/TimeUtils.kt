package com.yuyan.imemodule.utils

import android.os.Build
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.abs

/**
 * 时间工具类
 */
object TimeUtils {

    fun iso8601UTCDateTime(timeMillis: Long? = null): String {
        return SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.ROOT).format(timeMillis?.let { Date(it) } ?: Date())
    }

    fun getData(day:String = ""):List<String> {
        val data = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (day.isNotBlank()) {
                val weeks = arrayOf("一", "二", "三", "四", "五", "六", "日")
                val diffDays = diffDaysPreset[day] ?: 0
                val today = if (diffDays > 0) LocalDate.now().plusDays(diffDays.toLong()) else LocalDate.now().minusDays(abs(diffDays.toLong()))
                val dayOfMonth = today.dayOfMonth
                val month = today.monthValue
                val dayOfWeek = weeks[today.dayOfWeek.value - 1]
                data.add("(${month}月${dayOfMonth}日周$dayOfWeek)")
                val formatter = DateTimeFormatter.ofPattern("MMdd")
                val dataMMdd = today.format(formatter).toInt()
                val gregorianFestival = daysGregorianFestivalPreset[dataMMdd]
                if (gregorianFestival?.isNotEmpty() == true) data.addAll(gregorianFestival)
            } else {
                val today = LocalDate.now()
                val dayOfMonth = today.dayOfMonth
                val month = today.monthValue
                data.add("${month}月${dayOfMonth}日")
            }
        }
        return data
    }

    fun getTime():List<String> {
        val data = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentTime =  LocalTime.now()
            data.add("${currentTime.hour}:${currentTime.minute}")
        }
        return data
    }


    private val diffDaysPreset: Map<String, Int> = hashMapOf(
        "大前天" to -3,
        "前天" to -2,
        "昨天" to -1,
        "今天" to -0,
        "明天" to 1,
        "后天" to 2,
        "大后天" to 3,
    )

    private val daysGregorianFestivalPreset: Map<Int, Array<String>> = hashMapOf(
        // 公历节日
        101 to arrayOf("元旦"),
        110 to arrayOf("中国人民警察节"),
        202 to arrayOf("世界湿地日"),
        214 to arrayOf("情人节"),
        303 to arrayOf("全国爱耳日"),
        308 to arrayOf("妇女节"),
        312 to arrayOf("中国植树节"),
        315 to arrayOf("国际消费者权益日"),
        321 to arrayOf("世界睡眠日"),
        404 to arrayOf("清明节"),
        415 to arrayOf("全民国家安全教育日"),
        422 to arrayOf("世界地球日", "人民海军成立", "世界读书日"),
        423 to arrayOf("中国航天日"),
        501 to arrayOf("劳动节"),
        504 to arrayOf("青年节"),
        508 to arrayOf("世界微笑日"),
        512 to arrayOf("国际护士节", "全国防灾减灾日", "汶川地震周年祭"),
        518 to arrayOf("国际博物馆日"),
        522 to arrayOf("国际生物多样性日"),
        530 to arrayOf("全国科技工作者日"),
        531 to arrayOf("世界无烟日"),
        601 to arrayOf("儿童节"),
        605 to arrayOf("世界环境日"),
        606 to arrayOf("全国爱眼日"),
        607 to arrayOf("全国高考"),
        608 to arrayOf("世界海洋日"),
        610 to arrayOf("端午节"),
        614 to arrayOf("世界献血者日"),
        626 to arrayOf("国际禁毒日"),
        701 to arrayOf("中国共产党成立", "香港回归"),
        707 to arrayOf("七七事变纪念日"),
        728 to arrayOf("唐山大地震周年祭"),
        801 to arrayOf("建军节"),
        808 to arrayOf("全民健身日"),
        815 to arrayOf("全国生态日", "日本无条件投降"),
        819 to arrayOf("中国医师节"),
        903 to arrayOf("中国人民抗日战争暨世界反法西斯战争胜利"),
        910 to arrayOf("教师节"),
        918 to arrayOf("九一八事变"),
        920 to arrayOf("全国爱牙日"),
        921 to arrayOf("全民国防教育日"),
        922 to arrayOf("中国农民丰收节"),
        930 to arrayOf("烈士纪念日"),
        1001 to arrayOf("中华人民共和国成立", "国庆节"),
        1004 to arrayOf("世界动物日"),
        1010 to arrayOf("世界精神卫生日"),
        1016 to arrayOf("世界粮食日"),
        1025 to arrayOf("中国人民志愿军抗美援朝纪念日"),
        1108 to arrayOf("记者节"),
        1109 to arrayOf("全国消防日"),
        1201 to arrayOf("世界艾滋病日"),
        1202 to arrayOf("全国交通安全日"),
        1204 to arrayOf("国家宪法日"),
        1212 to arrayOf("西安事变"),
        1213 to arrayOf("南京大屠杀死难者国家公祭日"),
        1220 to arrayOf("澳门回归祖国"),
        1224 to arrayOf("长津湖战役胜利"),
        1225 to arrayOf("圣诞节"),
    )
}
