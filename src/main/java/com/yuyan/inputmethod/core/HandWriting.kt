package com.yuyan.inputmethod.core

import android.util.Base64
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.callback.IHandWritingCallBack
import com.yuyan.imemodule.libs.pinyin4j.PinyinHelper
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinCaseType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinToneType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinVCharType
import com.yuyan.imemodule.network.NativeMethods
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import org.json.JSONException
import org.json.JSONObject
import java.util.Collections

object HandWriting {
    private var isRecognitionState = false
    private val nativeMethods = NativeMethods()
    private var  nextDatas  =  Collections.synchronizedList(mutableListOf<Pair<MutableList<Short?>, IHandWritingCallBack>?>())
    private var mHanyuPinyinOutputFormat: HanyuPinyinOutputFormat

    init {
        nativeMethods.nativeHttpInit(Launcher.instance.context, 0)
        mHanyuPinyinOutputFormat = HanyuPinyinOutputFormat()
        mHanyuPinyinOutputFormat.caseType = HanyuPinyinCaseType.LOWERCASE
        mHanyuPinyinOutputFormat.toneType = HanyuPinyinToneType.WITH_TONE_MARK
        mHanyuPinyinOutputFormat.vCharType = HanyuPinyinVCharType.WITH_U_UNICODE
    }

    fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack){
        nextDatas.add(Pair(strokes, recogResult))
        if(isRecognitionState) return
        isRecognitionState = true
        ThreadPoolUtils.Companion.executeSingleton {
            while (true) {
                if(nextDatas.isEmpty()) break
                val data = nextDatas.removeAt(0)
                nextDatas.clear()
                val strokesData = data?.first?.toMutableList()
                val recogResultData = data?.second
                val request = JSONObject()
                request.put("uid", "0.0.0.0")
                request.put("lang", "chns")  //中文简体：chns；中文繁体：chnt；英文：en ；数字：number；法语：fr；德语：de；意大利语：it； 日语：ja；韩语：kr；西班牙语：es；葡萄牙语：pt
                request.put("type", 1)  //请求类型区分：1. 手写轨迹识别2. 获取联想字
                request.put("data", strokesData?.joinToString(",") + ",-1,-1")
                val responseData = nativeMethods.nativeHttpPost(request.toString())
                val response = String(Base64.decode(responseData, Base64.DEFAULT))
                try {
                    val jsonObject1 = JSONObject(response)
                    val code = jsonObject1.optInt("code")
                    if (code == 0) {
                        val result = jsonObject1.optString("result")
                        val results = result.split(",0,".toRegex()).dropLastWhile { it.isEmpty() }
                        val recogResultItems = ArrayList<CandidateListItem>()
                        for (can in results) {
                            val cans = can.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                            val sb = StringBuilder()
                            for (ca in cans) {
                                sb.append(Integer.parseInt(ca).toChar())
                            }
                            val candidate = sb.toString()
                            recogResultItems.add(CandidateListItem(PinyinHelper.toHanYuPinyin(candidate, mHanyuPinyinOutputFormat, "'").ifEmpty { candidate }, candidate))
                        }
                        recogResultData?.onSucess(recogResultItems.toTypedArray())
                    }
                } catch (_: JSONException) { }
            }
            isRecognitionState = false
        }
    }
}