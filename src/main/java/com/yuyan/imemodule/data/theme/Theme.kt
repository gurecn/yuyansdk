
package com.yuyan.imemodule.data.theme

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import androidx.core.content.ContextCompat
import com.yuyan.imemodule.application.Launcher
import com.yuyan.imemodule.utils.DarkenColorFilter
import com.yuyan.imemodule.utils.RectSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
sealed class Theme : Parcelable {

    abstract val name: String
    abstract val isDark: Boolean

    abstract val barColor: Int     // 候选拼音背景色

    abstract val keyboardResources: Int  // 键盘背景资源，首选
    abstract val keyboardColor: Int  // 键盘区域背景色，次选

    abstract val keyTextColor: Int   // 按键字体颜色

    abstract val keyBackgroundColor: Int    // 按键背景色
    abstract val keyPressHighlightColor: Int  // 按键按下背景色

    abstract val functionKeyBackgroundColor: Int    // 功能键背景色
    abstract val functionKeyPressHighlightColor: Int  // 功能键按下背景色

    abstract val accentKeyBackgroundColor: Int   // 回车键背景色
    abstract val accentKeyTextColor: Int   // 回车键字体色

    abstract val popupBackgroundColor: Int    // 长按弹窗背景色

    open fun backgroundDrawable(keyBorder: Boolean = false): Drawable {
        return if(keyboardResources != 0){
            ContextCompat.getDrawable(Launcher.instance.context, keyboardResources)!!
        } else {
            ColorDrawable(keyboardColor)
        }
    }

    @Serializable
    @Parcelize
    data class Custom(
        override val name: String,
        override val isDark: Boolean,
        /**
         * absolute paths of cropped and src png files
         */
        val backgroundImage: CustomBackground?,
        override val keyboardResources: Int,
        override val barColor: Int,
        override val keyboardColor: Int,
        override val keyBackgroundColor: Int,
        override val keyTextColor: Int,
        override val accentKeyBackgroundColor: Int,
        override val accentKeyTextColor: Int,
        override val keyPressHighlightColor: Int,
        override val popupBackgroundColor: Int,
        override val functionKeyBackgroundColor: Int,
        override val functionKeyPressHighlightColor: Int,
    ) : Theme() {
        @Parcelize
        @Serializable
        data class CustomBackground(
            val croppedFilePath: String,
            val srcFilePath: String,
            val brightness: Int = 70,
            val cropRect: @Serializable(RectSerializer::class) Rect?,
        ) : Parcelable {
            fun toDrawable(): Drawable? {
                val cropped = File(croppedFilePath)
                if (!cropped.exists()) return null
                val bitmap = BitmapFactory.decodeStream(cropped.inputStream()) ?: return null
                return BitmapDrawable(Launcher.instance.context.resources, bitmap).apply {
                    colorFilter = DarkenColorFilter(100 - brightness)
                }
            }
        }

        override fun backgroundDrawable(keyBorder: Boolean): Drawable {
            return backgroundImage?.toDrawable() ?: super.backgroundDrawable(keyBorder)
        }

    }

    @Parcelize
    data class Builtin(
        override val name: String,
        override val isDark: Boolean,
        override val keyboardResources: Int,
        override val barColor: Int,
        override val keyboardColor: Int,
        override val keyBackgroundColor: Int,
        override val keyTextColor: Int,
        override val accentKeyBackgroundColor: Int,
        override val accentKeyTextColor: Int,
        override val keyPressHighlightColor: Int,
        override val popupBackgroundColor: Int,
        override val functionKeyBackgroundColor: Int,
        override val functionKeyPressHighlightColor: Int,
    ) : Theme() {

        // an alias to use 0xAARRGGBB color literal in code
        // because kotlin compiler treats `0xff000000` as Long, not Int
        constructor(
            name: String,
            isDark: Boolean,
            keyboardResources: Number,
            barColor: Number,
            keyboardColor: Number,
            keyBackgroundColor: Number,
            keyTextColor: Number,
            accentKeyBackgroundColor: Number,
            accentKeyTextColor: Number,
            keyPressHighlightColor: Number,
            popupBackgroundColor: Number,
            functionKeyBackgroundColor: Number,
            functionKeyPressHighlightColor: Number,
        ) : this(
            name,
            isDark,
            keyboardResources.toInt(),
            barColor.toInt(),
            keyboardColor.toInt(),
            keyBackgroundColor.toInt(),
            keyTextColor.toInt(),
            accentKeyBackgroundColor.toInt(),
            accentKeyTextColor.toInt(),
            keyPressHighlightColor.toInt(),
            popupBackgroundColor.toInt(),
            functionKeyBackgroundColor.toInt(),
            functionKeyPressHighlightColor.toInt(),
        )

        fun deriveCustomNoBackground(name: String) = Custom(
            name,
            isDark,
            null,
            keyboardResources,
            barColor,
            keyboardColor,
            keyBackgroundColor,
            keyTextColor,
            accentKeyBackgroundColor,
            accentKeyTextColor,
            keyPressHighlightColor,
            popupBackgroundColor,
            functionKeyBackgroundColor,
            functionKeyPressHighlightColor,
        )

        fun deriveCustomBackground(
            name: String,
            croppedBackgroundImage: String,
            originBackgroundImage: String,
            brightness: Int = 70,
            cropBackgroundRect: Rect? = null,
        ) = Custom(
            name,
            isDark,
            Custom.CustomBackground(
                croppedBackgroundImage,
                originBackgroundImage,
                brightness,
                cropBackgroundRect
            ),
            keyboardResources,
            barColor,
            keyboardColor,
            keyBackgroundColor,
            keyTextColor,
            accentKeyBackgroundColor,
            accentKeyTextColor,
            keyPressHighlightColor,
            popupBackgroundColor,
            functionKeyBackgroundColor,
            functionKeyPressHighlightColor,
        )
    }

}