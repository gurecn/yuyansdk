package com.yuyan.imemodule.callback

interface ICandidate {
    // 返回新加载的数量
    fun loadMore(): Int
    fun getAllCandidates(): List<String?>
}