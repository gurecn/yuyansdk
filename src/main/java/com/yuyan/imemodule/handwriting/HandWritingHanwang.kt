package com.yuyan.imemodule.handwriting

import android.util.Base64
import com.yuyan.imemodule.callback.IHandWritingCallBack
import com.yuyan.imemodule.utils.LogUtil
import com.yuyan.imemodule.utils.thread.ThreadPoolUtils
import com.yuyan.inputmethod.core.CandidateListItem
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HandWritingHanwang : HandWritingMonitor {
    override fun initHdw(): Boolean {
        return true
    }

    override fun recognitionData(strokes: MutableList<Short?>, recogResult: IHandWritingCallBack){
        val url = ""
        val request = JSONObject()
        request.put("uid", "0.0.0.0")
        request.put("lang", "chns")  //中文简体：chns；中文繁体：chnt；英文：en ；数字：number；法语：fr；德语：de；意大利语：it； 日语：ja；韩语：kr；西班牙语：es；葡萄牙语：pt
        request.put("type", 1)  //请求类型区分：1. 手写轨迹识别2. 获取联想字
        request.put("data", strokes.joinToString(",") + "-1, -1")
        ThreadPoolUtils.executeSingleton { recognitionData(url, request.toString(), recogResult) }
    }

    override fun hciHwrRelease() {
        sInitState = false
    }

    companion object {
        private var sInitState = false
    }


    /**
     * 上传数据
     *
     * @param url      接口地址
     * @param recogResult 回调
     */
    private fun recognitionData(url: String?, dataStr: String, recogResult: IHandWritingCallBack) {
        var mConnection: HttpURLConnection? = null
        try {
            val bytes = dataStr.toByteArray()
            mConnection = URL(url).openConnection() as HttpURLConnection
            mConnection.setRequestMethod("POST")
            mConnection.setRequestProperty("Charset", "UTF-8")
            mConnection.setRequestProperty("Content-Type", "application/json")
            mConnection.setRequestProperty("Accept", "application/json")
            mConnection.setRequestProperty("Content-Length", bytes.size.toString())
            mConnection.setDoOutput(true)
            val outputStream = mConnection.outputStream
            outputStream.write(bytes)
            outputStream.flush()
            outputStream.close()
            val responseCode = mConnection.getResponseCode()
            if (responseCode == 200) {
                val buffer = StringBuilder()
                var readLine: String?
                val responseReader = BufferedReader(
                    InputStreamReader(
                        mConnection.inputStream
                    )
                )
                while (responseReader.readLine().also { readLine = it } != null) {
                    buffer.append(readLine).append("\n")
                }
                responseReader.close()
                val response = String(Base64.decode(buffer.toString(), Base64.DEFAULT))
                try {
                    val jsonObject1 = JSONObject(response)
                    val code = jsonObject1.optInt("code")
                    if (code == 0) {
                        val result = jsonObject1.optString("result")
                        val results = result.split(",0,".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        val recogResultItems = ArrayList<CandidateListItem?>()
                        for (can in results) {
                            val cans = can.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                            val sb = StringBuilder()
                            for (ca in cans) {
                                sb.append(ca.toInt().toChar())
                            }
                            recogResultItems.add(CandidateListItem("", sb.toString()))
                        }
                        recogResult.onSucess(recogResultItems)
                    }
                } catch (ignored: JSONException) {
                }
            }
        } catch (_: Exception) {
        } finally {
            mConnection?.disconnect()
        }
    }
}
