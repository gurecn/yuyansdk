package com.yuyan.inputmethod.util

import androidx.collection.SparseArrayCompat

object T9PinYinUtils {
    private val t9KeyMap = mapOf(
        'a' to '2',
        'b' to '2',
        'c' to '2',
        'd' to '3',
        'e' to '3',
        'f' to '3',
        'g' to '4',
        'h' to '4',
        'i' to '4',
        'j' to '5',
        'k' to '5',
        'l' to '5',
        'm' to '6',
        'n' to '6',
        'o' to '6',
        'p' to '7',
        'q' to '7',
        'r' to '7',
        's' to '7',
        't' to '8',
        'u' to '8',
        'v' to '8',
        'w' to '9',
        'x' to '9',
        'y' to '9',
        'z' to '9',
    )
    private val t9PinyinMap = SparseArrayCompat<Pair<String, Int>>(225)

    init {
        t9PinyinMap.append(2, "a,b,c" to 1)
        t9PinyinMap.append(3, "e,d,f" to 1)
        t9PinyinMap.append(4, "g,h,i" to 1)
        t9PinyinMap.append(5, "j,k,l" to 1)
        t9PinyinMap.append(6, "o,m,n" to 1)
        t9PinyinMap.append(7, "p,q,r,s" to 1)
        t9PinyinMap.append(8, "t,u,v" to 1)
        t9PinyinMap.append(9, "w,x,y,z" to 1)
        t9PinyinMap.append(22, "ba,ca" to 2)
        t9PinyinMap.append(23, "ce" to 2)
        t9PinyinMap.append(24, "ai,bi,ci,ch" to 2)
        t9PinyinMap.append(26, "an,ao,bo" to 2)
        t9PinyinMap.append(28, "bu,cu" to 2)
        t9PinyinMap.append(32, "da,fa" to 2)
        t9PinyinMap.append(33, "de" to 2)
        t9PinyinMap.append(34, "di,ei" to 2)
        t9PinyinMap.append(36, "en,fo" to 2)
        t9PinyinMap.append(37, "er" to 2)
        t9PinyinMap.append(38, "du,fu" to 2)
        t9PinyinMap.append(42, "ga,ha" to 2)
        t9PinyinMap.append(43, "ge,he" to 2)
        t9PinyinMap.append(48, "gu,hu" to 2)
        t9PinyinMap.append(52, "ka,la" to 2)
        t9PinyinMap.append(53, "ke,le" to 2)
        t9PinyinMap.append(54, "ji,li" to 2)
        t9PinyinMap.append(56, "lo" to 2)
        t9PinyinMap.append(58, "ju,ku,lu,lv" to 2)
        t9PinyinMap.append(62, "ma,na" to 2)
        t9PinyinMap.append(63, "me,ne" to 2)
        t9PinyinMap.append(64, "mi,ni" to 2)
        t9PinyinMap.append(66, "mo" to 2)
        t9PinyinMap.append(68, "mu,nu,nv,ou" to 2)
        t9PinyinMap.append(72, "pa,sa" to 2)
        t9PinyinMap.append(73, "re,se" to 2)
        t9PinyinMap.append(74, "pi,qi,ri,si,sh" to 2)
        t9PinyinMap.append(76, "po" to 2)
        t9PinyinMap.append(78, "pu,qu,ru,su" to 2)
        t9PinyinMap.append(82, "ta" to 2)
        t9PinyinMap.append(83, "te" to 2)
        t9PinyinMap.append(84, "ti" to 2)
        t9PinyinMap.append(88, "tu" to 2)
        t9PinyinMap.append(92, "wa,ya,za" to 2)
        t9PinyinMap.append(93, "ye,ze" to 2)
        t9PinyinMap.append(94, "xi,yi,zi" to 2)
        t9PinyinMap.append(96, "wo,yo" to 2)
        t9PinyinMap.append(98, "wu,xu,yu,zu" to 2)
        t9PinyinMap.append(224, "bai,cai" to 3)
        t9PinyinMap.append(226, "ban,bao,can,cao" to 3)
        t9PinyinMap.append(234, "bei" to 3)
        t9PinyinMap.append(236, "ben,cen" to 3)
        t9PinyinMap.append(242, "cha" to 3)
        t9PinyinMap.append(243, "bie,che" to 3)
        t9PinyinMap.append(244, "chi" to 3)
        t9PinyinMap.append(246, "bin" to 3)
        t9PinyinMap.append(248, "chu" to 3)
        t9PinyinMap.append(264, "ang" to 3)
        t9PinyinMap.append(268, "cou" to 3)
        t9PinyinMap.append(284, "cui" to 3)
        t9PinyinMap.append(286, "cun,cuo" to 3)
        t9PinyinMap.append(324, "dai" to 3)
        t9PinyinMap.append(326, "dan,dao,fan" to 3)
        t9PinyinMap.append(334, "dei,fei" to 3)
        t9PinyinMap.append(336, "den,fen" to 3)
        t9PinyinMap.append(342, "dia" to 3)
        t9PinyinMap.append(343, "die" to 3)
        t9PinyinMap.append(348, "diu" to 3)
        t9PinyinMap.append(364, "eng" to 3)
        t9PinyinMap.append(368, "dou,fou" to 3)
        t9PinyinMap.append(384, "dui" to 3)
        t9PinyinMap.append(386, "dun,duo" to 3)
        t9PinyinMap.append(424, "gai,hai" to 3)
        t9PinyinMap.append(426, "gan,gao,han,hao" to 3)
        t9PinyinMap.append(434, "gei,hei" to 3)
        t9PinyinMap.append(436, "gen,hen" to 3)
        t9PinyinMap.append(468, "gou,hou" to 3)
        t9PinyinMap.append(482, "gua,hua" to 3)
        t9PinyinMap.append(484, "gui,hui" to 3)
        t9PinyinMap.append(486, "gun,guo,hun,huo" to 3)
        t9PinyinMap.append(524, "kai,lai" to 3)
        t9PinyinMap.append(526, "kan,kao,lan,lao" to 3)
        t9PinyinMap.append(534, "kei,lei" to 3)
        t9PinyinMap.append(536, "ken" to 3)
        t9PinyinMap.append(542, "jia,lia" to 3)
        t9PinyinMap.append(543, "jie,lie" to 3)
        t9PinyinMap.append(546, "jin,lin" to 3)
        t9PinyinMap.append(548, "jiu,liu" to 3)
        t9PinyinMap.append(568, "kou,lou" to 3)
        t9PinyinMap.append(582, "kua" to 3)
        t9PinyinMap.append(583, "jue,lue" to 3)
        t9PinyinMap.append(584, "kui" to 3)
        t9PinyinMap.append(586, "jun,kun,kuo,lun,luo" to 3)
        t9PinyinMap.append(624, "mai,nai" to 3)
        t9PinyinMap.append(626, "man,mao,nan,nao" to 3)
        t9PinyinMap.append(634, "mei,nei" to 3)
        t9PinyinMap.append(636, "men,nen" to 3)
        t9PinyinMap.append(643, "mie,nie" to 3)
        t9PinyinMap.append(646, "min,nin" to 3)
        t9PinyinMap.append(648, "miu,niu" to 3)
        t9PinyinMap.append(668, "mou,nou" to 3)
        t9PinyinMap.append(683, "nue" to 3)
        t9PinyinMap.append(686, "nuo" to 3)
        t9PinyinMap.append(724, "pai,sai" to 3)
        t9PinyinMap.append(726, "pan,pao,ran,rao,san,sao" to 3)
        t9PinyinMap.append(734, "pei" to 3)
        t9PinyinMap.append(736, "pen,ren,sen" to 3)
        t9PinyinMap.append(742, "qia,sha" to 3)
        t9PinyinMap.append(743, "pie,qie,she" to 3)
        t9PinyinMap.append(744, "shi" to 3)
        t9PinyinMap.append(746, "pin,qin" to 3)
        t9PinyinMap.append(748, "qiu,shu" to 3)
        t9PinyinMap.append(768, "pou,rou,sou" to 3)
        t9PinyinMap.append(783, "que" to 3)
        t9PinyinMap.append(784, "rui,sui" to 3)
        t9PinyinMap.append(786, "qun,run,ruo,sun,suo" to 3)
        t9PinyinMap.append(824, "tai" to 3)
        t9PinyinMap.append(826, "tan,tao" to 3)
        t9PinyinMap.append(834, "tei" to 3)
        t9PinyinMap.append(843, "tie" to 3)
        t9PinyinMap.append(868, "tou" to 3)
        t9PinyinMap.append(884, "tui" to 3)
        t9PinyinMap.append(886, "tun,tuo" to 3)
        t9PinyinMap.append(924, "wai,zai" to 3)
        t9PinyinMap.append(926, "wan,yan,yao,zan,zao" to 3)
        t9PinyinMap.append(934, "wei,zei" to 3)
        t9PinyinMap.append(936, "wen,zen" to 3)
        t9PinyinMap.append(942, "xia,zha" to 3)
        t9PinyinMap.append(943, "xie,zhe" to 3)
        t9PinyinMap.append(944, "zhi" to 3)
        t9PinyinMap.append(946, "xin,yin" to 3)
        t9PinyinMap.append(948, "xiu,zhu" to 3)
        t9PinyinMap.append(968, "you,zou" to 3)
        t9PinyinMap.append(983, "xue,yue" to 3)
        t9PinyinMap.append(984, "zui" to 3)
        t9PinyinMap.append(986, "xun,yun,zun,zuo" to 3)
        t9PinyinMap.append(2264, "bang,cang" to 4)
        t9PinyinMap.append(2364, "beng,ceng" to 4)
        t9PinyinMap.append(2424, "chai" to 4)
        t9PinyinMap.append(2426, "bian,biao,chan,chao" to 4)
        t9PinyinMap.append(2436, "chen" to 4)
        t9PinyinMap.append(2464, "bing" to 4)
        t9PinyinMap.append(2468, "chou" to 4)
        t9PinyinMap.append(2482, "chua" to 4)
        t9PinyinMap.append(2484, "chui" to 4)
        t9PinyinMap.append(2486, "chun,chuo" to 4)
        t9PinyinMap.append(2664, "cong" to 4)
        t9PinyinMap.append(2826, "cuan" to 4)
        t9PinyinMap.append(3264, "dang,fang" to 4)
        t9PinyinMap.append(3364, "deng,feng" to 4)
        t9PinyinMap.append(3426, "dian,diao,fiao" to 4)
        t9PinyinMap.append(3464, "ding" to 4)
        t9PinyinMap.append(3664, "dong" to 4)
        t9PinyinMap.append(3826, "duan" to 4)
        t9PinyinMap.append(4264, "gang,hang" to 4)
        t9PinyinMap.append(4364, "geng,heng" to 4)
        t9PinyinMap.append(4664, "gong,hong" to 4)
        t9PinyinMap.append(4824, "guai,huai" to 4)
        t9PinyinMap.append(4826, "guan,huan" to 4)
        t9PinyinMap.append(5264, "kang,lang" to 4)
        t9PinyinMap.append(5364, "keng,leng" to 4)
        t9PinyinMap.append(5426, "jian,jiao,lian,liao" to 4)
        t9PinyinMap.append(5464, "jing,ling" to 4)
        t9PinyinMap.append(5664, "kong,long" to 4)
        t9PinyinMap.append(5824, "kuai" to 4)
        t9PinyinMap.append(5826, "juan,kuan,luan" to 4)
        t9PinyinMap.append(6264, "mang,nang" to 4)
        t9PinyinMap.append(6364, "meng,neng" to 4)
        t9PinyinMap.append(6426, "mian,miao,nian,niao" to 4)
        t9PinyinMap.append(6464, "ming,ning" to 4)
        t9PinyinMap.append(6664, "nong" to 4)
        t9PinyinMap.append(6826, "nuan" to 4)
        t9PinyinMap.append(7264, "pang,rang,sang" to 4)
        t9PinyinMap.append(7364, "peng,reng,seng" to 4)
        t9PinyinMap.append(7424, "shai" to 4)
        t9PinyinMap.append(7426, "pian,piao,qian,qiao,shan,shao" to 4)
        t9PinyinMap.append(7434, "shei" to 4)
        t9PinyinMap.append(7436, "shen" to 4)
        t9PinyinMap.append(7464, "ping,qing" to 4)
        t9PinyinMap.append(7468, "shou" to 4)
        t9PinyinMap.append(7482, "shua" to 4)
        t9PinyinMap.append(7484, "shui" to 4)
        t9PinyinMap.append(7486, "shun,shuo" to 4)
        t9PinyinMap.append(7664, "rong,song" to 4)
        t9PinyinMap.append(7826, "quan,ruan,suan" to 4)
        t9PinyinMap.append(8264, "tang" to 4)
        t9PinyinMap.append(8364, "teng" to 4)
        t9PinyinMap.append(8426, "tian,tiao" to 4)
        t9PinyinMap.append(8464, "ting" to 4)
        t9PinyinMap.append(8664, "tong" to 4)
        t9PinyinMap.append(8826, "tuan" to 4)
        t9PinyinMap.append(9264, "wang,yang,zang" to 4)
        t9PinyinMap.append(9364, "weng,zeng" to 4)
        t9PinyinMap.append(9424, "zhai" to 4)
        t9PinyinMap.append(9426, "xian,xiao,zhan,zhao" to 4)
        t9PinyinMap.append(9434, "zhei" to 4)
        t9PinyinMap.append(9436, "zhen" to 4)
        t9PinyinMap.append(9464, "xing,ying" to 4)
        t9PinyinMap.append(9468, "zhou" to 4)
        t9PinyinMap.append(9482, "zhua" to 4)
        t9PinyinMap.append(9484, "zhui" to 4)
        t9PinyinMap.append(9486, "zhun,zhuo" to 4)
        t9PinyinMap.append(9664, "yong,zong" to 4)
        t9PinyinMap.append(9826, "xuan,yuan,zuan" to 4)
        t9PinyinMap.append(24264, "chang,biang" to 5)
        t9PinyinMap.append(24364, "cheng" to 5)
        t9PinyinMap.append(24664, "chong" to 5)
        t9PinyinMap.append(24824, "chuai" to 5)
        t9PinyinMap.append(24826, "chuan" to 5)
        t9PinyinMap.append(48264, "guang,huang" to 5)
        t9PinyinMap.append(54264, "jiang,liang" to 5)
        t9PinyinMap.append(54664, "jiong" to 5)
        t9PinyinMap.append(58264, "kuang" to 5)
        t9PinyinMap.append(64264, "niang" to 5)
        t9PinyinMap.append(74264, "qiang,shang" to 5)
        t9PinyinMap.append(74364, "sheng" to 5)
        t9PinyinMap.append(74664, "qiong" to 5)
        t9PinyinMap.append(74824, "shuai" to 5)
        t9PinyinMap.append(74826, "shuan" to 5)
        t9PinyinMap.append(94264, "xiang,zhang" to 5)
        t9PinyinMap.append(94364, "zheng" to 5)
        t9PinyinMap.append(94664, "xiong,zhong" to 5)
        t9PinyinMap.append(94824, "zhuai" to 5)
        t9PinyinMap.append(94826, "zhuan" to 5)
        t9PinyinMap.append(248264, "chuang" to 6)
        t9PinyinMap.append(748264, "shuang" to 6)
        t9PinyinMap.append(948264, "zhuang" to 6)
    }

