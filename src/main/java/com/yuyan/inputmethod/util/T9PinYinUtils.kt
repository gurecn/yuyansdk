package com.yuyan.inputmethod.util

import androidx.collection.SparseArrayCompat

object T9PinYinUtils {
    private val t9KeyMap = mapOf(
        'a' to "2",
        'b' to "2",
        'c' to "2",
        'd' to "3",
        'e' to "3",
        'f' to "3",
        'g' to "4",
        'h' to "4",
        'i' to "4",
        'j' to "5",
        'k' to "5",
        'l' to "5",
        'm' to "6",
        'n' to "6",
        'o' to "6",
        'p' to "7",
        'q' to "7",
        'r' to "7",
        's' to "7",
        't' to "8",
        'u' to "8",
        'v' to "8",
        'w' to "9",
        'x' to "9",
        'y' to "9",
        'z' to "9",
    )
    private val t9PinyinMap = SparseArrayCompat<Pair<String, Int>>(225)

    init {
        t9PinyinMap.append(2, "a,b,c" to 103106)
        t9PinyinMap.append(3, "d,e,f" to 59674)
        t9PinyinMap.append(4, "g,h,i" to 0)
        t9PinyinMap.append(5, "j,k,l" to 0)
        t9PinyinMap.append(6, "m,n,o" to 57964)
        t9PinyinMap.append(7, "p,q,r,s" to 0)
        t9PinyinMap.append(8, "t,u,v" to 0)
        t9PinyinMap.append(9, "w,x,y,z" to 0)
        t9PinyinMap.append(22, "ca,ba" to 573630)
        t9PinyinMap.append(23, "ce" to 7686)
        t9PinyinMap.append(24, "ci,ai,bi" to 228387)
        t9PinyinMap.append(26, "ao,bo,an" to 98075)
        t9PinyinMap.append(28, "cu,bu" to 627523)
        t9PinyinMap.append(32, "fa,da" to 332385)
        t9PinyinMap.append(33, "de" to 4946330)
        t9PinyinMap.append(34, "ei,di" to 179015)
        t9PinyinMap.append(36, "en,fo" to 199739)
        t9PinyinMap.append(37, "er" to 177171)
        t9PinyinMap.append(38, "fu,du" to 112700)
        t9PinyinMap.append(42, "ga,ha" to 65258)
        t9PinyinMap.append(43, "ge,he" to 1030393)
        t9PinyinMap.append(48, "gu,hu" to 67405)
        t9PinyinMap.append(52, "la,ka" to 163772)
        t9PinyinMap.append(53, "le,ke" to 1613940)
        t9PinyinMap.append(54, "li,ji" to 396856)
        t9PinyinMap.append(56, "lo" to 27403)
        t9PinyinMap.append(58, "lu,ku,ju,lv" to 131533)
        t9PinyinMap.append(62, "ma,na" to 677715)
        t9PinyinMap.append(63, "me,ne" to 352967)
        t9PinyinMap.append(64, "mi,ni" to 1017721)
        t9PinyinMap.append(66, "mo" to 26161)
        t9PinyinMap.append(68, "mu,ou,nu,nv" to 75675)
        t9PinyinMap.append(72, "sa,pa" to 50117)
        t9PinyinMap.append(73, "se,re" to 23642)
        t9PinyinMap.append(74, "si,ri,pi,qi" to 306801)
        t9PinyinMap.append(76, "po" to 12595)
        t9PinyinMap.append(78, "su,ru,qu,pu" to 463411)
        t9PinyinMap.append(82, "ta" to 612589)
        t9PinyinMap.append(83, "te" to 9732)
        t9PinyinMap.append(84, "ti" to 27618)
        t9PinyinMap.append(88, "tu" to 35310)
        t9PinyinMap.append(92, "za,wa,ya" to 154991)
        t9PinyinMap.append(93, "ze,ye" to 491659)
        t9PinyinMap.append(94, "zi,yi,xi" to 484079)
        t9PinyinMap.append(96, "yo,wo" to 1209760)
        t9PinyinMap.append(98, "zu,yu,xu,wu" to 502167)
        t9PinyinMap.append(224, "cai,bai" to 123325)
        t9PinyinMap.append(226, "cao,can,ban,bao" to 161555)
        t9PinyinMap.append(234, "bei" to 171338)
        t9PinyinMap.append(236, "cen,ben" to 32979)
        t9PinyinMap.append(242, "cha" to 41390)
        t9PinyinMap.append(243, "che,bie" to 80519)
        t9PinyinMap.append(244, "chi" to 78944)
        t9PinyinMap.append(246, "bin" to 4484)
        t9PinyinMap.append(248, "chu" to 88356)
        t9PinyinMap.append(264, "ang" to 1537)
        t9PinyinMap.append(268, "cou" to 1892)
        t9PinyinMap.append(284, "cui" to 6697)
        t9PinyinMap.append(286, "cuo,cun" to 44476)
        t9PinyinMap.append(324, "dai" to 64723)
        t9PinyinMap.append(326, "fan,dan,dao" to 528232)
        t9PinyinMap.append(334, "fei,dei" to 63640)
        t9PinyinMap.append(336, "den,fen" to 43977)
        t9PinyinMap.append(342, "dia" to 1341)
        t9PinyinMap.append(343, "die" to 14359)
        t9PinyinMap.append(348, "diu" to 5654)
        t9PinyinMap.append(364, "eng" to 231)
        t9PinyinMap.append(368, "dou,fou" to 414977)
        t9PinyinMap.append(384, "dui" to 293370)
        t9PinyinMap.append(386, "dun,duo" to 168668)
        t9PinyinMap.append(424, "gai,hai" to 381153)
        t9PinyinMap.append(426, "gao,gan,han,hao" to 645447)
        t9PinyinMap.append(434, "gei,hei" to 209099)
        t9PinyinMap.append(436, "gen,hen" to 250376)
        t9PinyinMap.append(468, "hou,gou" to 179075)
        t9PinyinMap.append(482, "gua,hua" to 128976)
        t9PinyinMap.append(484, "gui,hui" to 287717)
        t9PinyinMap.append(486, "guo,gun,hun,huo" to 266589)
        t9PinyinMap.append(524, "lai,kai" to 296237)
        t9PinyinMap.append(526, "lan,lao,kao,kan" to 371685)
        t9PinyinMap.append(534, "lei,kei" to 43362)
        t9PinyinMap.append(536, "ken" to 3932)
        t9PinyinMap.append(542, "lia,jia" to 136708)
        t9PinyinMap.append(543, "lie,jie" to 83194)
        t9PinyinMap.append(546, "jin,lin" to 107962)
        t9PinyinMap.append(548, "liu,jiu" to 632952)
        t9PinyinMap.append(568, "kou,lou" to 34189)
        t9PinyinMap.append(582, "kua" to 4457)
        t9PinyinMap.append(583, "lue,jue" to 11432)
        t9PinyinMap.append(584, "kui" to 7884)
        t9PinyinMap.append(586, "kuo,kun,lun,luo,jun" to 65958)
        t9PinyinMap.append(624, "mai,nai" to 153169)
        t9PinyinMap.append(626, "mao,man,nao,nan" to 119369)
        t9PinyinMap.append(634, "nei,mei" to 388494)
        t9PinyinMap.append(636, "men,nen" to 87151)
        t9PinyinMap.append(643, "mie,nie" to 20243)
        t9PinyinMap.append(646, "min,nin" to 53584)
        t9PinyinMap.append(648, "miu,niu" to 17191)
        t9PinyinMap.append(668, "nou,mou" to 14349)
        t9PinyinMap.append(683, "nue" to 803)
        t9PinyinMap.append(686, "nuo" to 3406)
        t9PinyinMap.append(724, "sai,pai" to 44254)
        t9PinyinMap.append(726, "sao,san,ran,rao,pan,pao" to 95375)
        t9PinyinMap.append(734, "pei" to 21641)
        t9PinyinMap.append(736, "ren,sen,pen" to 272291)
        t9PinyinMap.append(742, "sha,qia" to 79194)
        t9PinyinMap.append(743, "pie,she,qie" to 40383)
        t9PinyinMap.append(744, "shi" to 1480715)
        t9PinyinMap.append(746, "qin,pin" to 27571)
        t9PinyinMap.append(748, "shu,qiu" to 88502)
        t9PinyinMap.append(768, "rou,pou,sou" to 13101)
        t9PinyinMap.append(783, "que" to 49812)
        t9PinyinMap.append(784, "sui,rui" to 29169)
        t9PinyinMap.append(786, "ruo,suo,sun,qun,run" to 89687)
        t9PinyinMap.append(824, "tai" to 91265)
        t9PinyinMap.append(826, "tan,tao" to 45340)
        t9PinyinMap.append(834, "tei" to 425)
        t9PinyinMap.append(843, "tie" to 21798)
        t9PinyinMap.append(868, "tou" to 32502)
        t9PinyinMap.append(884, "tui" to 17350)
        t9PinyinMap.append(886, "tun,tuo" to 15360)
        t9PinyinMap.append(924, "zai,wai" to 1124161)
        t9PinyinMap.append(926, "zan,zao,yao,yan,wan" to 658316)
        t9PinyinMap.append(934, "zei,wei" to 286765)
        t9PinyinMap.append(936, "zen,wen" to 77226)
        t9PinyinMap.append(942, "xia,zha" to 186777)
        t9PinyinMap.append(943, "xie,zhe" to 386813)
        t9PinyinMap.append(944, "zhi" to 176907)
        t9PinyinMap.append(946, "yin,xin" to 164811)
        t9PinyinMap.append(948, "xiu,zhu" to 99481)
        t9PinyinMap.append(968, "zou,you" to 875253)
        t9PinyinMap.append(983, "xue,yue" to 148478)
        t9PinyinMap.append(984, "zui" to 75648)
        t9PinyinMap.append(986, "yun,xun,zuo,zun" to 265610)
        t9PinyinMap.append(2264, "bang,cang" to 46800)
        t9PinyinMap.append(2364, "beng,ceng" to 18281)
        t9PinyinMap.append(2424, "chai" to 4087)
        t9PinyinMap.append(2426, "bian,biao,chan,chao" to 111026)
        t9PinyinMap.append(2436, "chen" to 23258)
        t9PinyinMap.append(2464, "bing" to 100508)
        t9PinyinMap.append(2468, "chou" to 15425)
        t9PinyinMap.append(2482, "chua" to 2)
        t9PinyinMap.append(2484, "chui" to 7041)
        t9PinyinMap.append(2486, "chun,chuo" to 13342)
        t9PinyinMap.append(2664, "cong" to 129785)
        t9PinyinMap.append(2826, "cuan" to 1011)
        t9PinyinMap.append(3264, "dang,fang" to 120530)
        t9PinyinMap.append(3364, "deng,feng" to 232370)
        t9PinyinMap.append(3426, "dian,diao,fiao" to 192011)
        t9PinyinMap.append(3464, "ding" to 38766)
        t9PinyinMap.append(3664, "dong" to 37723)
        t9PinyinMap.append(3826, "duan" to 22205)
        t9PinyinMap.append(4264, "gang,hang" to 48741)
        t9PinyinMap.append(4364, "geng,heng" to 59052)
        t9PinyinMap.append(4664, "gong,hong" to 59936)
        t9PinyinMap.append(4824, "guai,huai" to 46106)
        t9PinyinMap.append(4826, "guan,huan" to 91526)
        t9PinyinMap.append(5264, "kang,lang" to 18795)
        t9PinyinMap.append(5364, "keng,leng" to 9190)
        t9PinyinMap.append(5426, "lian,liao,jian,jiao" to 289495)
        t9PinyinMap.append(5464, "jing,ling" to 87414)
        t9PinyinMap.append(5664, "kong,long" to 30831)
        t9PinyinMap.append(5824, "kuai" to 64956)
        t9PinyinMap.append(5826, "kuan,juan,luan" to 31510)
        t9PinyinMap.append(6264, "nang,mang" to 32230)
        t9PinyinMap.append(6364, "neng,meng" to 182259)
        t9PinyinMap.append(6426, "nian,niao,miao,mian" to 154235)
        t9PinyinMap.append(6464, "ning,ming" to 46369)
        t9PinyinMap.append(6664, "nong" to 30528)
        t9PinyinMap.append(6826, "nuan" to 1477)
        t9PinyinMap.append(7264, "pang,sang,rang" to 108780)
        t9PinyinMap.append(7364, "seng,peng,reng" to 22785)
        t9PinyinMap.append(7424, "shai" to 3426)
        t9PinyinMap.append(7426, "qiao,qian,pian,piao,shan,shao" to 245936)
        t9PinyinMap.append(7434, "shei" to 11979)
        t9PinyinMap.append(7436, "shen" to 39185)
        t9PinyinMap.append(7464, "ping,qing" to 108883)
        t9PinyinMap.append(7468, "shou" to 64526)
        t9PinyinMap.append(7482, "shua" to 19677)
        t9PinyinMap.append(7484, "shui" to 93697)
        t9PinyinMap.append(7486, "shun,shuo" to 344346)
        t9PinyinMap.append(7664, "song,rong" to 38291)
        t9PinyinMap.append(7826, "quan,suan,ruan" to 81042)
        t9PinyinMap.append(8264, "tang" to 22001)
        t9PinyinMap.append(8364, "teng" to 6968)
        t9PinyinMap.append(8426, "tian,tiao" to 89294)
        t9PinyinMap.append(8464, "ting" to 66023)
        t9PinyinMap.append(8664, "tong" to 48562)
        t9PinyinMap.append(8826, "tuan" to 7236)
        t9PinyinMap.append(9264, "wang,yang,zang" to 120682)
        t9PinyinMap.append(9364, "weng,zeng" to 10498)
        t9PinyinMap.append(9424, "zhai" to 6113)
        t9PinyinMap.append(9426, "zhao,zhan,xian,xiao" to 415751)
        t9PinyinMap.append(9434, "zhei" to 2551)
        t9PinyinMap.append(9436, "zhen" to 88434)
        t9PinyinMap.append(9464, "ying,xing" to 145821)
        t9PinyinMap.append(9468, "zhou" to 26844)
        t9PinyinMap.append(9482, "zhua" to 8986)
        t9PinyinMap.append(9484, "zhui" to 7034)
        t9PinyinMap.append(9486, "zhuo,zhun" to 11378)
        t9PinyinMap.append(9664, "zong,yong" to 209979)
        t9PinyinMap.append(9826, "zuan,xuan,yuan" to 111642)
        t9PinyinMap.append(24264, "chang" to 46143)
        t9PinyinMap.append(24364, "cheng" to 70224)
        t9PinyinMap.append(24664, "chong" to 19810)
        t9PinyinMap.append(24824, "chuai" to 873)
        t9PinyinMap.append(24826, "chuan" to 37961)
        t9PinyinMap.append(48264, "huang,guang" to 41376)
        t9PinyinMap.append(54264, "jiang,liang" to 210514)
        t9PinyinMap.append(54664, "jiong" to 3800)
        t9PinyinMap.append(58264, "kuang" to 10189)
        t9PinyinMap.append(64264, "niang" to 3446)
        t9PinyinMap.append(74264, "qiang,shang" to 337105)
        t9PinyinMap.append(74364, "sheng" to 59418)
        t9PinyinMap.append(74664, "qiong" to 5500)
        t9PinyinMap.append(74824, "shuai" to 10786)
        t9PinyinMap.append(74826, "shuan" to 1062)
        t9PinyinMap.append(94264, "zhang,xiang" to 343393)
        t9PinyinMap.append(94364, "zheng" to 50154)
        t9PinyinMap.append(94664, "xiong,zhong" to 290184)
        t9PinyinMap.append(94824, "zhuai" to 1156)
        t9PinyinMap.append(94826, "zhuan" to 32280)
        t9PinyinMap.append(248264, "chuang" to 9782)
        t9PinyinMap.append(748264, "shuang" to 21079)
        t9PinyinMap.append(948264, "zhuang" to 32931)
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

    fun pinyin2T9Key(pinyin: Char): String = t9KeyMap[pinyin]?:pinyin.toString()
}