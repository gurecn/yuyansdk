package com.yuyan.imemodule.entity

import com.yuyan.imemodule.prefs.behavior.SkbMenuMode

class SkbFunItem( val funName: String, @JvmField val funImgRecource: Int, val skbMenuMode: SkbMenuMode){
    override fun equals(other: Any?): Boolean {
        return when(other) {
            !is SkbFunItem -> false
            else -> this === other || this.skbMenuMode == other.skbMenuMode
        }
    }
}
