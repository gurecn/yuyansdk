
package com.yuyan.imemodule.data.theme

import com.yuyan.imemodule.R

object ThemePreset {

    val MaterialLight = Theme.Builtin(
        name = "MaterialLight",
        isDark = false,
        keyboardResources = 0x0,
        keyboardColor = 0xffeceff1,
        barColor = 0xffe4e7e9,
        keyBackgroundColor = 0xfffbfbfc,
        keyTextColor = 0xff37474f,
        accentKeyBackgroundColor = 0xff5cb5ab,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xffd9dbdd,
        functionKeyBackgroundColor = 0xffc9ced1,
        functionKeyPressHighlightColor = 0x1f000000,
    )

    val MaterialDark = Theme.Builtin(
        name = "MaterialDark",
        isDark = true,
        keyboardResources = 0x0,
        barColor = 0xff21272b,
        keyboardColor = 0xff263238,
        keyBackgroundColor = 0xff404a50,
        keyTextColor = 0xffd9dbdc,
        accentKeyBackgroundColor = 0xff6eaca8,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x33ffffff,
        popupBackgroundColor = 0xff3c474c,
        functionKeyBackgroundColor = 0xff3b464c,
        functionKeyPressHighlightColor = 0x1fffffff,
    )

    val PixelLight = Theme.Builtin(
        name = "PixelLight",
        isDark = false,
        keyboardResources = 0x0,
        barColor = 0xffeeeeee,
        keyboardColor = 0xffE3E4EA,
        keyBackgroundColor = 0xffFCFCFC,
        keyTextColor = 0xff212121,
        accentKeyBackgroundColor = 0xff4285f4,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xffeeeeee,
        functionKeyBackgroundColor = 0xffB7BDC4,
        functionKeyPressHighlightColor = 0x1f000000,
    )

    val PixelDark = Theme.Builtin(
        name = "PixelDark",
        isDark = true,
        keyboardResources = 0x0,
        barColor = 0xff373737,
        keyboardColor = 0xff2d2d2d,
        keyBackgroundColor = 0xff464646,
        keyTextColor = 0xfffafafa,
        accentKeyBackgroundColor = 0xff5e97f6,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x33ffffff,
        popupBackgroundColor = 0xff373737,
        functionKeyBackgroundColor = 0xff4a4a4a,
        functionKeyPressHighlightColor = 0x1fffffff,
    )

    val AMOLEDBlack = Theme.Builtin(
        name = "AMOLEDBlack",
        isDark = true,
        keyboardResources = 0x0,
        barColor = 0xff373737,
        keyboardColor = 0xff000000,
        keyBackgroundColor = 0xff2e2e2e,
        keyTextColor = 0xffffffff,
        accentKeyBackgroundColor = 0xff80cbc4,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x33ffffff,
        popupBackgroundColor = 0xff373737,
        functionKeyBackgroundColor = 0xff727272,
        functionKeyPressHighlightColor = 0x1fffffff,
    )

    val NordLight = Theme.Builtin(
        name = "NordLight",
        isDark = false,
        keyboardResources = 0x0,
        barColor = 0xffE5E9F0,
        keyboardColor = 0xffECEFF4,
        keyBackgroundColor = 0xfffbfbfc,
        keyTextColor = 0xff2E3440,
        accentKeyBackgroundColor = 0xff5E81AC,
        accentKeyTextColor = 0xffECEFF4,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xffE5E9F0,
        functionKeyBackgroundColor = 0xffD8DEE9,
        functionKeyPressHighlightColor = 0x1f000000,
    )

    val NordDark = Theme.Builtin(
        name = "NordDark",
        isDark = true,
        keyboardResources = 0x0,
        barColor = 0xff434C5E,
        keyboardColor = 0xff2E3440,
        keyBackgroundColor = 0xff4C566A,
        keyTextColor = 0xffECEFF4,
        accentKeyBackgroundColor = 0xff88C0D0,
        accentKeyTextColor = 0xff2E3440,
        keyPressHighlightColor = 0x33ffffff,
        popupBackgroundColor = 0xff434C5E,
        functionKeyBackgroundColor = 0xff4C566A,
        functionKeyPressHighlightColor = 0x1fffffff,
    )

