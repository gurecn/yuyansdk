package com.yuyan.inputmethod.util
object T9PinYinUtils {
    private val t9KeyMap = mapOf(
        'a' to 'A',
        'b' to 'A',
        'c' to 'A',
        'd' to 'D',
        'e' to 'D',
        'f' to 'D',
        'g' to 'G',
        'h' to 'G',
        'i' to 'G',
        'j' to 'J',
        'k' to 'J',
        'l' to 'J',
        'm' to 'M',
        'n' to 'M',
        'o' to 'M',
        'p' to 'P',
        'q' to 'P',
        'r' to 'P',
        's' to 'P',
        't' to 'T',
        'u' to 'T',
        'v' to 'T',
        'w' to 'W',
        'x' to 'W',
        'y' to 'W',
        'z' to 'W',
    )
    private val pinyinMap = HashMap<String, String>(225)

    init {
        pinyinMap.put("A", "a,b,c")
        pinyinMap.put("D", "e,d,f")
        pinyinMap.put("G", "g,h,i")
        pinyinMap.put("J", "j,k,l")
        pinyinMap.put("M", "o,m,n")
        pinyinMap.put("P", "p,q,r,s")
        pinyinMap.put("T", "t,u,v")
        pinyinMap.put("W", "w,x,y,z")
        pinyinMap.put("AA", "ba,ca")
        pinyinMap.put("AD", "ce")
        pinyinMap.put("AG", "ai,bi,ci,ch")
        pinyinMap.put("AM", "an,ao,bo")
        pinyinMap.put("AT", "bu,cu")
        pinyinMap.put("DA", "da,fa")
        pinyinMap.put("DD", "de")
        pinyinMap.put("DG", "di,ei")
        pinyinMap.put("DM", "en,fo")
        pinyinMap.put("DP", "er")
        pinyinMap.put("DT", "du,fu")
        pinyinMap.put("GA", "ga,ha")
        pinyinMap.put("GD", "ge,he")
        pinyinMap.put("GT", "gu,hu")
        pinyinMap.put("JA", "ka,la")
        pinyinMap.put("JD", "ke,le")
        pinyinMap.put("JG", "ji,li")
        pinyinMap.put("JM", "lo")
        pinyinMap.put("JT", "ju,ku,lu,lv")
        pinyinMap.put("MA", "ma,na")
        pinyinMap.put("MD", "me,ne")
        pinyinMap.put("MG", "mi,ni")
        pinyinMap.put("MM", "mo")
        pinyinMap.put("MT", "mu,nu,nv,ou")
        pinyinMap.put("PA", "pa,sa")
        pinyinMap.put("PD", "re,se")
        pinyinMap.put("PG", "pi,qi,ri,si,sh")
        pinyinMap.put("PM", "po")
        pinyinMap.put("PT", "pu,qu,ru,su")
        pinyinMap.put("TA", "ta")
        pinyinMap.put("TD", "te")
        pinyinMap.put("TG", "ti")
        pinyinMap.put("TT", "tu")
        pinyinMap.put("WA", "wa,ya,za")
        pinyinMap.put("WD", "ye,ze")
        pinyinMap.put("WG", "xi,yi,zi")
        pinyinMap.put("WM", "wo,yo")
        pinyinMap.put("WT", "wu,xu,yu,zu")
        pinyinMap.put("AAG", "bai,cai")
        pinyinMap.put("AAM", "ban,bao,can,cao")
        pinyinMap.put("ADG", "bei")
        pinyinMap.put("ADM", "ben,cen")
        pinyinMap.put("AGA", "cha")
        pinyinMap.put("AGD", "bie,che")
        pinyinMap.put("AGG", "chi")
        pinyinMap.put("AGM", "bin")
        pinyinMap.put("AGT", "chu")
        pinyinMap.put("AMG", "ang")
        pinyinMap.put("AMT", "cou")
        pinyinMap.put("ATG", "cui")
        pinyinMap.put("ATM", "cun,cuo")
        pinyinMap.put("DAG", "dai")
        pinyinMap.put("DAM", "dan,dao,fan")
        pinyinMap.put("DDG", "dei,fei")
        pinyinMap.put("DDM", "den,fen")
        pinyinMap.put("DGA", "dia")
        pinyinMap.put("DGD", "die")
        pinyinMap.put("DGT", "diu")
        pinyinMap.put("DMG", "eng")
        pinyinMap.put("DMT", "dou,fou")
        pinyinMap.put("DTG", "dui")
        pinyinMap.put("DTM", "dun,duo")
        pinyinMap.put("GAG", "gai,hai")
        pinyinMap.put("GAM", "gan,gao,han,hao")
        pinyinMap.put("GDG", "gei,hei")
        pinyinMap.put("GDM", "gen,hen")
        pinyinMap.put("GMT", "gou,hou")
        pinyinMap.put("GTA", "gua,hua")
        pinyinMap.put("GTG", "gui,hui")
        pinyinMap.put("GTM", "gun,guo,hun,huo")
        pinyinMap.put("JAG", "kai,lai")
        pinyinMap.put("JAM", "kan,kao,lan,lao")
        pinyinMap.put("JDG", "kei,lei")
        pinyinMap.put("JDM", "ken")
        pinyinMap.put("JGA", "jia,lia")
        pinyinMap.put("JGD", "jie,lie")
        pinyinMap.put("JGM", "jin,lin")
        pinyinMap.put("JGT", "jiu,liu")
        pinyinMap.put("JMT", "kou,lou")
        pinyinMap.put("JTA", "kua")
        pinyinMap.put("JTD", "jue,lue")
        pinyinMap.put("JTG", "kui")
        pinyinMap.put("JTM", "jun,kun,kuo,lun,luo")
        pinyinMap.put("MAG", "mai,nai")
        pinyinMap.put("MAM", "man,mao,nan,nao")
        pinyinMap.put("MDG", "mei,nei")
        pinyinMap.put("MDM", "men,nen")
        pinyinMap.put("MGD", "mie,nie")
        pinyinMap.put("MGM", "min,nin")
        pinyinMap.put("MGT", "miu,niu")
        pinyinMap.put("MMT", "mou,nou")
        pinyinMap.put("MTD", "nue")
        pinyinMap.put("MTM", "nuo")
        pinyinMap.put("PAG", "pai,sai")
        pinyinMap.put("PAM", "pan,pao,ran,rao,san,sao")
        pinyinMap.put("PDG", "pei")
        pinyinMap.put("PDM", "pen,ren,sen")
        pinyinMap.put("PGA", "qia,sha")
        pinyinMap.put("PGD", "pie,qie,she")
        pinyinMap.put("PGG", "shi")
        pinyinMap.put("PGM", "pin,qin")
        pinyinMap.put("PGT", "qiu,shu")
        pinyinMap.put("PMT", "pou,rou,sou")
        pinyinMap.put("PTD", "que")
        pinyinMap.put("PTG", "rui,sui")
        pinyinMap.put("PTM", "qun,run,ruo,sun,suo")
        pinyinMap.put("TAG", "tai")
        pinyinMap.put("TAM", "tan,tao")
        pinyinMap.put("TDG", "tei")
        pinyinMap.put("TGD", "tie")
        pinyinMap.put("TMT", "tou")
        pinyinMap.put("TTG", "tui")
        pinyinMap.put("TTM", "tun,tuo")
        pinyinMap.put("WAG", "wai,zai")
        pinyinMap.put("WAM", "wan,yan,yao,zan,zao")
        pinyinMap.put("WDG", "wei,zei")
        pinyinMap.put("WDM", "wen,zen")
        pinyinMap.put("WGA", "xia,zha")
        pinyinMap.put("WGD", "xie,zhe")
        pinyinMap.put("WGG", "zhi")
        pinyinMap.put("WGM", "xin,yin")
        pinyinMap.put("WGT", "xiu,zhu")
        pinyinMap.put("WMT", "you,zou")
        pinyinMap.put("WTD", "xue,yue")
        pinyinMap.put("WTG", "zui")
        pinyinMap.put("WTM", "xun,yun,zun,zuo")
        pinyinMap.put("AAMG", "bang,cang")
        pinyinMap.put("ADMG", "beng,ceng")
        pinyinMap.put("AGAG", "chai")
        pinyinMap.put("AGAM", "bian,biao,chan,chao")
        pinyinMap.put("AGDM", "chen")
        pinyinMap.put("AGMG", "bing")
        pinyinMap.put("AGMT", "chou")
        pinyinMap.put("AGTA", "chua")
        pinyinMap.put("AGTG", "chui")
        pinyinMap.put("AGTM", "chun,chuo")
        pinyinMap.put("AMMG", "cong")
        pinyinMap.put("ATAM", "cuan")
        pinyinMap.put("DAMG", "dang,fang")
        pinyinMap.put("DDMG", "deng,feng")
        pinyinMap.put("DGAM", "dian,diao,fiao")
        pinyinMap.put("DGMG", "ding")
        pinyinMap.put("DMMG", "dong")
        pinyinMap.put("DTAM", "duan")
        pinyinMap.put("GAMG", "gang,hang")
        pinyinMap.put("GDMG", "geng,heng")
        pinyinMap.put("GMMG", "gong,hong")
        pinyinMap.put("GTAG", "guai,huai")
        pinyinMap.put("GTAM", "guan,huan")
        pinyinMap.put("JAMG", "kang,lang")
        pinyinMap.put("JDMG", "keng,leng")
        pinyinMap.put("JGAM", "jian,jiao,lian,liao")
        pinyinMap.put("JGMG", "jing,ling")
        pinyinMap.put("JMMG", "kong,long")
        pinyinMap.put("JTAG", "kuai")
        pinyinMap.put("JTAM", "juan,kuan,luan")
        pinyinMap.put("MAMG", "mang,nang")
        pinyinMap.put("MDMG", "meng,neng")
        pinyinMap.put("MGAM", "mian,miao,nian,niao")
        pinyinMap.put("MGMG", "ming,ning")
        pinyinMap.put("MMMG", "nong")
        pinyinMap.put("MTAM", "nuan")
        pinyinMap.put("PAMG", "pang,rang,sang")
        pinyinMap.put("PDMG", "peng,reng,seng")
        pinyinMap.put("PGAG", "shai")
        pinyinMap.put("PGAM", "pian,piao,qian,qiao,shan,shao")
        pinyinMap.put("PGDG", "shei")
        pinyinMap.put("PGDM", "shen")
        pinyinMap.put("PGMG", "ping,qing")
        pinyinMap.put("PGMT", "shou")
        pinyinMap.put("PGTA", "shua")
        pinyinMap.put("PGTG", "shui")
        pinyinMap.put("PGTM", "shun,shuo")
        pinyinMap.put("PMMG", "rong,song")
        pinyinMap.put("PTAM", "quan,ruan,suan")
        pinyinMap.put("TAMG", "tang")
        pinyinMap.put("TDMG", "teng")
        pinyinMap.put("TGAM", "tian,tiao")
        pinyinMap.put("TGMG", "ting")
        pinyinMap.put("TMMG", "tong")
        pinyinMap.put("TTAM", "tuan")
        pinyinMap.put("WAMG", "wang,yang,zang")
        pinyinMap.put("WDMG", "weng,zeng")
        pinyinMap.put("WGAG", "zhai")
        pinyinMap.put("WGAM", "xian,xiao,zhan,zhao")
        pinyinMap.put("WGDG", "zhei")
        pinyinMap.put("WGDM", "zhen")
        pinyinMap.put("WGMG", "xing,ying")
        pinyinMap.put("WGMT", "zhou")
        pinyinMap.put("WGTA", "zhua")
        pinyinMap.put("WGTG", "zhui")
        pinyinMap.put("WGTM", "zhun,zhuo")
        pinyinMap.put("WMMG", "yong,zong")
        pinyinMap.put("WTAM", "xuan,yuan,zuan")
        pinyinMap.put("AGAMG", "chang,biang")
        pinyinMap.put("AGDMG", "cheng")
        pinyinMap.put("AGMMG", "chong")
        pinyinMap.put("AGTAG", "chuai")
        pinyinMap.put("AGTAM", "chuan")
        pinyinMap.put("GTAMG", "guang,huang")
        pinyinMap.put("JGAMG", "jiang,liang")
        pinyinMap.put("JGMMG", "jiong")
        pinyinMap.put("JTAMG", "kuang")
        pinyinMap.put("MGAMG", "niang")
        pinyinMap.put("PGAMG", "qiang,shang")
        pinyinMap.put("PGDMG", "sheng")
        pinyinMap.put("PGMMG", "qiong")
        pinyinMap.put("PGTAG", "shuai")
        pinyinMap.put("PGTAM", "shuan")
        pinyinMap.put("WGAMG", "xiang,zhang")
        pinyinMap.put("WGDMG", "zheng")
        pinyinMap.put("WGMMG", "xiong,zhong")
        pinyinMap.put("WGTAG", "zhuai")
        pinyinMap.put("WGTAM", "zhuan")
        pinyinMap.put("AGTAMG", "chuang")
        pinyinMap.put("PGTAMG", "shuang")
        pinyinMap.put("WGTAMG", "zhuang")
    }

    /**
     * 获取T9键码对应的拼音组合
     */
    fun t9KeyToPinyin(t9Sequence: String?): Array<String> {
        if (t9Sequence.isNullOrEmpty()) {
            return emptyArray()
        }
        val t9NumString = if (t9Sequence.length > 6) t9Sequence.substring(0, 6) else t9Sequence
        val pinyin = ArrayList<String>(5)
        for (length in t9NumString.length downTo 1) {
            val prefix = t9NumString.substring(0, length)
            pinyinMap[prefix]?.let { value ->
                pinyin.add(value)
            }
        }
        return pinyin.joinToString(",") { it }.split(",").toTypedArray()
    }


    /**
     * 获取拼音拼音对应的键码
     */
    fun pinyin2Key(sequence: String?): String {
        if (sequence.isNullOrEmpty()) return ""
        for ((key, value) in pinyinMap) {
            val pinyinList = value.split(",")
            if (pinyinList.any { it == sequence }) {
                return key
            }
        }
        return ""
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