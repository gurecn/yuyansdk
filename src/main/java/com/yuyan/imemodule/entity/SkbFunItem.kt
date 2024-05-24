package com.yuyan.imemodule.entity

import com.yuyan.imemodule.prefs.behavior.SkbMenuMode

class SkbFunItem(//名称
    val funName: String, //图片资源
    @JvmField
    val funImgRecource: Int,
    val skbMenuMode: SkbMenuMode
)
