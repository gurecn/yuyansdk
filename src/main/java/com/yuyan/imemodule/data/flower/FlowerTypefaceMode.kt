package com.yuyan.imemodule.data.flower

import com.yuyan.imemodule.view.preference.ManagedPreference

enum class FlowerTypefaceMode {
    Mars, FlowerVine, Messy, Germinate, Fog, ProhibitAccess, Grass, Wind, Disabled;
   // 火星文, 花藤字, 凌乱字, 发芽字, 雾霾字, 禁止查阅， 长草字，起风了， 关闭;
    companion object : ManagedPreference.StringLikeCodec<FlowerTypefaceMode> {
        override fun decode(raw: String) = FlowerTypefaceMode.valueOf(raw)
    }
}