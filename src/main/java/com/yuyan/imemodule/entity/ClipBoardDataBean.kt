package com.yuyan.imemodule.entity

class ClipBoardDataBean {
    var copyContentId: String? = null
    var copyContent: String? = null
    var isKeep = false
    var copyTime = ""

    constructor(copyContentId: String?, copyContent: String?, isKeep: Boolean, copyTime: String = "") {
        this.copyContentId = copyContentId
        this.copyContent = copyContent
        this.isKeep = isKeep
        this.copyTime = copyTime
    }

}
