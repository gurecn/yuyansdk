package com.yuyan.inputmethod.util
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator

object LX17PinYinUtils {
    private val lx17KeyMap = mapOf(
        'b' to "B",
        'd' to "D",
        'u' to "D",
        'c' to "F",
        'f' to "F",
        'g' to "G",
        'h' to "H",
        'a' to "H",
        'p' to "H",
        'i' to "J",
        'j' to "J",
        'k' to "J",
        'l' to "L",
        'm' to "M",
        's' to "M",
        'n' to "N",
        'r' to "N",
        'q' to "Q",
        't' to "T",
        'w' to "W",
        'z' to "W",
        'e' to "W",
        'x' to "X",
        'o' to "X",
        'v' to "X",
        'y' to "Y",
    )

    private val lx17PinyinMap = HashMap<String, String>(225)

    init {
        lx17PinyinMap.put("H", "h,p")
        lx17PinyinMap.put("S", "sh")
        lx17PinyinMap.put("Z", "zh")
        lx17PinyinMap.put("B", "b")
        lx17PinyinMap.put("X", "x")
        lx17PinyinMap.put("M", "m,s")
        lx17PinyinMap.put("L", "l")
        lx17PinyinMap.put("D", "d")
        lx17PinyinMap.put("Y", "y")
        lx17PinyinMap.put("W", "w,z")
        lx17PinyinMap.put("J", "j,k")
        lx17PinyinMap.put("N", "n,r")
        lx17PinyinMap.put("C", "ch")
        lx17PinyinMap.put("Q", "q,Q")
        lx17PinyinMap.put("G", "g")
        lx17PinyinMap.put("F", "f,c")
        lx17PinyinMap.put("T", "t")

        lx17PinyinMap.put("HH", "ha,hua,pa")
        lx17PinyinMap.put("HS", "hen,pen,pin")
        lx17PinyinMap.put("HZ", "hang,pang,piao")
        lx17PinyinMap.put("HB", "hao,pao")
        lx17PinyinMap.put("HX", "huai,huan,po")
        lx17PinyinMap.put("HM", "pie,huo")
        lx17PinyinMap.put("HL", "hai,pai")
        lx17PinyinMap.put("HD", "hu,pu")
        lx17PinyinMap.put("HY", "heng,peng,ping")
        lx17PinyinMap.put("HW", "he")
        lx17PinyinMap.put("HJ", "pi")
        lx17PinyinMap.put("HN", "han,pan")
        lx17PinyinMap.put("HC", "hui")
        lx17PinyinMap.put("HQ", "huang,pian")
        lx17PinyinMap.put("HG", "hei,pei,hun")
        lx17PinyinMap.put("HF", "hiu,hou,pou")
        lx17PinyinMap.put("HT", "hong")

        lx17PinyinMap.put("SH", "shua,sha")
        lx17PinyinMap.put("SS", "shen")
        lx17PinyinMap.put("SZ", "shang")
        lx17PinyinMap.put("SB", "shao")
        lx17PinyinMap.put("SX", "shuai,shuan")
        lx17PinyinMap.put("SM", "shuo")
        lx17PinyinMap.put("SL", "shai")
        lx17PinyinMap.put("SD", "shu")
        lx17PinyinMap.put("SY", "sheng")
        lx17PinyinMap.put("SW", "she")
        lx17PinyinMap.put("SJ", "shi")
        lx17PinyinMap.put("SN", "shan")
        lx17PinyinMap.put("SC", "shui")
        lx17PinyinMap.put("SQ", "shuang")
        lx17PinyinMap.put("SG", "shun,shei")
        lx17PinyinMap.put("SF", "shou")

        lx17PinyinMap.put("ZH", "zha,zhua")
        lx17PinyinMap.put("ZS", "zhen")
        lx17PinyinMap.put("ZZ", "zhang")
        lx17PinyinMap.put("ZB", "zhao")
        lx17PinyinMap.put("ZX", "zhuai,zhuan")
        lx17PinyinMap.put("ZM", "zhuo")
        lx17PinyinMap.put("ZL", "zhai")
        lx17PinyinMap.put("ZD", "zhu")
        lx17PinyinMap.put("ZY", "zheng")
        lx17PinyinMap.put("ZW", "zhe")
        lx17PinyinMap.put("ZJ", "zhi")
        lx17PinyinMap.put("ZN", "zhan")
        lx17PinyinMap.put("ZC", "zhui")
        lx17PinyinMap.put("ZQ", "zhuang")
        lx17PinyinMap.put("ZG", "zhun")
        lx17PinyinMap.put("ZF", "zhou")
        lx17PinyinMap.put("ZT", "zhong")

        lx17PinyinMap.put("BH", "ba")
        lx17PinyinMap.put("BS", "ben,bin")
        lx17PinyinMap.put("BZ", "bang,biao")
        lx17PinyinMap.put("BB", "bao")
        lx17PinyinMap.put("BX", "bo")
        lx17PinyinMap.put("BM", "bie")
        lx17PinyinMap.put("BL", "bai")
        lx17PinyinMap.put("BD", "bu")
        lx17PinyinMap.put("BY", "beng,bing")
        lx17PinyinMap.put("BJ", "bi")
        lx17PinyinMap.put("BN", "ban")
        lx17PinyinMap.put("BC", "biang")
        lx17PinyinMap.put("BQ", "bian")
        lx17PinyinMap.put("BG", "bei")

        lx17PinyinMap.put("XH", "xia")
        lx17PinyinMap.put("XS", "xin")
        lx17PinyinMap.put("XZ", "xiao")
        lx17PinyinMap.put("XB", "xiong")
        lx17PinyinMap.put("XX", "xuan")
        lx17PinyinMap.put("XM", "xie")
        lx17PinyinMap.put("XL", "xue")
        lx17PinyinMap.put("XD", "xu")
        lx17PinyinMap.put("XY", "xing")
        lx17PinyinMap.put("XJ", "xi")
        lx17PinyinMap.put("XN", "xiang")
        lx17PinyinMap.put("XC", "xian")
        lx17PinyinMap.put("XQ", "xun")
        lx17PinyinMap.put("XG", "xun")
        lx17PinyinMap.put("XF", "xiu")

        lx17PinyinMap.put("MH", "ma,sa")
        lx17PinyinMap.put("MS", "men,sen,min")
        lx17PinyinMap.put("MZ", "mang,sang,miao")
        lx17PinyinMap.put("MB", "mao,sao")
        lx17PinyinMap.put("MX", "mo,suan")
        lx17PinyinMap.put("MM", "mie,muo,suo")
        lx17PinyinMap.put("ML", "mai,sai")
        lx17PinyinMap.put("MD", "mu,su")
        lx17PinyinMap.put("MY", "ming,meng,seng")
        lx17PinyinMap.put("MW", "se,me")
        lx17PinyinMap.put("MJ", "mi,si")
        lx17PinyinMap.put("MN", "man,san")
        lx17PinyinMap.put("MC", "sui")
        lx17PinyinMap.put("MQ", "mian")
        lx17PinyinMap.put("MG", "mei,sun")
        lx17PinyinMap.put("MF", "sui,mou,sou")
        lx17PinyinMap.put("MT", "song")

        lx17PinyinMap.put("LH", "la,lia")
        lx17PinyinMap.put("LS", "lin")
        lx17PinyinMap.put("LZ", "lang,liao")
        lx17PinyinMap.put("LB", "lao")
        lx17PinyinMap.put("LX", "luan,lv,lo")
        lx17PinyinMap.put("LM", "lie,luo")
        lx17PinyinMap.put("LL", "lai,lve")
        lx17PinyinMap.put("LD", "lu")
        lx17PinyinMap.put("LY", "leng,ling")
        lx17PinyinMap.put("LW", "le")
        lx17PinyinMap.put("LJ", "li")
        lx17PinyinMap.put("LN", "lan")
        lx17PinyinMap.put("LC", "liang")
        lx17PinyinMap.put("LQ", "lian")
        lx17PinyinMap.put("LG", "lei,lun")
        lx17PinyinMap.put("LF", "liu,lou")
        lx17PinyinMap.put("LT", "long")

        lx17PinyinMap.put("DH", "da")
        lx17PinyinMap.put("DS", "den")
        lx17PinyinMap.put("DZ", "dang,diao")
        lx17PinyinMap.put("DB", "dao")
        lx17PinyinMap.put("DX", "duan")
        lx17PinyinMap.put("DM", "die,duo")
        lx17PinyinMap.put("DL", "dai")
        lx17PinyinMap.put("DD", "du")
        lx17PinyinMap.put("DY", "deng,ding")
        lx17PinyinMap.put("DW", "de")
        lx17PinyinMap.put("DJ", "di")
        lx17PinyinMap.put("DN", "dan")
        lx17PinyinMap.put("DC", "dui")
        lx17PinyinMap.put("DQ", "duang,dian")
        lx17PinyinMap.put("DG", "dun")
        lx17PinyinMap.put("DF", "diu,dou")
        lx17PinyinMap.put("DT", "dong")

        lx17PinyinMap.put("YH", "ya")
        lx17PinyinMap.put("YS", "yin")
        lx17PinyinMap.put("YZ", "yang")
        lx17PinyinMap.put("YB", "yao")
        lx17PinyinMap.put("YX", "yuan")
        lx17PinyinMap.put("YL", "yue")
        lx17PinyinMap.put("YD", "yu")
        lx17PinyinMap.put("YY", "ying")
        lx17PinyinMap.put("YW", "ye")
        lx17PinyinMap.put("YJ", "yi")
        lx17PinyinMap.put("YN", "yan")
        lx17PinyinMap.put("YG", "yun")
        lx17PinyinMap.put("YF", "you")
        lx17PinyinMap.put("YT", "yong")

        lx17PinyinMap.put("WH", "wa,za")
        lx17PinyinMap.put("WS", "wen,zen")
        lx17PinyinMap.put("WZ", "wang,zang")
        lx17PinyinMap.put("WB", "zao")
        lx17PinyinMap.put("WX", "zuan,wo")
        lx17PinyinMap.put("WM", "zuo")
        lx17PinyinMap.put("WL", "zai,wai")
        lx17PinyinMap.put("WD", "zu,wu")
        lx17PinyinMap.put("WY", "zeng,weng")
        lx17PinyinMap.put("WW", "ze")
        lx17PinyinMap.put("WJ", "zi")
        lx17PinyinMap.put("WN", "zan,wan")
        lx17PinyinMap.put("WC", "zui")
        lx17PinyinMap.put("WG", "zei,wei,zun")
        lx17PinyinMap.put("WF", "zou")
        lx17PinyinMap.put("WT", "zong")

        lx17PinyinMap.put("JH", "jia,kua,ja,ka")
        lx17PinyinMap.put("JS", "ken,jin")
        lx17PinyinMap.put("JZ", "kang,jiao")
        lx17PinyinMap.put("JB", "jiong,kao")
        lx17PinyinMap.put("JX", "kuai,juan,kuan")
        lx17PinyinMap.put("JM", "jie,kuo")
        lx17PinyinMap.put("JL", "kai,jue")
        lx17PinyinMap.put("JD", "ju,ku")
        lx17PinyinMap.put("JY", "keng,jing")
        lx17PinyinMap.put("JW", "ke")
        lx17PinyinMap.put("JJ", "ji")
        lx17PinyinMap.put("JN", "kan")
        lx17PinyinMap.put("JC", "jiang,kui")
        lx17PinyinMap.put("JQ", "kuang,jian")
        lx17PinyinMap.put("JG", "jun,kun")
        lx17PinyinMap.put("JF", "jiu,kou")
        lx17PinyinMap.put("JT", "kong")

        lx17PinyinMap.put("NH", "na")
        lx17PinyinMap.put("NS", "nen,ren,nin")
        lx17PinyinMap.put("NZ", "nang,rang,niao")
        lx17PinyinMap.put("NB", "nao,rao")
        lx17PinyinMap.put("NX", "ruan,nuan,nv")
        lx17PinyinMap.put("NM", "nie,ruo,nuo")
        lx17PinyinMap.put("NL", "nai,nve")
        lx17PinyinMap.put("ND", "ru,nu")
        lx17PinyinMap.put("NY", "reng,neng,ning")
        lx17PinyinMap.put("NW", "ne,re")
        lx17PinyinMap.put("NJ", "ni,ri")
        lx17PinyinMap.put("NN", "nan,ran")
        lx17PinyinMap.put("NC", "niang,rui")
        lx17PinyinMap.put("NQ", "nian")
        lx17PinyinMap.put("NG", "nei,run")
        lx17PinyinMap.put("NF", "niu,rou")
        lx17PinyinMap.put("NT", "nong,rong")

        lx17PinyinMap.put("CH", "chua,cha")
        lx17PinyinMap.put("CS", "chen")
        lx17PinyinMap.put("CZ", "chang")
        lx17PinyinMap.put("CB", "chao")
        lx17PinyinMap.put("CX", "chuai,chuan")
        lx17PinyinMap.put("CM", "chuo")
        lx17PinyinMap.put("CL", "chai")
        lx17PinyinMap.put("CD", "chu")
        lx17PinyinMap.put("CY", "cheng")
        lx17PinyinMap.put("CW", "che")
        lx17PinyinMap.put("CJ", "chi")
        lx17PinyinMap.put("CN", "chan")
        lx17PinyinMap.put("CC", "chui")
        lx17PinyinMap.put("CQ", "chuang")
        lx17PinyinMap.put("CG", "chun")
        lx17PinyinMap.put("CF", "chou")
        lx17PinyinMap.put("CT", "chong")

        lx17PinyinMap.put("QH", "qia,Qa")
        lx17PinyinMap.put("QS", "qin,Qen")
        lx17PinyinMap.put("QZ", "qiao,Qang")
        lx17PinyinMap.put("QB", "qiong,Qao")
        lx17PinyinMap.put("QX", "quan,Qo")
        lx17PinyinMap.put("QM", "qie")
        lx17PinyinMap.put("QL", "que,Qai")
        lx17PinyinMap.put("QD", "qu")
        lx17PinyinMap.put("QY", "qing,Qeng")
        lx17PinyinMap.put("QG", "Qei")
        lx17PinyinMap.put("QW", "Qe")
        lx17PinyinMap.put("QJ", "qi")
        lx17PinyinMap.put("QN", "Qan")
        lx17PinyinMap.put("QC", "qiang")
        lx17PinyinMap.put("QQ", "qian")
        lx17PinyinMap.put("QG", "qun,Qei")
        lx17PinyinMap.put("QF", "qiu,Qou")
        lx17PinyinMap.put("QT", "Qer")

        lx17PinyinMap.put("GH", "gua,ga")
        lx17PinyinMap.put("GS", "gen")
        lx17PinyinMap.put("GZ", "gang")
        lx17PinyinMap.put("GB", "gao")
        lx17PinyinMap.put("GX", "guai,guan")
        lx17PinyinMap.put("GM", "guo")
        lx17PinyinMap.put("GL", "gai")
        lx17PinyinMap.put("GD", "gu")
        lx17PinyinMap.put("GY", "geng")
        lx17PinyinMap.put("GW", "ge")
        lx17PinyinMap.put("GN", "gan")
        lx17PinyinMap.put("GC", "gui")
        lx17PinyinMap.put("GQ", "guang")
        lx17PinyinMap.put("GG", "gei,gun")
        lx17PinyinMap.put("GF", "gou")
        lx17PinyinMap.put("GT", "gong")

        lx17PinyinMap.put("FH", "fa,ca")
        lx17PinyinMap.put("FS", "fen,cen")
        lx17PinyinMap.put("FZ", "fang,cang")
        lx17PinyinMap.put("FB", "cao")
        lx17PinyinMap.put("FX", "cuan,fo")
        lx17PinyinMap.put("FM", "fuo,cuo")
        lx17PinyinMap.put("FL", "cai")
        lx17PinyinMap.put("FD", "fu,cu")
        lx17PinyinMap.put("FY", "feng,ceng")
        lx17PinyinMap.put("FW", "ce")
        lx17PinyinMap.put("FJ", "ci")
        lx17PinyinMap.put("FN", "fan,can")
        lx17PinyinMap.put("FC", "cui")
        lx17PinyinMap.put("FG", "fei,cun")
        lx17PinyinMap.put("FF", "fou,cou")
        lx17PinyinMap.put("FT", "cong")

        lx17PinyinMap.put("TH", "ta")
        lx17PinyinMap.put("TZ", "tang,tiao")
        lx17PinyinMap.put("TB", "tao")
        lx17PinyinMap.put("TX", "tuan")
        lx17PinyinMap.put("TM", "tie,tuo")
        lx17PinyinMap.put("TL", "tai")
        lx17PinyinMap.put("TD", "tu")
        lx17PinyinMap.put("TY", "teng,ting")
        lx17PinyinMap.put("TW", "te")
        lx17PinyinMap.put("TJ", "ti")
        lx17PinyinMap.put("TN", "tan")
        lx17PinyinMap.put("TC", "tui")
        lx17PinyinMap.put("TQ", "tian")
        lx17PinyinMap.put("TG", "tun")
        lx17PinyinMap.put("TF", "tou")
        lx17PinyinMap.put("TT", "tong")
    }

    /**
     * 获取键码对应的拼音组合
     */
    fun lx17KeyToPinyin(sequence: String?): Array<String> {
        if (sequence.isNullOrEmpty()) {
            return emptyArray()
        }
        val sequenceString = if (sequence.length > 2) sequence.substring(0..1) else sequence
        val pinyin = ArrayList<String>(5)
        for (length in sequenceString.length downTo 1) {
            val prefix = sequenceString.substring(0, length)
            lx17PinyinMap[prefix]?.let { value ->
                pinyin.add(value)
            }
        }
        return pinyin.joinToString(",") { it }.split(",").toTypedArray()
    }


    /**
     * 获取拼音拼音对应的键码
     */
    fun pinyin2Key(sequence: String?): String {
        if (sequence.isNullOrEmpty()) {
            return ""
        }
        for ((key, value) in lx17PinyinMap) {
            val pinyinList = value.split(",")
            if (pinyinList.any { it == sequence }) {
                return key
            }
        }
        return ""
    }

    fun pinyin2Lx17Key(pinyin: Char): String = lx17KeyMap[pinyin]?:pinyin.toString()
}