    val Monokai = Theme.Builtin(
        name = "Monokai",
        isDark = true,
        keyboardResources = 0x0,
        barColor = 0xff1f201b,
        keyboardColor = 0xff272822,
        keyBackgroundColor = 0xff33342c,
        keyTextColor = 0xffd6d6d6,
        accentKeyBackgroundColor = 0xffb05279,
        accentKeyTextColor = 0xffd6d6d6,
        keyPressHighlightColor = 0x33ffffff,
        popupBackgroundColor = 0xff1f201b,
        functionKeyBackgroundColor = 0xff373830,
        functionKeyPressHighlightColor = 0x1fffffff,
    )

    /**
     * transparent background with semi-transparent white keys
     */
    val TransparentDark = Theme.Builtin(
        name = "TransparentDark",
        isDark = true,
        keyboardResources = 0x0,
        barColor = 0x4c000000,
        keyboardColor = 0x00000000,
        keyBackgroundColor = 0x4bffffff,
        keyTextColor = 0xffffffff,
        accentKeyBackgroundColor = 0xff5e97f6,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xff373737,
        functionKeyBackgroundColor = 0x4bffffff,
        functionKeyPressHighlightColor = 0x1fffffff,
    )

    /**
     * transparent background with semi-transparent black keys
     */
    val TransparentLight = Theme.Builtin(
        name = "TransparentLight",
        isDark = false,
        keyboardResources = 0x0,
        barColor = 0x26000000,
        keyboardColor = 0x00000000,
        keyBackgroundColor = 0x4bffffff,
        keyTextColor = 0xff000000,
        accentKeyBackgroundColor = 0xff5e97f6,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xffeeeeee,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )

    val CustomRed = Theme.Builtin(
        name = "CustomRed",
        isDark = false,
        keyboardResources = R.drawable.skb_background_red,
        barColor = 0xfffbae44,
        keyboardColor = 0xffeceff1,
        keyBackgroundColor = 0x33fbfbfc,
        keyTextColor = 0xffffffff,
        accentKeyBackgroundColor = 0xffffaaa5,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xfffd3422,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )
    val CustomPink = Theme.Builtin(
        name = "CustomPink",
        isDark = false,
        keyboardResources = R.drawable.skb_background_pink,
        barColor = 0xffd5a3a4,
        keyboardColor = 0xffeceff1,
        keyBackgroundColor = 0x33fbfbfc,
        keyTextColor = 0xff2E3440,
        accentKeyBackgroundColor = 0xff6eaca8,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xffd7c4d0,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )
    val CustomYellow = Theme.Builtin(
        name = "CustomYellow",
        isDark = false,
        keyboardResources = R.drawable.skb_background_yellow,
        barColor = 0xffffecba,
        keyboardColor = 0xffeceff1,
        keyBackgroundColor = 0x33fbfbfc,
        keyTextColor = 0xff2E3440,
        accentKeyBackgroundColor = 0xff6eaca8,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xfff16877,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )
    val CustomCrimson = Theme.Builtin(
        name = "CustomCrimson",
        isDark = true,
        keyboardResources = R.drawable.skb_background_crimso,
        barColor = 0XFF860001,
        keyboardColor = 0xffeceff1,
        keyBackgroundColor = 0x33fbfbfc,
        keyTextColor = 0xffffffff,
        accentKeyBackgroundColor = 0xff6eaca8,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0XFF480603,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )
    val CustomPurple = Theme.Builtin(
        name = "CustomPurple",
        isDark = false,
        keyboardResources = R.drawable.skb_background_purple,
        barColor = 0xffe100ff,
        keyboardColor = 0xffeceff1,
        keyBackgroundColor = 0x33fbfbfc,
        keyTextColor = 0xffffffff,
        accentKeyBackgroundColor = 0xff7F00FF,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xff9f00ff,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )

    val CustomBlue = Theme.Builtin(
        name = "CustomBlue",
        isDark = false,
        keyboardResources = R.drawable.skb_background_blue,
        barColor = 0xff1488cc,
        keyboardColor = 0xffeceff1,
        keyBackgroundColor = 0x33fbfbfc,
        keyTextColor = 0xffffffff,
        accentKeyBackgroundColor = 0xff2196f3,
        accentKeyTextColor = 0xffffffff,
        keyPressHighlightColor = 0x1f000000,
        popupBackgroundColor = 0xff0d47a1,
        functionKeyBackgroundColor = 0x5affffff,
        functionKeyPressHighlightColor = 0x1f000000,
    )
}