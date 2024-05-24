package com.hanvon.inputmethod.library

object Native {
    // 手写核心识别语言编号代码
    const val HW_RC_LANGUAGE_CN = 0x01

    // 手写核心识别模式编号代码
    const val HWRC_SINGLE = 0x01
    const val HWRC_SENTENCE = 0x02
    const val HWRC_OVERLAP = 0x03
    const val HWRC_SENTENCE_OVERLAP = 0x04

    // 手写核心识别范围
    ///////////////////////////////中文/////////////////////////////
    const val ALC_SC_COMMON = 0x00000001 // 简体一级字
    const val ALC_SC_RARE = 0x00000002 // 简体二级字
    const val ALC_SC_GBK34 = 0x00000004 // GBK34
    const val ALC_SC_GB18030EX = 0x00000008 /* GB18030Add */
    const val ALC_CS_CURSIVE = 0x00010000 /* 行草字 */
    const val ALC_CS_GB18030_Level1 = 0x10000000 /* GB18030-2022-level1Add */
    const val ALC_CS_GB18030_Level2 = 0x20000000 /* GB18030-2022-level2Add */

    //放在ALC_GBK中，识别连笔情况下字符
    const val ALC_NUMERIC = 0x00000100 /* 数字 0-9 ) */
    const val ALC_UCALPHA = 0x00000200 /* 大写字母( A-Z ) */
    const val ALC_LCALPHA = 0x00000400 /* 小写字母 ( a-z ) */
    const val ALC_PUNC_COMMON = 0x00000800 //!",:;?“”、。
    const val ALC_PUNC_RARE = 0x00001000 //()—…《》
    const val ALC_SYM_COMMON = 0x00002000 //#$%&*+-./<=>@€￡￥
    const val ALC_SYM_RARE = 0x00004000 //[\]^_`{|}~×√
    const val ALC_SC_RADICAL = 0x00200000 //偏旁部首
    const val ALC_PUN_SYM = ALC_PUNC_COMMON or ALC_PUNC_RARE or ALC_SYM_COMMON or ALC_SYM_RARE
    const val ALC_ALPHA = ALC_UCALPHA or ALC_LCALPHA

    //GB18030 > GBK > GB2312  中文简体推荐范围
    const val ALC_CHS_GB2312 =
        ALC_SC_COMMON or ALC_SC_RARE or ALC_NUMERIC or ALC_ALPHA or ALC_CS_CURSIVE or ALC_SC_RADICAL or ALC_PUN_SYM
    const val ALC_CHS_GBK = ALC_CHS_GB2312 or ALC_SC_GBK34
    const val ALC_CHS_GB18030 = ALC_CHS_GBK or ALC_SC_GB18030EX
    const val ALC_CHS_GB18030_L1 = ALC_CHS_GB18030 or ALC_CS_GB18030_Level1
    const val ALC_CHS_GB18030_L2 = ALC_CHS_GB18030_L1 or ALC_CS_GB18030_Level2

    init {
        System.loadLibrary("jni_hanvonime_handwriting")
    }

    /**
     * 初始化运算空间
     *
     *
     * 注意：1.这个方法的调用，不会重复申请内存，可以重复调用
     * 2.这个方法的调用会释放掉原来的字典，所以重复调用后需要重新设置字典，不然程序会崩溃重启才能正常使用
     */
    external fun nativeHwInitWorkspace()

    /**
     * 释放运算空间
     */
    external fun nativeHwReleaseWorkspace()

    /**
     * 设置识别模式
     *
     * @param mode 1:单字；2：中文短句；3：叠写；
     */
    external fun nativeHwSetMode(mode: Int)

    /**
     * 设置识别范围
     * 根据需求可多次设置识别范围，其中ALC_CHS_GB18030_L2为最大范围
     */
    external fun nativeHwSetRange(range: Int)

    /**
     * 设置倾斜笔迹识别功能
     * iSlant = 0，不开启倾斜识别；iSlant > 0，开启倾斜识别， 且iSlant为书写笔迹可旋转的范围；
     */
    external fun nativeHwSetSlantScope(iSlant: Int)

    /**
     * 设置字典和识别语言
     *
     * @param dic        字典路径
     * @param languageId 语言类型Id
     * @return 是否成功
     */
    external fun nativeHwSetDicAndLanguage(dic: String?, languageId: Int): Boolean

    /**
     * 识别笔迹
     *
     * @param stroke 笔迹数据数组
     * @return 识别结果数量
     */
    external fun nativeHwRecognize(stroke: ShortArray?): Int

    /**
     * 获取结果
     * @return 识别结果字符串
     */
    external fun nativeHwGetResult(): String?

    /**
     * 获取识别核心版本号
     */
    external fun nativeHwGetVersion(): String?

    /**
     * 获取字典版本号
     */
    external fun nativeHwGetDictVersion(): String?
}
