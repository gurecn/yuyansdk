package com.yuyan.imemodule.data.emojicon

import com.yuyan.imemodule.R

/**
 * Emoji表情实体类
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */

object EmojiconData{
    val symbolData: Map<Int, List<String>> = linkedMapOf(
        R.drawable.icon_emojibar_recents to emptyList(),
        R.drawable.icon_symbol_chinese to listOf("，", "。", "！", "＠", "＃", "＄", "％", "＾", "＆", "＊", "−", "（", "）", "“", "”", "‘", "’", "＝", "＿", "｀", "：", "；", "？", "〜", "｜", "＋", "―", "＼", "／", "、", "．", "……", "《", "》", "＜", "＞", "ー", "・", "￥", "〒", "々", "仝", "〃", "ゝ", "ゞ", "ヽ", "ヾ", "１", "２", "３", "４", "５", "６", "７", "８", "９", "０",),
        R.drawable.icon_symbol_english to listOf(",", ".", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "'", "\"", "=", "_", "`", ":", ";", "?", "~", "|", "+", "-", "\\", "/", "[", "]", "{", "}", "<", ">", "·", "‘", "’", "¡", "¿", "¥", "€", "£", "¢", "©", "®", "™", "℃", "℉", "°", "§", "№", "†", "‡", "‥", "…", "※", "‾", "⁄", "‼", "⁇", "⁈", "⁉", "√", "π", "±", "×", "÷", "¶", "∆", "¤", "µ", "‹", "›", "«", "»",),
        R.drawable.icon_symbol_math to listOf("+", "-", "×", "÷", "±", "∓", "=", "≠", "∼", "≅", "<", ">", "≤", "≥", "√", "∛", "≈", "≡", "⁺", "⁻", "⊕", "⊗", "%", "‰", "∀", "∂", "∃", "∅", "∆", "∇", "∈", "∉", "∋", "∌", "∏", "∑", "ℵ", "∝", "∞", "∟", "∠", "∧", "∨", "∣", "∥", "∩", "∪", "∫", "∬", "∮", "∴", "∵", "∶", "∷", "⊂", "⊃", "⊄", "⊅", "⊆", "⊇", "∪", "∩", "ⁿ",),
        R.drawable.icon_symbol_parentheses to listOf("(", ")", "[", "]", "{", "}", "（", "）", "［", "］", "｛", "｝", "❨", "❩", "❲", "❳", "❴", "❵", "‘", "’", "“", "”", "❛", "❜", "❝", "❞", "<", ">", "〈", "〉", "《", "》", "〔", "〕", "【", "】", "〘", "〙", "「", "」", "『", "』", "︵", "︶", "︷", "︸", "︹", "︺", "︻", "︼", "︽", "︾", "︿", "﹀", "﹁", "﹂",),
        R.drawable.icon_symbol_sequence_2 to listOf("①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩", "⑴", "⑵", "⑶", "⑷", "⑸", "⑹", "⑺", "⑻", "⑼", "⑽", "❶", "❷", "❸", "❹", "❺", "❻", "❼", "❽", "❾", "❿", "㈠", "㈡", "㈢", "㈣", "㈤", "㈥", "㈦", "㈧", "㈨", "㈩", "Ⅰ", "Ⅱ", "Ⅲ", "Ⅳ", "Ⅴ", "Ⅵ", "Ⅶ", "Ⅷ", "Ⅸ", "Ⅹ", "Ⅺ", "Ⅻ", "Ⅼ", "Ⅽ", "Ⅾ", "Ⅿ", "ⅰ", "ⅱ", "ⅲ", "ⅳ", "ⅴ", "ⅵ", "ⅶ", "ⅷ", "ⅸ", "ⅹ", "ⅺ", "ⅻ", "ⅼ", "ⅽ", "ⅾ", "ⅿ", "¹", "²", "³", "⁴", "₁", "₂", "₃", "₄", "½", "⅓", "⅔", "㊣"),
        R.drawable.icon_symbol_special_2 to listOf("←", "↑", "→", "↓", "↔", "↕", "↖", "↗", "↘", "↙", "↚", "↛", "↜", "↝", "↞", "↟", "↠", "↡", "↢", "↣", "↤", "↥", "↦", "↧", "↨", "↩", "↪", "↫", "↬", "↭", "↮", "↯", "↰", "↱", "↲", "↳", "↴", "↵", "↶", "↷", "↸", "↹", "↺", "↻", "↼", "↽", "↾", "↿", "⇀", "⇁", "⇂", "⇃", "⇄", "⇅", "⇆", "⇇", "⇈", "⇉", "⇊", "⇋", "⇌", "⇍", "⇎", "⇏", "⇐", "⇑", "⇒", "⇓", "⇔", "⇕", "⇖", "⇗", "⇘", "⇙", "⇚", "⇛", "⇜", "⇝", "⇞", "⇟", "⇠", "⇡", "⇢", "⇣"),
        R.drawable.icon_symbol_special_1 to listOf("⚫", "⚪", "●", "○", "■", "□", "⬛", "⬜", "★", "☆", "◆", "◇", "▲", "△", "▶", "▷", "▼", "▽", "◀", "◁", "◐", "◑", "♀", "♂", "♤", "♡", "♧", "♢", "♠", "♥", "♣", "♦", "•", "❖", "✓", "✔", "✕", "✖", "✗", "✘", "✙", "✚", "✛", "✜", "✝", "✞", "✟", "✠", "✡", "✢", "✣", "✤", "✥", "✦", "✧", "✨", "✩", "✪", "✫", "✬", "✭", "✮", "✯", "✰", "✱", "✲", "✳", "✴", "✵", "✶", "✷", "✸", "✹", "✺", "✻", "✼", "✽", "✾", "✿", "❀", "❁", "❂", "❃", "❄", "❅", "❆", "❇", "❈", "❉", "❊", "❋", "❌", "❍", "❎", "❏", "❐", "❑", "❒", "◈", "◉", "◊", "○", "◢", "◣", "◤", "◥", "❢", "❣", "❤", "❥", "❦", "❧"),
        R.drawable.icon_symbol_greek to listOf("α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "π", "ρ", "ς", "σ", "τ", "υ", "φ", "χ", "ψ", "ω", "Α", "Β", "Γ", "Δ", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο", "Π", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω",),
        R.drawable.icon_symbol_pinyin to listOf("ā", "á", "ǎ", "à", "ō", "ó", "ǒ", "ò", "ê", "ē", "é", "ě", "è", "ī", "í", "ǐ", "ì", "ū", "ú", "ǔ", "ù", "ǖ", "ǘ", "ǚ", "ǜ", "ü"),
        R.drawable.icon_symbol_phonetic to listOf("ㄅ", "ㄆ", "ㄇ", "ㄈ", "ㄉ", "ㄊ", "ㄋ", "ㄌ", "ㄍ", "ㄎ", "ㄏ", "ㄐ", "ㄑ", "ㄒ", "ㄓ", "ㄔ", "ㄕ", "ㄖ", "ㄗ", "ㄘ", "ㄙ", "ㄚ", "ㄛ", "ㄜ", "ㄝ", "ㄞ", "ㄟ", "ㄠ", "ㄡ", "ㄢ", "ㄣ", "ㄤ", "ㄥ", "ㄦ", "ㄧ", "ㄨ", "ㄩ", "ㄪ", "ㄫ", "ㆠ", "ㆡ", "ㆢ", "ㆣ", "ㆤ", "ㆥ", "ㆦ", "ㆧ", "ㆨ", "ㆩ", "ㆪ", "ㆫ", "ㆬ", "ㆭ", "ㆮ", "ㆯ", "ㆰ", "ㆱ", "ㆲ", "ㆳ", "ㆴ", "ㆵ", "ㆶ", "ㆷ",),
        R.drawable.icon_symbol_japanese_hiragana to listOf( "ぁ", "あ", "ぃ", "い", "ぅ", "う", "ぇ", "え", "ぉ", "お", "か", "が", "き", "ぎ", "く", "ぐ", "け", "げ", "こ", "ご", "さ", "ざ", "し", "じ", "す", "ず", "せ", "ぜ", "そ", "ぞ", "た", "だ", "ち", "ぢ", "っ", "つ", "づ", "て", "で", "と", "ど", "な", "に", "ぬ", "ね", "の", "は", "ば", "ぱ", "ひ", "び", "ぴ", "ふ", "ぶ", "ぷ", "へ", "べ", "ぺ", "ほ", "ぼ", "ぽ", "ま", "み", "む", "め", "も", "ゃ", "や", "ゅ", "ゆ", "ょ", "よ", "ら", "り", "る", "れ", "ろ", "ゎ", "わ", "ゐ", "ゑ", "を", "ん", "ゔ", "ゕ", "ゖ", "゙", "゚", "゛", "゜", "ゝ", "ゞ", "ゟ",),
        R.drawable.icon_symbol_japanese_katakana to listOf("゠", "ァ", "ア", "ィ", "イ", "ゥ", "ウ", "ェ", "エ", "ォ", "オ", "カ", "ガ", "キ", "ギ", "ク", "グ", "ケ", "ゲ", "コ", "ゴ", "サ", "ザ", "シ", "ジ", "ス", "ズ", "セ", "ゼ", "ソ", "ゾ", "タ", "ダ", "チ", "ヂ", "ッ", "ツ", "ヅ", "テ", "デ", "ト", "ド", "ナ", "ニ", "ヌ", "ネ", "ノ", "ハ", "バ", "パ", "ヒ", "ビ", "ピ", "フ", "ブ", "プ", "ヘ", "ベ", "ペ", "ホ", "ボ", "ポ", "マ", "ミ", "ム", "メ", "モ", "ャ", "ヤ", "ュ", "ユ", "ョ", "ヨ", "ラ", "リ", "ル", "レ", "ロ", "ヮ", "ワ", "ヰ", "ヱ", "ヲ", "ン", "ヴ", "ヵ", "ヶ", "ヷ", "ヸ", "ヹ", "ヺ", "・", "ー", "ヽ", "ヾ", "ヿ", "ㇰ", "ㇱ", "ㇲ", "ㇳ", "ㇴ", "ㇵ", "ㇶ", "ㇷ", "ㇸ", "ㇹ", "ㇺ", "ㇻ", "ㇼ", "ㇽ", "ㇾ", "ㇿ",),
        R.drawable.icon_symbol_russian to listOf( "а", "о", "у", "ы", "э", "я", "ё", "ю", "и", "е", "б", "в", "г", "д", "ж", "з", "л", "м", "н", "р", "й", "п", "ф", "к", "т", "ш", "с", "х", "ц", "ч", "щ", "ъ", "ь", "А", "О", "У", "Ы", "Э", "Я", "Ё", "Ю", "П", "Е", "Б", "В", "Г", "Д", "Ж", "З", "Л", "М", "Н", "Р", "Й", "И", "Ф", "К", "Т", "Ш", "С", "Х", "Ц", "Ч", "Щ", "Ъ", "Ь",),
        R.drawable.icon_symbol_sequence_1 to listOf("囍", "卍", "卐", "㍿", "㊎", "㊍", "㊌", "㊋", "㊏", "㊚", "㊛", "㊐", "㊊", "㊣", "㊤", "㊥", "㊦", "㊧", "㊨", "㊒", "㊫", "㊑", "㊓", "㊔", "㊕", "㊖", "㊗", "㊘", "㊜", "㊝", "㊞", "㊟", "㊠", "㊡", "㊢", "㊩", "㊪", "㊬", "㊭", "㊮", "㊯", "㊰", "㊀", "㊁", "㊂", "㊃", "㊄", "㊅", "㊆", "㊇", "㊈", "㊉", "㋀", "㋁", "㋂", "㋃", "㋄", "㋅", "㋆", "㋇", "㋈", "㋉", "㋊", "㋋", "㏠", "㏡", "㏢", "㏣", "㏤", "㏥", "㏦", "㏧", "㏨", "㏩", "㏪", "㏫", "㏬", "㏭", "㏮", "㏯", "㏰", "㏱", "㏲", "㏳", "㏴", "㏵", "㏶", "㏷", "㏸", "㏹", "㏺", "㏻", "㏼", "㏽", "㏾", "㍙", "㍚", "㍛", "㍜", "㍝", "㍞", "㍟", "㍠", "㍡", "㍢", "㍣", "㍤", "㍥", "㍦", "㍧", "㍨", "㍩", "㍪", "㍫", "㍬", "㍭", "㍮", "㍯", "㍰", "㍘", "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾", "佰", "仟", "弍", "弎", "弐", "朤", "氺", "曱", "甴",),
    )

    val emoticonData: Map<Int, List<String>> = linkedMapOf(
        R.drawable.icon_emoticon_1 to listOf("^^;", "^_^;", "(^^ゞ", "(・・;", "(;_;", "(・・;)", "(*_*;", "(ーー;)", "(^o^;", "(゜o゜;", "(・。・;", "(^.^;", "(@@;)", "(¯―¯٥)", "(^_^;)", "(-_-;)", "('・ω・')", "(°ー°〃)", "(ーー゛)", "(~_~;)", "(；´Д｀)", "(^o^;)", "(；・∀・)", "(；´∀｀)", "(-.-;)", "(・_・;)", "(＠_＠;)", "（；^ω^）", "(；一_一)", "(_ _;)", "(^O^;)", "(~O~;)", "(・.・;)", "(=o=;)", "(-_-;)", "(~_~;)", "(-.-;)", "(；一_一)", "(~O~;)", "(=o=;)", "(´∀｀；)", "(´-﹏-`；)", "(ㆀ˘･з･˘)", "(_ _;)m", "(_ _;)m", "( •̀ㅁ•́;)", "(； ･`д･´)"),
        R.drawable.icon_emoticon_2 to listOf( ":-O", ":-|", "(゜゜)", "(@_@)", "(*_*)", "(+_+)", "(・o・)", "(゜o゜)", "(゜-゜)", "(?_?)", "(ﾟДﾟ)", "( ﾟдﾟ)", "＼(◎o◎)／", "(✽ ﾟдﾟ ✽)", "≡≡ﾍ( ´Д`)ﾉ"),
        R.drawable.icon_emoticon_3 to listOf("(TT)", "(__)", "(;_;", "(´Д⊂ヽ", "(T_T)", "(;_;)", "(._.)", "(ToT)", "(_ _)", "(/_;)", "(:_;)", "(;O;)", "(TдT)", "(TOT)", "(;_:)", "|￣|○", "(_ _;)", "(¯―¯٥)", "(__)m", "(｡ŏ﹏ŏ)", "(ヽ´ω`)", "( ；∀；)", "(･ัω･ั)", "(_^_)_", "(._.)_", "(_ _)m", "(*´﹃｀*)", "(´・ω・`)", "(´・ω・｀)", "(´；ω；｀)", "(´д⊂)‥ﾊｩ", "( ･ั﹏･ั)", "(つд⊂)ｴｰﾝ", "(_ _;)m", "( ´Д｀)=3", "(ﾉД`)ｼｸｼｸ", "(๑´0`๑)۶", "(ToT)/~~~", "(;_;)/~~~", "(T_T)/~~~", "(´；ω；｀)ｳｯ…", "｡ﾟ(ﾟ´Д｀ﾟ)ﾟ｡", "(´；ω；｀)ﾌﾞﾜｯ", "(๑´•.̫ • `๑)", "(´ . .̫ . `)", "｡･ﾟ･(ﾉД`)･ﾟ･｡", "｡･ﾟ･(ﾉ∀`)･ﾟ･｡", "(´°̥̥̥̥̥̥̥̥ω°̥̥̥̥̥̥̥̥｀)",),
        R.drawable.icon_emoticon_4 to listOf(":(", ":|", ":-(", ":-P", "=_=", "(--)", "(｀´）", "(-_-)", "(-.-)", "(=_=)", "(︶^︶)", "→_→", "←_←", "(~o~)", "(~_~)", "(~O~)", "(ー_ー)", "( 一一)", "(・へ・)", "(ーー゛)", "(－－〆)", "(#･∀･)", "(-_-;)", "(~_~;)", "(-.-;)", "(=_=;)", "(~O~;)", "(=o=;)", "(-_-メ)", "(~_~メ)", "(^_^メ)", "(ー_ー;)", "(；一_一)", "((+_+))", "(# ﾟДﾟ)", "( ´Д｀)=3", "(๑`^´๑)۶", "(๑òωó๑)۶",),
        R.drawable.icon_emoticon_5 to listOf("(⌒▽⌒)", "（￣▽￣）", "(=・ω・=)", "(｀・ω・´)", "(〜￣△￣)〜", "(･∀･)", "(°∀°)ﾉ", "(￣3￣)", "╮(￣▽￣)╭", "_(:3」∠)_", "( ´_ゝ｀)", "←_←", "→_→", "(<_<)", "(>_>)", "(;¬_¬)", "(\"▔□▔)/", "(ﾟДﾟ≡ﾟдﾟ)!?", "Σ(ﾟдﾟ;)", "Σ( ￣□￣||)", "(´；ω；`)", "（/TДT)/", "(^・ω・^ )", "(｡･ω･｡)", "(●￣(ｴ)￣●)", "ε=ε=(ノ≧∇≦)ノ", "(´･_･`)", "(-_-#)", "（￣へ￣）", "(￣ε(#￣) Σ", "ヽ(`Д´)ﾉ", "（#-_-)┯━┯", "(╯°口°)╯(┴—┴", "←◡←", "( ♥д♥)", "Σ>―(〃°ω°〃)♡→", "⁄(⁄ ⁄•⁄ω⁄•⁄ ⁄)⁄", "(╬ﾟдﾟ)▄︻┻┳═一", "･*･:≡(　ε:)", "¯\\_(ツ)_/¯"),
    )

    val emojiconData: Map<Int, List<String>>
    init {
        val emojiCompatInstance = YuyanEmojiCompat.getAsFlow().value
        val emojicons: Map<Int, List<String>> = linkedMapOf(
            R.drawable.icon_emojibar_recents to emptyList(),
            R.drawable.icon_emojibar_wechat to listOf("💥炸弹", "🎇烟花", "🎉庆祝", "💖高甜表白", "🤔光速连怼", "👍花式夸人", "🎂生日快乐", "🙂认真敷衍", "🌷衷心感谢", "❤️土味情话", "😋萌语攻击", "🐶单身宣言", "💣群聊轰炸", "🥰谢谢红包", "🧧跪求红包"),
            R.drawable.icon_emojibar_hot to listOf("🌝🌝", "🌚🌚", "🐂🍺", "🤙🤙🤙", "💖你", "🦀🦀", "😅😅😅", "嘿嘿🤤🤤🤤", "出🚪😷", "勤💧🤲", "🍐🎼", "👉👈", "🧡ྀི💛ྀི❤️ྀི", "🉑🉑💗💗", "🌹🌹🌹", "💗🐯🈶", "ᥬ🌝᭄", "ᥬ🌚᭄", "ᥬ😂᭄", "(   ˊ ᵕ ˋ 💦)", "🤺💨退‼️", "👍👍👍对对对", "8️⃣🅱️Q了", "🍚⚔️", "老🤙", "🧯Q", "☁️？", "🌪️我👄里", "🌞☀️🥵☀️🌞", "有🧊", "冒🍚了", "📰👀", "📰😋", "🐮🐴💃🕺", "⬆️👄", "⬇️👄", "❤️👄", "💘💘💘", "🌪️😭", "太✔️🌶️", "🛡️🦶", "❤️你1️⃣万年", "💖里有你", "😘😘😘", "单身🐶", "着🥝", "好🥥", "🍎啥", "完🥚", "🍐解", "烦💩了", "🌸心", "有🍅", "😁👍", "你🍓🍅吧", "真🍰😁", "🍑跑", "🍓事", "绝绝子🎶📢", "开💗💗", "吃🍑🍑", "吃🍉🍉", "吃🍋🍋", "吃🐳🐳", "🌬️🐮🐝", "🐚❎住了", "🈶🧊😅", "开炫🍊🍊🍊", "😙❤️", "🎤给你", "🎤🐈", "👌格局小了", "✊格局正好", "👐格局打开", "🖖格局裂开", "整挺好👍👍👍", "👏👏👏优秀", "🈶1️⃣说1️⃣", "🉑🈶🉑🈚", "1️⃣🕸️💗深", "🍻🍚了", "🤡😀🤡🤡", "😀🔍🤡", "🈯🈯👉👉", "玩🎠", "阿巴阿巴😦😦😦", "🕊️💊❗", "🌶️🐔", "♡💌♡", "🍌💚", "来💃💃🎊🕺🕺", "👂懂👏👏", "🈶👀🈚🐷", "🍑💨", "🈶内👻", "🦈🦅", "🌶️👀", "🍾+🍋+🌿+🍒=🍹", "👅🐶", "🍓🚪", "👶怕", "在🦌上", "👣❤️", "🐒颜🈚😁", "🕳️👨", "我💔开", "🕊️💋", "搞🥢点", "你在想🍑", "🈶🈚吃🐔", "🈶🈚王者", "🎣🐡🐠🐟🦞", "送你🌸🌸", "💗💗💗加速", "和我👩‍❤️‍👨8️⃣", "给👴爪巴", "🐂🍺🐃🍺🐄🍺", "给👴🏻整笑了", "关你💨事", "🐊❤️", "胖成🐷了", "我🍋了", "🉑以", "你是🐷吗", "🌈💨", "我拒绝❌", "👴❤️了", "🌨🌨🌨", "💰💴💵💶💷", "晚安💤🌙", "🐴 了", "🔒了", "吃💩", "我🤮了", "🙄🙃", "我👀到了", "我💥了", "🤤🍦", "傻🐷🐷", "🗝️您配吗", "大吃一🐳", "下🌧️了", "打🏀吗", "💯💯💯", "💪💪💪", "👌👌👌", "👋👋👋", "💢💢💢", "🙉🙉🙉", "🙈🙈🙈", "😒😒😒", "😆😆😆", "🥺🥺🥺", "✨((🌕⥎🌕))✨", "🌿((🌑⥎🌑))🌿", "🌹((❤️⥎❤️))🌹", "💐((🌺⥎🌺))🌹", "🌸((☀️⥎☀️))🌸", "🍃((🌳⥎🌳))🍃", "🍂((🍁⥎🍁))🍂", "☃️((❄️⥎❄️))☃️", "💥((🔥⥎🔥))💥", "💥((💣⥎💣))💥", "🚞🚃🚃🚃💨", "🌑🌒🌓🌔🌕🌖🌗🌘🌑",),
            R.drawable.icon_emojibar_smileys to listOf("😀", "😃", "😄", "😁", "😆", "😅", "🤣", "😂", "🙂", "🙃", "🫠", "😉", "😊", "😇", "🥰", "😍", "🤩", "😘", "😗", "☺️", "😚", "😙", "🥲", "😋", "😛", "😜", "🤪", "😝", "🤑", "🤗", "🤭", "🫢", "🫣", "🤫", "🤔", "🫡", "🤐", "🤨", "😐", "😑", "😶", "🫥", "😶‍🌫️", "😏", "😒", "🙄", "😬", "😮‍💨", "🤥", "🫨", "🙂‍↔️", "🙂‍↕️", "😌", "😔", "😪", "🤤", "😴", "😷", "🤒", "🤕", "🤢", "🤮", "🤧", "🥵", "🥶", "🥴", "😵", "😵‍💫", "🤯", "🤠", "🥳", "🥸", "😎", "🤓", "🧐", "😕", "🫤", "😟", "🙁", "☹️", "😮", "😯", "😲", "😳", "🥺", "🥹", "😦", "😧", "😨", "😰", "😥", "😢", "😭", "😱", "😖", "😣", "😞", "😓", "😩", "😫", "🥱", "😤", "😡", "😠", "🤬", "😈", "👿", "💀", "☠️", "💩", "🤡", "👹", "👺", "👻", "👽", "👾", "🤖", "😺", "😸", "😹", "😻", "😼", "😽", "🙀", "😿", "😾", "🙈", "🙉", "🙊", "💌", "💘", "💝", "💖", "💗", "💓", "💞", "💕", "💟", "❣️", "💔", "❤️‍🔥", "❤️‍🩹", "❤️", "🩷", "🧡", "💛", "💚", "💙", "🩵", "💜", "🤎", "🖤", "🩶", "🤍", "💋", "💯", "💢", "💥", "💫", "💦", "💨", "🕳️", "💬", "👁️‍🗨️", "🗨️", "🗯️", "💭", "💤",),
            R.drawable.icon_emojibar_people to listOf("👋", "🤚", "🖐️", "✋", "🖖", "🫱", "🫲", "🫳", "🫴", "🫷", "🫸", "👌", "🤌", "🤏", "✌️", "🤞", "🫰", "🤟", "🤘", "🤙", "👈", "👉", "👆", "🖕", "👇", "☝️", "🫵", "👍", "👎", "✊", "👊", "🤛", "🤜", "👏", "🙌", "🫶", "👐", "🤲", "🤝", "🙏", "✍️", "💅", "🤳", "💪", "🦾", "🦿", "🦵", "🦶", "👂", "🦻", "👃", "🧠", "🫀", "🫁", "🦷", "🦴", "👀", "👁️", "👅", "👄", "🫦", "👶", "🧒", "👦", "👧", "🧑", "👱", "👨", "🧔", "🧔‍♂️", "🧔‍♀️", "👨‍🦰", "👨‍🦱", "👨‍🦳", "👨‍🦲", "👩", "👩‍🦰", "🧑‍🦰", "👩‍🦱", "🧑‍🦱", "👩‍🦳", "🧑‍🦳", "👩‍🦲", "🧑‍🦲", "👱‍♀️", "👱‍♂️", "🧓", "👴", "👵", "🙍", "🙍‍♂️", "🙍‍♀️", "🙎", "🙎‍♂️", "🙎‍♀️", "🙅", "🙅‍♂️", "🙅‍♀️", "🙆", "🙆‍♂️", "🙆‍♀️", "💁", "💁‍♂️", "💁‍♀️", "🙋", "🙋‍♂️", "🙋‍♀️", "🧏", "🧏‍♂️", "🧏‍♀️", "🙇", "🙇‍♂️", "🙇‍♀️", "🤦", "🤦‍♂️", "🤦‍♀️", "🤷", "🤷‍♂️", "🤷‍♀️", "🧑‍⚕️", "👨‍⚕️", "👩‍⚕️", "🧑‍🎓", "👨‍🎓", "👩‍🎓", "🧑‍🏫", "👨‍🏫", "👩‍🏫", "🧑‍⚖️", "👨‍⚖️", "👩‍⚖️", "🧑‍🌾", "👨‍🌾", "👩‍🌾", "🧑‍🍳", "👨‍🍳", "👩‍🍳", "🧑‍🔧", "👨‍🔧", "👩‍🔧", "🧑‍🏭", "👨‍🏭", "👩‍🏭", "🧑‍💼", "👨‍💼", "👩‍💼", "🧑‍🔬", "👨‍🔬", "👩‍🔬", "🧑‍💻", "👨‍💻", "👩‍💻", "🧑‍🎤", "👨‍🎤", "👩‍🎤", "🧑‍🎨", "👨‍🎨", "👩‍🎨", "🧑‍✈️", "👨‍✈️", "👩‍✈️", "🧑‍🚀", "👨‍🚀", "👩‍🚀", "🧑‍🚒", "👨‍🚒", "👩‍🚒", "👮", "👮‍♂️", "👮‍♀️", "🕵️", "🕵️‍♂️", "🕵️‍♀️", "💂", "💂‍♂️", "💂‍♀️", "🥷", "👷", "👷‍♂️", "👷‍♀️", "🫅", "🤴", "👸", "👳", "👳‍♂️", "👳‍♀️", "👲", "🧕", "🤵", "🤵‍♂️", "🤵‍♀️", "👰", "👰‍♂️", "👰‍♀️", "🤰", "🫃", "🫄", "🤱", "👩‍🍼", "👨‍🍼", "🧑‍🍼", "👼", "🎅", "🤶", "🧑‍🎄", "🦸", "🦸‍♂️", "🦸‍♀️", "🦹", "🦹‍♂️", "🦹‍♀️", "🧙", "🧙‍♂️", "🧙‍♀️", "🧚", "🧚‍♂️", "🧚‍♀️", "🧛", "🧛‍♂️", "🧛‍♀️", "🧜", "🧜‍♂️", "🧜‍♀️", "🧝", "🧝‍♂️", "🧝‍♀️", "🧞", "🧞‍♂️", "🧞‍♀️", "🧟", "🧟‍♂️", "🧟‍♀️", "🧌", "💆", "💆‍♂️", "💆‍♀️", "💇", "💇‍♂️", "💇‍♀️", "🚶", "🚶‍♂️", "🚶‍♀️", "🚶‍➡️", "🚶‍♀️‍➡️", "🚶‍♂️‍➡️", "🧍", "🧍‍♂️", "🧍‍♀️", "🧎", "🧎‍♂️", "🧎‍♀️", "🧎‍➡️", "🧎‍♀️‍➡️", "🧎‍♂️‍➡️", "🧑‍🦯", "🧑‍🦯‍➡️", "👨‍🦯", "👨‍🦯‍➡️", "👩‍🦯", "👩‍🦯‍➡️", "🧑‍🦼", "🧑‍🦼‍➡️", "👨‍🦼", "👨‍🦼‍➡️", "👩‍🦼", "👩‍🦼‍➡️", "🧑‍🦽", "🧑‍🦽‍➡️", "👨‍🦽", "👨‍🦽‍➡️", "👩‍🦽", "👩‍🦽‍➡️", "🏃", "🏃‍♂️", "🏃‍♀️", "🏃‍➡️", "🏃‍♀️‍➡️", "🏃‍♂️‍➡️", "💃", "🕺", "🕴️", "👯", "👯‍♂️", "👯‍♀️", "🧖", "🧖‍♂️", "🧖‍♀️", "🧗", "🧗‍♂️", "🧗‍♀️", "🤺", "🏇", "⛷️", "🏂", "🏌️", "🏌️‍♂️", "🏌️‍♀️", "🏄", "🏄‍♂️", "🏄‍♀️", "🚣", "🚣‍♂️", "🚣‍♀️", "🏊", "🏊‍♂️", "🏊‍♀️", "⛹️", "⛹️‍♂️", "⛹️‍♀️", "🏋️", "🏋️‍♂️", "🏋️‍♀️", "🚴", "🚴‍♂️", "🚴‍♀️", "🚵", "🚵‍♂️", "🚵‍♀️", "🤸", "🤸‍♂️", "🤸‍♀️", "🤼", "🤼‍♂️", "🤼‍♀️", "🤽", "🤽‍♂️", "🤽‍♀️", "🤾", "🤾‍♂️", "🤾‍♀️", "🤹", "🤹‍♂️", "🤹‍♀️", "🧘", "🧘‍♂️", "🧘‍♀️", "🛀", "🛌", "🧑‍🤝‍🧑", "👭", "👫", "👬", "💏", "👩‍❤️‍💋‍👨", "👨‍❤️‍💋‍👨", "👩‍❤️‍💋‍👩", "💑", "👩‍❤️‍👨", "👨‍❤️‍👨", "👩‍❤️‍👩", "👨‍👩‍👦", "👨‍👩‍👧", "👨‍👩‍👧‍👦", "👨‍👩‍👦‍👦", "👨‍👩‍👧‍👧", "👨‍👨‍👦", "👨‍👨‍👧", "👨‍👨‍👧‍👦", "👨‍👨‍👦‍👦", "👨‍👨‍👧‍👧", "👩‍👩‍👦", "👩‍👩‍👧", "👩‍👩‍👧‍👦", "👩‍👩‍👦‍👦", "👩‍👩‍👧‍👧", "👨‍👦", "👨‍👦‍👦", "👨‍👧", "👨‍👧‍👦", "👨‍👧‍👧", "👩‍👦", "👩‍👦‍👦", "👩‍👧", "👩‍👧‍👦", "👩‍👧‍👧", "🗣️", "👤", "👥", "🫂", "👪", "🧑‍🧑‍🧒", "🧑‍🧑‍🧒‍🧒", "🧑‍🧒", "🧑‍🧒‍🧒", "👣",),
            R.drawable.icon_emojibar_nature to listOf("🐵", "🐒", "🦍", "🦧", "🐶", "🐕", "🦮", "🐕‍🦺", "🐩", "🐺", "🦊", "🦝", "🐱", "🐈", "🐈‍⬛", "🦁", "🐯", "🐅", "🐆", "🐴", "🫎", "🫏", "🐎", "🦄", "🦓", "🦌", "🦬", "🐮", "🐂", "🐃", "🐄", "🐷", "🐖", "🐗", "🐽", "🐏", "🐑", "🐐", "🐪", "🐫", "🦙", "🦒", "🐘", "🦣", "🦏", "🦛", "🐭", "🐁", "🐀", "🐹", "🐰", "🐇", "🐿️", "🦫", "🦔", "🦇", "🐻", "🐻‍❄️", "🐨", "🐼", "🦥", "🦦", "🦨", "🦘", "🦡", "🐾", "🦃", "🐔", "🐓", "🐣", "🐤", "🐥", "🐦", "🐧", "🕊️", "🦅", "🦆", "🦢", "🦉", "🦤", "🪶", "🦩", "🦚", "🦜", "🪽", "🐦‍⬛", "🪿", "🐦‍🔥", "🐸", "🐊", "🐢", "🦎", "🐍", "🐲", "🐉", "🦕", "🦖", "🐳", "🐋", "🐬", "🦭", "🐟", "🐠", "🐡", "🦈", "🐙", "🐚", "🪸", "🪼", "🐌", "🦋", "🐛", "🐜", "🐝", "🪲", "🐞", "🦗", "🪳", "🕷️", "🕸️", "🦂", "🦟", "🪰", "🪱", "🦠", "💐", "🌸", "💮", "🪷", "🏵️", "🌹", "🥀", "🌺", "🌻", "🌼", "🌷", "🪻", "🌱", "🪴", "🌲", "🌳", "🌴", "🌵", "🌾", "🌿", "☘️", "🍀", "🍁", "🍂", "🍃", "🪹", "🪺", "🍄",),
            R.drawable.icon_emojibar_food to listOf("🍇", "🍈", "🍉", "🍊", "🍋", "🍋‍🟩", "🍌", "🍍", "🥭", "🍎", "🍏", "🍐", "🍑", "🍒", "🍓", "🫐", "🥝", "🍅", "🫒", "🥥", "🥑", "🍆", "🥔", "🥕", "🌽", "🌶️", "🫑", "🥒", "🥬", "🥦", "🧄", "🧅", "🥜", "🫘", "🌰", "🫚", "🫛", "🍄‍🟫", "🍞", "🥐", "🥖", "🫓", "🥨", "🥯", "🥞", "🧇", "🧀", "🍖", "🍗", "🥩", "🥓", "🍔", "🍟", "🍕", "🌭", "🥪", "🌮", "🌯", "🫔", "🥙", "🧆", "🥚", "🍳", "🥘", "🍲", "🫕", "🥣", "🥗", "🍿", "🧈", "🧂", "🥫", "🍱", "🍘", "🍙", "🍚", "🍛", "🍜", "🍝", "🍠", "🍢", "🍣", "🍤", "🍥", "🥮", "🍡", "🥟", "🥠", "🥡", "🦀", "🦞", "🦐", "🦑", "🦪", "🍦", "🍧", "🍨", "🍩", "🍪", "🎂", "🍰", "🧁", "🥧", "🍫", "🍬", "🍭", "🍮", "🍯", "🍼", "🥛", "☕", "🫖", "🍵", "🍶", "🍾", "🍷", "🍸", "🍹", "🍺", "🍻", "🥂", "🥃", "🫗", "🥤", "🧋", "🧃", "🧉", "🧊", "🥢", "🍽️", "🍴", "🥄", "🔪", "🫙", "🏺",),
            R.drawable.icon_emojibar_car to listOf("🌍", "🌎", "🌏", "🌐", "🗺️", "🗾", "🧭", "🏔️", "⛰️", "🌋", "🗻", "🏕️", "🏖️", "🏜️", "🏝️", "🏞️", "🏟️", "🏛️", "🏗️", "🧱", "🪨", "🪵", "🛖", "🏘️", "🏚️", "🏠", "🏡", "🏢", "🏣", "🏤", "🏥", "🏦", "🏨", "🏩", "🏪", "🏫", "🏬", "🏭", "🏯", "🏰", "💒", "🗼", "🗽", "⛪", "🕌", "🛕", "🕍", "⛩️", "🕋", "⛲", "⛺", "🌁", "🌃", "🏙️", "🌄", "🌅", "🌆", "🌇", "🌉", "♨️", "🎠", "🛝", "🎡", "🎢", "💈", "🎪", "🚂", "🚃", "🚄", "🚅", "🚆", "🚇", "🚈", "🚉", "🚊", "🚝", "🚞", "🚋", "🚌", "🚍", "🚎", "🚐", "🚑", "🚒", "🚓", "🚔", "🚕", "🚖", "🚗", "🚘", "🚙", "🛻", "🚚", "🚛", "🚜", "🏎️", "🏍️", "🛵", "🦽", "🦼", "🛺", "🚲", "🛴", "🛹", "🛼", "🚏", "🛣️", "🛤️", "🛢️", "⛽", "🛞", "🚨", "🚥", "🚦", "🛑", "🚧", "⚓", "🛟", "⛵", "🛶", "🚤", "🛳️", "⛴️", "🛥️", "🚢", "✈️", "🛩️", "🛫", "🛬", "🪂", "💺", "🚁", "🚟", "🚠", "🚡", "🛰️", "🚀", "🛸", "🛎️", "🧳", "⌛", "⏳", "⌚", "⏰", "⏱️", "⏲️", "🕰️", "🕛", "🕧", "🕐", "🕜", "🕑", "🕝", "🕒", "🕞", "🕓", "🕟", "🕔", "🕠", "🕕", "🕡", "🕖", "🕢", "🕗", "🕣", "🕘", "🕤", "🕙", "🕥", "🕚", "🕦", "🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘", "🌙", "🌚", "🌛", "🌜", "🌡️", "☀️", "🌝", "🌞", "🪐", "⭐", "🌟", "🌠", "🌌", "☁️", "⛅", "⛈️", "🌤️", "🌥️", "🌦️", "🌧️", "🌨️", "🌩️", "🌪️", "🌫️", "🌬️", "🌀", "🌈", "🌂", "☂️", "☔", "⛱️", "⚡", "❄️", "☃️", "⛄", "☄️", "🔥", "💧", "🌊",),
            R.drawable.icon_emojibar_activity to listOf("🎃", "🎄", "🎆", "🎇", "🧨", "✨", "🎈", "🎉", "🎊", "🎋", "🎍", "🎎", "🎏", "🎐", "🎑", "🧧", "🎀", "🎁", "🎗️", "🎟️", "🎫", "🎖️", "🏆", "🏅", "🥇", "🥈", "🥉", "⚽", "⚾", "🥎", "🏀", "🏐", "🏈", "🏉", "🎾", "🥏", "🎳", "🏏", "🏑", "🏒", "🥍", "🏓", "🏸", "🥊", "🥋", "🥅", "⛳", "⛸️", "🎣", "🤿", "🎽", "🎿", "🛷", "🥌", "🎯", "🪀", "🪁", "🔫", "🎱", "🔮", "🪄", "🎮", "🕹️", "🎰", "🎲", "🧩", "🧸", "🪅", "🪩", "🪆", "♠️", "♥️", "♦️", "♣️", "♟️", "🃏", "🀄", "🎴", "🎭", "🖼️", "🎨", "🧵", "🪡", "🧶", "🪢",),
            R.drawable.icon_emojibar_objects to listOf("👓", "🕶️", "🥽", "🥼", "🦺", "👔", "👕", "👖", "🧣", "🧤", "🧥", "🧦", "👗", "👘", "🥻", "🩱", "🩲", "🩳", "👙", "👚", "🪭", "👛", "👜", "👝", "🛍️", "🎒", "🩴", "👞", "👟", "🥾", "🥿", "👠", "👡", "🩰", "👢", "🪮", "👑", "👒", "🎩", "🎓", "🧢", "🪖", "⛑️", "📿", "💄", "💍", "💎", "🔇", "🔈", "🔉", "🔊", "📢", "📣", "📯", "🔔", "🔕", "🎼", "🎵", "🎶", "🎙️", "🎚️", "🎛️", "🎤", "🎧", "📻", "🎷", "🪗", "🎸", "🎹", "🎺", "🎻", "🪕", "🥁", "🪘", "🪇", "🪈", "📱", "📲", "☎️", "📞", "📟", "📠", "🔋", "🪫", "🔌", "💻", "🖥️", "🖨️", "⌨️", "🖱️", "🖲️", "💽", "💾", "💿", "📀", "🧮", "🎥", "🎞️", "📽️", "🎬", "📺", "📷", "📸", "📹", "📼", "🔍", "🔎", "🕯️", "💡", "🔦", "🏮", "🪔", "📔", "📕", "📖", "📗", "📘", "📙", "📚", "📓", "📒", "📃", "📜", "📄", "📰", "🗞️", "📑", "🔖", "🏷️", "💰", "🪙", "💴", "💵", "💶", "💷", "💸", "💳", "🧾", "💹", "✉️", "📧", "📨", "📩", "📤", "📥", "📦", "📫", "📪", "📬", "📭", "📮", "🗳️", "✏️", "✒️", "🖋️", "🖊️", "🖌️", "🖍️", "📝", "💼", "📁", "📂", "🗂️", "📅", "📆", "🗒️", "🗓️", "📇", "📈", "📉", "📊", "📋", "📌", "📍", "📎", "🖇️", "📏", "📐", "✂️", "🗃️", "🗄️", "🗑️", "🔒", "🔓", "🔏", "🔐", "🔑", "🗝️", "🔨", "🪓", "⛏️", "⚒️", "🛠️", "🗡️", "⚔️", "💣", "🪃", "🏹", "🛡️", "🪚", "🔧", "🪛", "🔩", "⚙️", "🗜️", "⚖️", "🦯", "🔗", "⛓️‍💥", "⛓️", "🪝", "🧰", "🧲", "🪜", "⚗️", "🧪", "🧫", "🧬", "🔬", "🔭", "📡", "💉", "🩸", "💊", "🩹", "🩼", "🩺", "🩻", "🚪", "🛗", "🪞", "🪟", "🛏️", "🛋️", "🪑", "🚽", "🪠", "🚿", "🛁", "🪤", "🪒", "🧴", "🧷", "🧹", "🧺", "🧻", "🪣", "🧼", "🫧", "🪥", "🧽", "🧯", "🛒", "🚬", "⚰️", "🪦", "⚱️", "🧿", "🪬", "🗿", "🪧", "🪪",),
            R.drawable.icon_emojibar_symbols to listOf("🏧", "🚮", "🚰", "♿", "🚹", "🚺", "🚻", "🚼", "🚾", "🛂", "🛃", "🛄", "🛅", "⚠️", "🚸", "⛔", "🚫", "🚳", "🚭", "🚯", "🚱", "🚷", "📵", "🔞", "☢️", "☣️", "⬆️", "↗️", "➡️", "↘️", "⬇️", "↙️", "⬅️", "↖️", "↕️", "↔️", "↩️", "↪️", "⤴️", "⤵️", "🔃", "🔄", "🔙", "🔚", "🔛", "🔜", "🔝", "🛐", "⚛️", "🕉️", "✡️", "☸️", "☯️", "✝️", "☦️", "☪️", "☮️", "🕎", "🔯", "🪯", "♈", "♉", "♊", "♋", "♌", "♍", "♎", "♏", "♐", "♑", "♒", "♓", "⛎", "🔀", "🔁", "🔂", "▶️", "⏩", "⏭️", "⏯️", "◀️", "⏪", "⏮️", "🔼", "⏫", "🔽", "⏬", "⏸️", "⏹️", "⏺️", "⏏️", "🎦", "🔅", "🔆", "📶", "🛜", "📳", "📴", "♀️", "♂️", "⚧️", "✖️", "➕", "➖", "➗", "🟰", "♾️", "‼️", "⁉️", "❓", "❔", "❕", "❗", "〰️", "💱", "💲", "⚕️", "♻️", "⚜️", "🔱", "📛", "🔰", "⭕", "✅", "☑️", "✔️", "❌", "❎", "➰", "➿", "〽️", "✳️", "✴️", "❇️", "©️", "®️", "™️", "#️⃣", "*️⃣", "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", "🔟", "🔠", "🔡", "🔢", "🔣", "🔤", "🅰️", "🆎", "🅱️", "🆑", "🆒", "🆓", "ℹ️", "🆔", "Ⓜ️", "🆕", "🆖", "🅾️", "🆗", "🅿️", "🆘", "🆙", "🆚", "🈁", "🈂️", "🈷️", "🈶", "🈯", "🉐", "🈹", "🈚", "🈲", "🉑", "🈸", "🈴", "🈳", "㊗️", "㊙️", "🈺", "🈵", "🔴", "🟠", "🟡", "🟢", "🔵", "🟣", "🟤", "⚫", "⚪", "🟥", "🟧", "🟨", "🟩", "🟦", "🟪", "🟫", "⬛", "⬜", "◼️", "◻️", "◾", "◽", "▪️", "▫️", "🔶", "🔷", "🔸", "🔹", "🔺", "🔻", "💠", "🔘", "🔳", "🔲",),
            R.drawable.icon_emojibar_flags to listOf("🏁", "🚩", "🎌", "🏴", "🏳️", "🏳️‍🌈", "🏳️‍⚧️", "🏴‍☠️", "🇦🇨", "🇦🇩", "🇦🇪", "🇦🇫", "🇦🇬", "🇦🇮", "🇦🇱", "🇦🇲", "🇦🇴", "🇦🇶", "🇦🇷", "🇦🇸", "🇦🇹", "🇦🇺", "🇦🇼", "🇦🇽", "🇦🇿", "🇧🇦", "🇧🇧", "🇧🇩", "🇧🇪", "🇧🇫", "🇧🇬", "🇧🇭", "🇧🇮", "🇧🇯", "🇧🇱", "🇧🇲", "🇧🇳", "🇧🇴", "🇧🇶", "🇧🇷", "🇧🇸", "🇧🇹", "🇧🇻", "🇧🇼", "🇧🇾", "🇧🇿", "🇨🇦", "🇨🇨", "🇨🇩", "🇨🇫", "🇨🇬", "🇨🇭", "🇨🇮", "🇨🇰", "🇨🇱", "🇨🇲", "🇨🇳", "🇨🇴", "🇨🇵", "🇨🇷", "🇨🇺", "🇨🇻", "🇨🇼", "🇨🇽", "🇨🇾", "🇨🇿", "🇩🇪", "🇩🇬", "🇩🇯", "🇩🇰", "🇩🇲", "🇩🇴", "🇩🇿", "🇪🇦", "🇪🇨", "🇪🇪", "🇪🇬", "🇪🇭", "🇪🇷", "🇪🇸", "🇪🇹", "🇪🇺", "🇫🇮", "🇫🇯", "🇫🇰", "🇫🇲", "🇫🇴", "🇫🇷", "🇬🇦", "🇬🇧", "🇬🇩", "🇬🇪", "🇬🇫", "🇬🇬", "🇬🇭", "🇬🇮", "🇬🇱", "🇬🇲", "🇬🇳", "🇬🇵", "🇬🇶", "🇬🇷", "🇬🇸", "🇬🇹", "🇬🇺", "🇬🇼", "🇬🇾", "🇭🇰", "🇭🇲", "🇭🇳", "🇭🇷", "🇭🇹", "🇭🇺", "🇮🇨", "🇮🇩", "🇮🇪", "🇮🇱", "🇮🇲", "🇮🇳", "🇮🇴", "🇮🇶", "🇮🇷", "🇮🇸", "🇮🇹", "🇯🇪", "🇯🇲", "🇯🇴", "🇯🇵", "🇰🇪", "🇰🇬", "🇰🇭", "🇰🇮", "🇰🇲", "🇰🇳", "🇰🇵", "🇰🇷", "🇰🇼", "🇰🇾", "🇰🇿", "🇱🇦", "🇱🇧", "🇱🇨", "🇱🇮", "🇱🇰", "🇱🇷", "🇱🇸", "🇱🇹", "🇱🇺", "🇱🇻", "🇱🇾", "🇲🇦", "🇲🇨", "🇲🇩", "🇲🇪", "🇲🇫", "🇲🇬", "🇲🇭", "🇲🇰", "🇲🇱", "🇲🇲", "🇲🇳", "🇲🇴", "🇲🇵", "🇲🇶", "🇲🇷", "🇲🇸", "🇲🇹", "🇲🇺", "🇲🇻", "🇲🇼", "🇲🇽", "🇲🇾", "🇲🇿", "🇳🇦", "🇳🇨", "🇳🇪", "🇳🇫", "🇳🇬", "🇳🇮", "🇳🇱", "🇳🇴", "🇳🇵", "🇳🇷", "🇳🇺", "🇳🇿", "🇴🇲", "🇵🇦", "🇵🇪", "🇵🇫", "🇵🇬", "🇵🇭", "🇵🇰", "🇵🇱", "🇵🇲", "🇵🇳", "🇵🇷", "🇵🇸", "🇵🇹", "🇵🇼", "🇵🇾", "🇶🇦", "🇷🇪", "🇷🇴", "🇷🇸", "🇷🇺", "🇷🇼", "🇸🇦", "🇸🇧", "🇸🇨", "🇸🇩", "🇸🇪", "🇸🇬", "🇸🇭", "🇸🇮", "🇸🇯", "🇸🇰", "🇸🇱", "🇸🇲", "🇸🇳", "🇸🇴", "🇸🇷", "🇸🇸", "🇸🇹", "🇸🇻", "🇸🇽", "🇸🇾", "🇸🇿", "🇹🇦", "🇹🇨", "🇹🇩", "🇹🇫", "🇹🇬", "🇹🇭", "🇹🇯", "🇹🇰", "🇹🇱", "🇹🇲", "🇹🇳", "🇹🇴", "🇹🇷", "🇹🇹", "🇹🇻", "🇹🇿", "🇺🇦", "🇺🇬", "🇺🇲", "🇺🇳", "🇺🇸", "🇺🇾", "🇺🇿", "🇻🇦", "🇻🇨", "🇻🇪", "🇻🇬", "🇻🇮", "🇻🇳", "🇻🇺", "🇼🇫", "🇼🇸", "🇽🇰", "🇾🇪", "🇾🇹", "🇿🇦", "🇿🇲", "🇿🇼", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", "🏴󠁧󠁢󠁳󠁣󠁴󠁿", "🏴󠁧󠁢󠁷󠁬󠁳󠁿", ),
        )

        emojiconData = emojicons.mapValues { (icon, emojiList) ->
            when (icon) {
                R.drawable.icon_emojibar_smileys, R.drawable.icon_emojibar_people, R.drawable.icon_emojibar_nature, R.drawable.icon_emojibar_food,
                R.drawable.icon_emojibar_car, R.drawable.icon_emojibar_activity, R.drawable.icon_emojibar_objects, R.drawable.icon_emojibar_symbols,
                R.drawable.icon_emojibar_flags -> emojiList.filter { emoji -> YuyanEmojiCompat.getEmojiMatch(emojiCompatInstance, emoji) }
                else -> emojiList
            }
        }
    }

    val wechatEmojiconData: Map<String, List<Array<String>>> = linkedMapOf(
        "💥炸弹" to listOf(arrayOf("[炸弹]")),
        "🎇烟花" to listOf(arrayOf("[烟花]")),
        "🎉庆祝" to listOf(arrayOf("[庆祝]")),
        "💖高甜表白" to listOf(arrayOf("遇见你爱意汹涌💞\n看世间万物🌍\n都浪漫心动❤️", "[烟花]"), arrayOf("别担心，无论世界怎么运转🌍\n我们的轨迹始终一致🌹", "[庆祝]"), arrayOf("这世界太大了🌍\n我需要有个人和我是一伙的，你懂么☺️\n而那个人又必须得是你❤️", "[庆祝]"), arrayOf("耳机分你一只🎧\n开启心动模式💓", "[庆祝]"), arrayOf("饿了😔\n不知道吃什么🤦‍♀️\n想吃点爱情的苦💘", "[庆祝]"), arrayOf("有幸白头偕老绝不辜负今生温柔🌹", "[庆祝]"), arrayOf("一生很长\n要和有趣的人在一起\n比如我❤️", "[庆祝]"), arrayOf("你是我慢慢余生里斩钉截铁的梦想💞", "[庆祝]"), arrayOf("☀☻你很重要\n就是你很重\n我也要🥛", "[庆祝]"), arrayOf("你好我是米花你要爆爆米花吗🧨", "[庆祝]"), arrayOf("◠‿◠那就祝我们有说不尽的笑话和浪漫💞", "[庆祝]"), arrayOf("I'm waiting.什么? \n爱慕未停💞", "[庆祝]"), arrayOf("半途而废可不好🤭\n所以我会陪你到老💝", "[庆祝]"), arrayOf("我喜欢三月的风🍃\n六月的雨🌧️\n和任何时候的你🌝", "[庆祝]"), arrayOf("我对你没有偏袒的意思\n而是正儿八经的偏心。", "[庆祝]"), arrayOf("sin²a＋cos²a👩‍🏫\n始终如一💖", "[庆祝]"), arrayOf("️〰︎📀奇迹就留给别人吧\n我有你了💖", "[庆祝]"), arrayOf("💫比起互道晚安\n我更希望能帮你盖被子💞", "[庆祝]"), arrayOf("♡喜欢你呀\n像一颗牛奶糖从头甜到尾ℓσиɛʕ•̫͡•ʔ", "[庆祝]"), arrayOf("⛰уσυ ωιℓℓ αℓωαуѕ вє му ℓσνє\n му ℓσνє💖", "[庆祝]"), arrayOf("✧˖ꀿªᵖᵖᵞ𝚝𝚒𝚖𝚎 ²⁰²1°꒰๑'ꀾ'๑꒱°˖✧\n心仪男孩常驻身旁😘", "[庆祝]"), arrayOf("♥老来多健忘\n唯不忘相思🌟", "[庆祝]"), arrayOf("生活很苦🍒\n幸好你足够甜ʚ 💗ིྀ ɞ ", "[庆祝]"), arrayOf("🌈⋰ ᴵ ᴸᴼᵛᴱ ᵞᴼᵁ 🎎", "[庆祝]"), arrayOf("🌈与你有关\n皆是浪漫🌸", "[庆祝]"), arrayOf("你总能成为我热爱生活的原因🌙🌟💛", "[庆祝]"), arrayOf("我是想说\n我想你了💛", "[庆祝]"), arrayOf("你陪着我的时候😘\n我从没羡慕过任何人 💛", "[庆祝]")),
        "🤔光速连怼" to listOf( arrayOf("你瞅你跟个斑马脑袋似的头头是道🦓", "[炸弹]"), arrayOf("您抓周拿到的是键盘吗⌨️", "[炸弹]"), arrayOf("是金子总会发光✨\n你这个玻璃渣子总在反光🌚", "[炸弹]"), arrayOf("你不去当厨子可惜了👨‍🍳\n甩锅甩的那么厉害。🍳", "[炸弹]"), arrayOf("您上辈子是缝纫机吗😏\n这么会拉踩😊", "[炸弹]"), arrayOf("如果我惹你生气了🙁\n对不起，我不改🙂\n记住了吗？😌", "[炸弹]"), arrayOf("你讨厌我关我什么事啊🤔\n好像被你喜欢能升华人生似的😒", "[炸弹]"), arrayOf("最近记性不太好🤔\n忘记是不是给你脸了😉", "[炸弹]"), arrayOf("吃亏是福💥\n我祝你福如东海🌊", "[炸弹]"), arrayOf("少吃点盐看你闲的😉", "[炸弹]"), arrayOf("你的话，我连标点符号都不信。😌", "[炸弹]"), arrayOf("你的戏可以像你的钱一样少吗？🤫", "[炸弹]"), arrayOf("我也并非与生俱来就带刺🙂\n知道我不好就别碰。👿", "[炸弹]"), arrayOf("这么会抬杠\n工地需要你👷‍♂️", "[炸弹]"), arrayOf("有空一起去吃鱼吧🐟\n看你挺会挑刺的😂", "[炸弹]"), arrayOf("不是说贫穷抑制了我的想象力🙃\n是你的气质不配。🙉", "[炸弹]"), arrayOf("不管你圈子多大😕\n好好跟我说话。🤡", "[炸弹]"), arrayOf("不是看不起你\n是根本没看你👀", "[炸弹]"), arrayOf("你说的挺对的👏\n但我为什么要听你的🤔", "[炸弹]"), arrayOf("你去当演员吧👀\n我看你挺会演的👏", "[炸弹]")),
        "👍花式夸人" to listOf(arrayOf("女娲炫技作品🤩", "[烟花]"), arrayOf("知道恐龙为什么灭绝吗？🦖\n因为它们的前肢太短无法为你的美貌鼓掌👏\n于是它们难过得都死掉了😭", "[烟花]"), arrayOf("出道吧！！👨‍🎤\n我带着七大姑八大姨给你投票！！💪", "[烟花]"), arrayOf("在吗？😉\n出来挨夸👍", "[烟花]"), arrayOf("p图技术再练练吧😒\n根本没本人好看🤫", "[烟花]"), arrayOf("不知道为啥你要隔三差五发张自拍🤔\n我真的无语🤷‍♀️\n要发就天天发这是在拯救世界！！🤩", "[烟花]"), arrayOf("你的颜值好奇怪呀🤔\n一下子高⬆️\n一下子更高🔝", "[烟花]"), arrayOf("真是漂亮她妈给漂亮开门👏\n漂亮到家了👏", "[烟花]"), arrayOf("生活每天都让我灰头土脸🙁\n你却有能力让沙漠里开出蔷薇🌹", "[烟花]"), arrayOf("你的可爱🌸\n治愈一切不可爱🍎", "[烟花]"), arrayOf("你这张脸要是生在古代🧐\n定会引起诸侯战乱😱", "[烟花]"), arrayOf("此人只应天上有\n人间难得几回闻😊", "[烟花]"), arrayOf("真不愧是我天上地下绝无仅有的可爱小宝贝！❤️🧡💛", "[烟花]"), arrayOf("姐妹，稍微有点姿色行了\n倒不必美的如此满分💯", "[烟花]"), arrayOf("你是山间清冽的月🌙\n南风也不及温柔的你", "[烟花]"), arrayOf("上帝创造你的时候\n是不是糖罐子倒了🍬\n撒你身上了？", "[烟花]"), arrayOf("你能笑一下吗😊\n我的咖啡忘记加糖了🍬", "[烟花]"), arrayOf("千秋无绝色\n悦目是佳人\n倾国倾城貌\n惊为天下人！😘", "[烟花]"), arrayOf("你在我面前永远都闪闪发光✨\n就像整个宇宙的星光都洒在你身上🌠", "[烟花]"), arrayOf("第一次看到宇宙🌏\n是和你四目相对的时候❤️", "[烟花]"), arrayOf("我的心是旷野的鸟🕊️\n是在你的眼里找到了天空☁️", "[烟花]"), arrayOf("原本觉得困难的事😣\n只要看看你呀👀\n就觉得还能再坚持一下💪", "[烟花]"), arrayOf("因为有你\n连今天的落日都觉得可爱🌄", "[烟花]")),
        "🎂生日快乐" to listOf(arrayOf("🎂ʜᴀᴘᴘʏ ʙɪʀᴛʜᴅᴀʏ ᴛᴏ ʏᴏᴜ\n生日快乐🎉", "[庆祝]"), arrayOf("🍰➕🍓＝🎂祝生日快乐￼\n不止今天🍰永远有效￼🎀", "[烟花]"), arrayOf("🎀ʜᵃᵖᵖᵞ ʙⁱʳᵗʰᵈᵃᵞ ♪♬🎀\n🎂生日快乐 三岁小孩 ​😋 ​​​", "[庆祝]"), arrayOf("祝你生日快乐🎂\n不止生日🍻", "[烟花]"), arrayOf("生日快乐哦[蛋糕]\n祝你良辰吉日时时有🌠\n锦瑟年华岁岁拥👏", "[庆祝]"), arrayOf("生日快乐\n愿你有酒可以醉🍻\n愿你醒后有人陪[爱心]", "[烟花]"), arrayOf("生日快乐！🎂\n祝你生命中的愿望都能实现🎉", "[庆祝]"), arrayOf("✨♡ ʜᴀᴘᴘʏ ʙɪʀᴛʜᴅᴀʏ ᴛᴏ ᴍᴇ ◟̆◞̆ ♡\n我又长大一岁 生日快乐🎂ᵕ̈", "[烟花]"), arrayOf("承蒙时光不弃\n终究又长大了一岁🎂", "[庆祝]"), arrayOf("～按时长大啦🎊\n生日快乐🎉", "[烟花]"), arrayOf("🎂 祝自己生日快乐🎂", "[庆祝]"), arrayOf("🎂♡𝙃𝙖𝙥𝙥𝙮 𝘽𝙞𝙧𝙩𝙝𝙙𝙖𝙮 ♡🎂\n愿：眼里有光，兜里有钱，活得漂亮✨", "[烟花]"), arrayOf("✨♡ ʜᴀᴘᴘʏ ʙɪʀᴛʜᴅᴀʏ ᴛᴏ ᴍᴇ ◟̆◞̆ ♡\n˶⍤⃝˶ 不管几岁 快乐万岁🙌🏻", "[庆祝]"), arrayOf("⚗︎·̫⚗︎ | ʜᴀᴘᴘʏ ʙɪʀᴛʜᴅᴀʏ ᴛᴏ ᴜ\n美好的事物一定会在新的一岁如约而至💖", "[烟花]"), arrayOf("❤️ʜᴀ͟ᴘ͟ᴘ͟ʏ ᴇᴠᴇʀʏᴅᴀʏ̆̈\n一年一度吹蜡烛时间到[干杯]", "[庆祝]"), arrayOf("好的坏的都是风景\n祝自己生日快乐🍰", "[烟花]"), arrayOf("生日快乐\n星星是银河递给月亮的情书\n你是世界赠与我的恩赐🎉", "[庆祝]"), arrayOf("生日快乐[爱心]\n你可以只过生日[爱心]\n不用长大[爱心]", "[烟花]"), arrayOf("祝你年年皆胜意🎊\n岁岁都欢愉🎉", "[庆祝]")),
        "🙂认真敷衍" to listOf(arrayOf("您的小可爱正八百里加急赶往你的聊天界面🚶", "[庆祝]"), arrayOf("魔仙堡打盹中醒了回你😪", "[烟花]"), arrayOf("咕噜咕噜魔仙堡专线在为你接通☎️", "[庆祝]"), arrayOf("不要烦我噢 我在冒泡泡Oooooo💦", "[烟花]"), arrayOf("我不喜欢回消息☹️\n感觉我上辈子就是个免打扰🚫", "[庆祝]"), arrayOf("人工服务请按1📞", "[烟花]"), arrayOf("去宇宙摘星星啦✨\n马上回来", "[庆祝]"), arrayOf("你好 我是自动回复😬\n您的聊天对象暂时不在🙈", "[烟花]"), arrayOf("对方正尝试与您连接📞\n请稍等 当前进度1%", "[庆祝]"), arrayOf("您的消息已送达\n对方收到 就是不回🙈", "[烟花]"), arrayOf("请输入520次我爱你召唤本人💞", "[庆祝]"), arrayOf("不好意思，我现在有事不在😬\n一会儿没事也不会理你😬", "[烟花]"), arrayOf("是什么风把你吹来了\n是timi赢了吗？😂", "[庆祝]"), arrayOf("你要和我说话？你真的要和我说话？\n你确定自己想说吗？你一定非说不可吗？\n那你就说吧，这是自动回复。", "[烟花]"), arrayOf("此人已去外太空📡\n回来时会带星星和月亮给你哦。🌃", "[庆祝]"), arrayOf("不回我消息你就是个臭猪猪\n我不回你消息很正常\n你见哪个仙女不忙的😤", "[烟花]"), arrayOf("您好 发送一元红包自动解锁聊天模式[红包]", "[庆祝]"), arrayOf("您好 我现在不无聊\n希望无聊时您再找我🤝", "[烟花]"), arrayOf("这边因泄露了蟹堡王的祖传秘方🍔\n海洋监管局已将她抓获🌊\n待释放之时她会主动与您联系🌝", "[庆祝]"), arrayOf("嗯 你继续说 我听着呢😬", "[烟花]")),
        "🌷衷心感谢" to listOf(arrayOf("谢谢你，从来没让我羡慕过任何人💐", "[庆祝]"), arrayOf("谢谢已经无法表达我的感恩和敬意了💐", "[烟花]"), arrayOf("千言万语[爱心]\n感谢有你[爱心]", "[庆祝]"), arrayOf("谢谢❤️\n有你真好❤️", "[烟花]"), arrayOf("谢谢你🌸\n帮助我度过难关🤗", "[庆祝]"), arrayOf("谢谢\n非常感谢~☺️", "[烟花]"), arrayOf("谢谢❤️\n麻烦你了💐", "[庆祝]"), arrayOf("谢谢你[爱心]\n我会继续努力的~💪", "[烟花]"), arrayOf("谢谢你\n给我许多帮助💐", "[庆祝]"), arrayOf("今天真的是辛苦你啦\n谢谢🤝", "[烟花]"), arrayOf("谢谢你的帮助\n有你帮助我真是轻松多了❤️", "[庆祝]"), arrayOf("谢谢已经无法表达我的感恩和敬意了~🌷", "[烟花]"), arrayOf("我明白了\n谢谢你的帮助", "[庆祝]"), arrayOf("万分感谢[爱心]\n超级感谢[爱心]", "[烟花]")),
        "❤️土味情话" to listOf(arrayOf("你知道你和天上的星星有什么区别吗？🌟\n星星在天上，你在我心里！❤️", "[庆祝]"), arrayOf("🍟你这条路走了一遍就记得了\n🍔和你一起走过的路我都记得～", "[烟花]"), arrayOf("你是不是在我饮料里放了致幻剂？💊\n我好像看到了我们的美好未来😊", "[庆祝]"), arrayOf("你知道我最特别的地方是什么吗？\n特别喜欢你～[爱心]", "[烟花]"), arrayOf("今天风好大，你是乘风回去的吧？🍃\n因为我想你想疯了😭", "[庆祝]"), arrayOf("今天听说了一件很高兴的事🤭\n听说你想我了😊", "[烟花]"), arrayOf("我们来玩木头人不许动吧？😂\n我输了，因为我心动了❤️", "[庆祝]"), arrayOf("宝，外面风大雨大快回房子里吧\n什么房🏠，爱你的心房❤️", "[烟花]"), arrayOf("我想请假。\n请什么假？\n请你嫁给我～🌹", "[庆祝]"), arrayOf("我有超能力🦸\n我超喜欢你[爱心]", "[烟花]"), arrayOf("我玩捉迷藏每次都输😔\n为什么啊？\n因为爱你的心怎么也藏不住❤️", "[庆祝]"), arrayOf("你喜欢菜包还是肉包🤔\n我喜欢你这个小宝宝~[爱心]", "[烟花]"), arrayOf("你喜欢二郎神还是财神？\n我喜欢你的眼神😉", "[庆祝]"), arrayOf("我有个人生建议可以让你受益一生㊙️\n建议这辈子和我在一起～☺️", "[烟花]"), arrayOf("你知道喝什么酒最容易醉吗？🍻\n你和我的天长地久❤️", "[庆祝]"), arrayOf("我们去吃全家桶吧🍗\n这样我们就是一家人了👨‍👩‍👧", "[烟花]"), arrayOf("我觉得你特别像一款游戏🎮\n我的世界❤️", "[庆祝]"), arrayOf("不要抱怨😫\n抱我😘", "[烟花]")),
        "😋萌语攻击" to listOf(arrayOf("最近圆了许多😎\n我的可爱又多了一吨😋", "[庆祝]"), arrayOf("以梦为马🐎\n越骑越傻😑", "[烟花]"), arrayOf("摸摸小猪头🐷\n万事不用愁 •͈˽•͈😌", "[炸弹]"), arrayOf("肚子胖胖😎\n生活旺旺🎉", "[烟花]"), arrayOf("我的小熊说它真的很想见你🧸\n对了🙁\n我也是💝", "[庆祝]"), arrayOf("掐指一算觉得你想我了💞", "[烟花]"), arrayOf("我是图图小淘气😋\n面对世界很好奇👀", "[庆祝]"), arrayOf("我的手ฅฅ👰 \n你的手ლლ🤵", "[烟花]"), arrayOf("我的小愿望💕\n晚上不失眠💤\n卡上不缺钱💳", "[庆祝]"), arrayOf("智者不入爱河❤️\n可我是个糊涂蛋😏", "[烟花]"), arrayOf("生活圈娱乐圈朋友圈🎇\n爱的魔力转圈圈💞", "[庆祝]"), arrayOf("只要我把我挣的钱全给我爸😣\n那我就能成富二代了！😂", "[烟花]"), arrayOf("既然不来找我聊天那你也别想好过😡\n我叫你刷朋友圈都要看见我😠", "[庆祝]"), arrayOf("我有QQ糖，糖给你🍬\nQQ可以给我吗？🐧", "[烟花]"), arrayOf("总结一下，近年来获得成功的主要分三类：🙈\n登录成功，下载成功，支付成功。😆", "[庆祝]"), arrayOf("你社你的会，我富我的贵😎\n我们香水不犯花露水。😏", "[庆祝]"), arrayOf("吃了这个鸳鸯锅🥢\n你我的胃从此结为夫妻。👰🤵", "[烟花]"), arrayOf("我是小熊饼干🧸\n爱吃小区保安。🚔", "[庆祝]"), arrayOf("如果菜咸了🥘就等一会儿\n因为时间可以冲淡一切！🕓", "[烟花]")),
        "🐶单身宣言" to listOf(arrayOf("谈什么恋爱💢\n王者上荣耀没？🤡", "[炸弹]"), arrayOf("你们宁愿当狗也不来跟我表白😠\n这个血海深仇我记下了📝", "[炸弹]"), arrayOf("我的女朋友不拜金不虚荣不懒惰🤗\n不存在🤪", "[炸弹]"), arrayOf("我想今天玩一整天的开心消消乐😑\n消一对是一对🙅‍♀️", "[炸弹]"), arrayOf("我是一只小青蛙🐸\n寡寡寡寡寡🐸", "[庆祝]"), arrayOf("今年还是过不上🙁\n明年继续加油💪", "[烟花]"), arrayOf("别人一谈对象谈三年🌝\n我一句谈吗问三年🌚", "[庆祝]"), arrayOf("我只是一个平平无奇的🙃\n单身小天才🤠", "[烟花]"), arrayOf("我是一个没有感情的扣999机器9️⃣", "[庆祝]"), arrayOf("朋友圈里没有对象的男孩子😌\n找我互删一下😏\n别人不要的男孩子🤨\n我也不要🙂", "[烟花]"), arrayOf("有人收闲置宝贝么💋\n反正我闲着也是闲着🤦‍♀️", "[庆祝]"), arrayOf("祝大家拥有爱情❤️\n而我拥有金钱💳", "[烟花]"), arrayOf("宝贝长宝贝短😥\n宝贝单身你不管😩", "[烟花]"), arrayOf("没人疼 没人爱😞\n我是孤独小白菜🥦", "[烟花]"), arrayOf("你们发现没有🌝\n优秀的人普遍单身🙂", "[庆祝]"), arrayOf("关爱小动物从你我做起🐵\n坚决抵制虐狗🐶", "[烟花]"), arrayOf("有需要电灯泡的嘛💡\n我想近距离看看爱情❤️", "[烟花]"), arrayOf("想换情头了💞\n麻烦大家推荐几组好看的男朋友💁‍♀️", "[庆祝]"), arrayOf("关爱小动物从你我做起🧸\n坚决抵制虐狗🐶", "[烟花]")),
        "💣群聊轰炸" to listOf(arrayOf("很喜欢这个群，到了早上齐刷刷的全是比狗都困，根本没人在装热爱生活","[炸弹]"), arrayOf("我喜欢你，这条信息虽然群里所有人都能看到，但是你应该知道我说的就是你", "[炸弹]"), arrayOf("怎么回事，一个钟没人讲话了，今天是工作日啊，工作日不在群里讲话是想干什么，给资本家当走狗吗？我工作日一看到群里的消息断了，我的嘴就发痒，心就发痛", "[炸弹]"), arrayOf("好久没来群里说话，看你们其乐融融的样子，我感觉你们都有小群，有私聊了，只有我像个憨憨什么都看不懂，还乐呵呵的跟着笑。别复制，这是我的心里话", "[炸弹]"), arrayOf("有些新群员心态没有放稳，你给老板打工搬砖，你能学到东西吗？你在群里聊天你培养的交际能力是实打实的呀，是跟着你一辈子的呀，不要把眼光老是放在工资上面，你将来能力有了，你去哪儿不能高就？说了这么多，一起摸鱼吧", "[炸弹]"), arrayOf("又到周一，我都快忙死了，每个群都恢复了往日的生机，我都快聊不过来了", "[炸弹]"), arrayOf("每个群里都有一个不喜欢说话的人，每当大家聊得热火朝天的时候，他都会安静地在边上看，手握着鼠标，轻轻地点击着“收藏表情”，是谁我不说", "[炸弹]"), arrayOf("群里又哑巴了，每次看你们聊的都火热的，但是只要我发了一句话或者一个字，甚至一个表情，群里顿时就没了声音，怎么？你们欠我钱？看到我就跑是么？", "[炸弹]"), arrayOf("离婚这么多年了，竟然在群里遇到你了，你过得挺好我也就放心了。孩子上四年级了，成绩一直都很好，连续三年拿班上第一名，就是性格像你，每次生气就逃避，孩子总问妈妈什么时候来参加家长会！有时间就来看看孩子吧", "[炸弹]"), arrayOf("退群了，以后你们的事跟我没关系，你们过得好不好与我无关，总之我再也不想看到你们，你们让我挺失望的，从来没想过你们是这种人，认识时间也不短了，怪我没好好看你们，别人说你们我还不乐意，现在想想好傻啊，无语了，当我没认识过你们，真够恶心的，这段话也不知道谁写的好狠心啊，我复制给你们看看", "[炸弹]"), arrayOf("如果我没猜错，你们现在都在看群里的消息吧", "[炸弹]"), arrayOf("有些话不能乱说，里面是有忌讳的，你还小，不懂这里面的套路。有些话说出来，冥冥之中就会改变你生活的轨迹。看似简单的一句话，将会影响你的一生。你没有能力把握它，听姐一句劝，把这句话撤回了让姐说", "[炸弹]"), arrayOf("温馨提示：切记群员之间不要产生恋情，尝吃禁果，万万不可！网络不是不法之地，请爱护自己，网络对象都是虚拟的未知的！网恋＝群里勾搭 添加好友 日常 聊骚 暧昧不断 互发私照 确定关系 线下见面 翻云覆雨 假装冷漠 矛盾频出 关系破裂 删除好友 双双退群", "[炸弹]"), arrayOf("各位优秀的朋友再见了，很抱歉做了这个决定，希望我们有缘再见吧。因为遇到了一些意料之外的突发情况，对我的影响和打击比较大，所以不打算和大家聊了。和大家正式道个别吧，真的挺舍不得你们的，这么久了，能够认识你们真的很开心。谢谢你们带给了我许多的欢乐和感动，谢谢你们一直陪伴在我的身边。这次离开大家，主要原因是因为手机要充电了，我充满了就回来", "[炸弹]"), arrayOf("不好意思，也不是平时不想群聊，只是我这台手机2千多买的，平时都供着，打电话发消息的时候要先上柱香，用完之后拿清洁剂里里外外擦一遍，防止留下指纹，都是贴正面膜背面膜镜头膜还要戴全包的手机套，防止划伤和氧化，有需要装兜里的时候，先裹上三层丝绸防止在兜里划伤，每隔十分钟，都要给手机翻个身，充电的时候只用原装数据线和充电头，家里有一条专门的电路给它充电，防止电流不稳，给电池造成损伤，耳机都是用蓝牙，防止有线耳机多次插拔损伤接口，不说了，一次打这么多字心疼死我了，我得赶紧关机，给手机做一次保养", "[炸弹]")),
        "🥰谢谢红包" to listOf(arrayOf("谢谢红包\n祝您佳节如意\n一年到头走好运💰", "[烟花]"), arrayOf("谢谢您的红包🤴\n虎年伊始\n祝您全家欢乐🥰", "[庆祝]"), arrayOf("感谢红包\n我真挚地祝您春节快乐🏮", "[烟花]"), arrayOf("感谢红包💰\n祝你虎年行大运\n好运翻倍\n快乐加倍(^_−)☆", "[庆祝]"), arrayOf("我在通往富婆的这条路上\n你是最大的功臣\n春节快乐ヾ(o´∀｀o)ﾉ", "[烟花]"), arrayOf("感谢老板的新年红包\n祝福老板大富大贵💰", "[烟花]"), arrayOf("激动的心\n颤抖的手\n谢谢你的红包🧧", "[庆祝]"), arrayOf("感谢天感谢地\n更要感谢发红包的你🧧\n新年快乐！", "[烟花]"), arrayOf("💰红包到\n好运来\n开心的一天开始了* ੈ✩‧₊˚", "[烟花]"), arrayOf("谢谢你的大红包🧧\n新年快乐哦♪(･ω･)ﾉ", "[烟花]"), arrayOf("希望你每年都能保持给我发红包的习惯😆", "[烟花]"), arrayOf("大河向东流啊\n收到红包很知足啊🧧", "[烟花]"), arrayOf("感谢红包🧧\n承蒙厚爱\n日后报答\n酒肉管够😃", "[烟花]"), arrayOf("世间万物🌺\n唯有发红包的人不可辜负(*^▽^*)", "[烟花]"), arrayOf("又收到礼物\n我只想用四个字夸赞我家大哥\n眼光真好👍🏻", "[烟花]"), arrayOf("感谢天\n感谢地\n感谢大哥的赏赐🤙🤙", "[烟花]"), arrayOf("是不是看不起我？\n我是一个红包就可以拉拢的人吗？\n除非再来一个", "[烟花]"), arrayOf("收到大哥今日份的投食", "[烟花]"), arrayOf("给我发红包的这个男人\n简直不要太帅了", "[烟花]"), arrayOf("红包有多大\n我就爱你有多深", "[烟花]"), arrayOf("虽然钱财都是身外之物\n但是我不介意我身外都是你的钱财", "[烟花]"), arrayOf("尾款已到账\n本闲置宝贝暂时记在你的名下吧", "[烟花]"), arrayOf("又收到红包\n我只想用四个字夸赞我家大哥\n眼光真好👍🏻", "[烟花]")),
        "🧧跪求红包" to listOf(arrayOf("感谢天感谢地😘\n更要感谢发红包的你[红包]", "[庆祝]"), arrayOf("谢谢大家的新年红包[红包]\n因为没有收到就不一一感谢了😔", "[烟花]"), arrayOf("富婆[爱心]\n饿饿😭", "[炸弹]"), arrayOf("谢谢大家的红包[红包]\n虽然还没发😭\n可能是忘了😭\n但是先谢谢了🙇‍♂️\n总不可能真的不发吧😜", "[烟花]"), arrayOf("恭喜发财🎉\n红包拿来[红包]", "[庆祝]"), arrayOf("我需要一个红色的包包[红包]\n谢谢好人！😘", "[烟花]"), arrayOf("急需红包封印\n镇住小可爱[爱心]", "[庆祝]"), arrayOf("红包抢得好[红包]\n生活没烦恼😉", "[烟花]"), arrayOf("等一个发红包的人[红包],也支持转账😉", "[庆祝]"), arrayOf("红包你都不发吗\n那我再等等[红包]", "[烟花]"), arrayOf("可怜可怜孩子\n来一个专属[红包]", "[庆祝]"), arrayOf("你看彩虹的第一个颜色\n会不会想起给我发个[红包]", "[烟花]"), arrayOf("[红包]拿来吧你！", "[庆祝]"), arrayOf("感情到不到位\n看[红包]的大小了", "[烟花]"), arrayOf("臭宝，发个[红包]你就可以变成香宝哟", "[庆祝]"), arrayOf("礼轻情意重\n[红包]虽小但你得发", "[烟花]"), arrayOf("⛰山穷水复疑无路\n有你红包又一村🧧", "[庆祝]"), arrayOf("💐春花秋月何时了\n发个红包好不好🧧", "[烟花]"), arrayOf("需要红包叫醒🧧~~~ \n∩∩\n（´･ω･）\n＿|　⊃／(＿＿_\n／ └-(＿＿＿_／\n￣￣￣￣￣￣￣", "[庆祝]"), arrayOf("|_∧ \n|･ω･) .... 求红包🧧\n|つ", "[烟花]"), arrayOf("ᵕ̈𝒽𝒶𝓅𝓅𝓎 ꫛꫀꪝ❥ 🐯𝟚𝟘𝟚𝟚🐯\n🧨门口放鞭炮的吓死我了\n🧧请给个红包压压惊🥳", "[烟花]"), arrayOf("🐯(っ◔◡◔)っ 💸💵💴\n谁说我是来要红包的🧧\n到底是谁走漏了风声🤠", "[庆祝]"), arrayOf("🧧🧧ฅ՞•ﻌ•՞ฅ︎ 🧧🧧\n别的小朋友都有红包\n你的小朋友也想要个红包😊\n୧ʕ•̬͡•ʕ•̫͡•♡ ʕ͙•̫͑͡•ʔͦʕͮ•̫ͤ͡•ʔ͙ ʕ•̫͡•ʕ•̫͡•ʔ•̫͡•ʔ", "[烟花]"), arrayOf("Σ>―(〃°ω°〃)♡→💸\n🧧红包在哪里呀\n🧧红包在哪里\n💰快来小可爱的碗碗里", "[庆祝]")),
    )

    val SymbolPreset: Map<String, String> = hashMapOf(
        "(" to ")", "[" to "]", "{" to "}", "（" to "）", "［" to "］", "｛" to "｝", "❨" to "❩", "❲" to "❳", "❴" to "❵", "‘" to "’", "“" to "”", "❛" to "❜", "❝" to "❞", "<" to ">", "〈" to "〉", "《" to "》", "〔" to "〕", "【" to "】", "〘" to "〙", "「" to "」", "『" to "』", "︵" to "︶", "︷" to "︸", "︹" to "︺", "︻" to "︼", "︽" to "︾", "︿" to "﹀", "﹁" to "﹂",)
    }