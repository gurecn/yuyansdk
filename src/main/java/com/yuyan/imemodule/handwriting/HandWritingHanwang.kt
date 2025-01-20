package com.yuyan.imemodule.handwriting

import android.util.Base64
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.callback.IHandWritingCallBack
import com.yuyan.imemodule.network.NativeMethods
import com.yuyan.imemodule.libs.pinyin4j.PinyinHelper
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinCaseType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinToneType
import com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinVCharType
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.inputmethod.core.CandidateListItem
import org.json.JSONException
import org.json.JSONObject
import java.util.Collections

class HandWritingHanwang : HandWritingMonitor {
    private lateinit var mHanyuPinyinOutputFormat: com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat
    override fun initHdw(): Boolean {
        nativeMethods.nativeHttpInit(ImeSdkApplication.context, 0)
        mHanyuPinyinOutputFormat =
            com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinOutputFormat()
        mHanyuPinyinOutputFormat.caseType = com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinCaseType.LOWERCASE
        mHanyuPinyinOutputFormat.toneType = com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinToneType.WITH_TONE_MARK
        mHanyuPinyinOutputFormat.vCharType = com.yuyan.imemodule.libs.pinyin4j.format.HanyuPinyinVCharType.WITH_U_UNICODE
        return true
    }
    override fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack){
        nextDatas.add(Pair(strokes, recogResult))
        if(isRecognitionState) return
        isRecognitionState = true
        ThreadPoolUtils.executeSingleton {
            while (true) {
                if(nextDatas.size == 0) break
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
                            recogResultItems.add(CandidateListItem(com.yuyan.imemodule.libs.pinyin4j.PinyinHelper.toHanYuPinyin(candidate, mHanyuPinyinOutputFormat, "'").ifEmpty { candidate }, candidate))
                        }
                        recogResultData?.onSucess(recogResultItems.toTypedArray())
                    }
                } catch (ignored: JSONException) {
                }
            }
            isRecognitionState = false
        }
    }

    companion object {
        private var isRecognitionState = false
        private val nativeMethods = NativeMethods()
        private var  nextDatas  =  Collections.synchronizedList(mutableListOf<Pair<MutableList<Short?>, IHandWritingCallBack>?>())
    }
}