    /**
     * 获取T9键码对应的拼音组合
     */
    fun t9KeyToPinyin(t9Sequence: String?): Array<String> {
        if (t9Sequence.isNullOrEmpty()) {
            return emptyArray()
        }
        val t9NumString = if (t9Sequence.length > 6) t9Sequence.substring(0, 6) else t9Sequence
        var t9Num = t9NumString.toIntOrNull() ?: return emptyArray()
        val pinyin = ArrayList<Pair<String, Int>>(6)
        while (t9Num > 0) {
            if (pinyin.isEmpty()) {
                // 完全匹配的权重最高
                t9PinyinMap[t9Num]?.let { pinyin.add(it.copy(second = Int.MAX_VALUE)) }
            } else {
                t9PinyinMap[t9Num]?.let { pinyin.add(it) }
            }
            t9Num /= 10
        }
        return pinyin.sortedByDescending { it.second }
            .joinToString(",") { it.first }
            .split(",").toTypedArray()
    }

    fun pinyin2T9Key(pinyin: Char): Char = t9KeyMap[pinyin]?:pinyin

    fun getT9Composition(composition: String, comment: String): String {
        if(comment.isEmpty())return composition
        val compositionList = composition.filter { it.code <= 0xFF }.split("'".toRegex())
        return buildSpannedString {
            append(composition.filter { it.code > 0xFF })
            comment.split("'").zip(compositionList).forEach { (pinyin, compo) ->
                append(if (compo.length >= pinyin.length) pinyin else pinyin.substring(0, compo.length))
                append("'")
            }
        }
    }
}