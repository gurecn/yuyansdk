package com.yuyan.imemodule.handwriting

import android.util.Base64
import com.yuyan.imemodule.callback.IHandWritingCallBack
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
        strokes.add(-1)
        strokes.add(-1)
        val url = ""
        val request = JSONObject()
        request.put("uid", "0.0.0.0")
        request.put("lang", "chns")
        request.put("type", 1)
        request.put("data", strokes.joinToString(","))
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
