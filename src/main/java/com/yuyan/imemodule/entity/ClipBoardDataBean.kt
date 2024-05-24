package com.yuyan.imemodule.entity

class ClipBoardDataBean {
    @JvmField
    var copyContentId: String? = null
    @JvmField
    var copyContent: String? = null
    @JvmField
    var isKeep = false
    @JvmField
    var copyExtends = ""

    constructor(
        copyContentId: String?,
        copyContent: String?,
        isKeep: Boolean,
        copyExtends: String
    ) {
        this.copyContentId = copyContentId
        this.copyContent = copyContent
        this.isKeep = isKeep
        this.copyExtends = copyExtends
    }

}
