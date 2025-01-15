package com.yuyan.imemodule.view.keyboard

import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.application.ImeSdkApplication
import com.yuyan.imemodule.data.flower.FlowerTypefaceMode
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.manager.InputModeSwitcherManager
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.prefs.behavior.SkbMenuMode
import com.yuyan.imemodule.prefs.behavior.SymbolMode
import com.yuyan.imemodule.singleton.EnvironmentSingleton
import com.yuyan.imemodule.ui.utils.AppUtil
import com.yuyan.imemodule.utils.KeyboardLoaderUtil
import com.yuyan.imemodule.view.keyboard.container.CandidatesContainer
import com.yuyan.imemodule.view.keyboard.container.ClipBoardContainer
import com.yuyan.imemodule.view.keyboard.container.SettingsContainer
import com.yuyan.imemodule.view.keyboard.container.SymbolContainer
import com.yuyan.inputmethod.core.Kernel

fun onSettingsMenuClick(inputView: InputView, skbMenuMode: SkbMenuMode) {
    when (skbMenuMode) {
        SkbMenuMode.Emojicon, SkbMenuMode.Emoticon -> {
            val symbolType = if(skbMenuMode == SkbMenuMode.Emoticon) SymbolMode.Emoticon else SymbolMode.Emojicon
            if((KeyboardManager.instance.currentContainer as? SymbolContainer)?.getMenuMode() == symbolType){
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            } else {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SYMBOL)
                inputView.mSkbCandidatesBarView.showEmoji()
                (KeyboardManager.instance.currentContainer as? SymbolContainer)?.setEmojisView(symbolType)
            }
        }
        SkbMenuMode.SwitchKeyboard -> {
            KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
            (KeyboardManager.instance.currentContainer as? SettingsContainer)?.showSkbSelelctModeView()
        }
        SkbMenuMode.KeyboardHeight -> {
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            KeyboardManager.instance.currentContainer!!.setKeyboardHeight()
        }
        SkbMenuMode.DarkTheme -> {
            val theme = (if (ThemeManager.activeTheme.isDark) ThemeManager.prefs.lightModeTheme else ThemeManager.prefs.darkModeTheme).getValue()
            ThemeManager.setNormalModeTheme(theme)
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.Feedback -> {
            AppUtil.launchSettingsToKeyboard(ImeSdkApplication.context)
        }
        SkbMenuMode.NumberRow -> {
            val abcNumberLine = AppPrefs.getInstance().keyboardSetting.abcNumberLine.getValue()
            AppPrefs.getInstance().keyboardSetting.abcNumberLine.setValue(!abcNumberLine)
            //更换键盘模式后 重亲加载键盘
            KeyboardLoaderUtil.instance.changeSKBNumberRow()
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.JianFan -> {
            val chineseFanTi = AppPrefs.getInstance().input.chineseFanTi.getValue()
            AppPrefs.getInstance().input.chineseFanTi.setValue(!chineseFanTi)
            Kernel.nativeUpdateImeOption()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.LockEnglish -> {
            val keyboardLockEnglish = AppPrefs.getInstance().keyboardSetting.keyboardLockEnglish.getValue()
            AppPrefs.getInstance().keyboardSetting.keyboardLockEnglish.setValue(!keyboardLockEnglish)
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.SymbolShow -> {
            val keyboardSymbol = ThemeManager.prefs.keyboardSymbol.getValue()
            ThemeManager.prefs.keyboardSymbol.setValue(!keyboardSymbol)
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.Mnemonic -> {
            val keyboardMnemonic = ThemeManager.prefs.keyboardMnemonic.getValue()
            ThemeManager.prefs.keyboardMnemonic.setValue(!keyboardMnemonic)
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.EmojiInput -> {
            val emojiInput = AppPrefs.getInstance().input.emojiInput.getValue()
            AppPrefs.getInstance().input.emojiInput.setValue(!emojiInput)
            Kernel.nativeUpdateImeOption()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.Handwriting -> AppUtil.launchSettingsToHandwriting(ImeSdkApplication.context)
        SkbMenuMode.Settings -> AppUtil.launchSettings(ImeSdkApplication.context)
        SkbMenuMode.OneHanded -> {
            AppPrefs.getInstance().keyboardSetting.oneHandedModSwitch.setValue(!AppPrefs.getInstance().keyboardSetting.oneHandedModSwitch.getValue())
            EnvironmentSingleton.instance.initData()
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.FlowerTypeface -> {
            CustomConstant.flowerTypeface = if(CustomConstant.flowerTypeface == FlowerTypefaceMode.Disabled) FlowerTypefaceMode.Mars else FlowerTypefaceMode.Disabled
            inputView.mSkbCandidatesBarView.showFlowerTypeface()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.FloatKeyboard -> {
            val keyboardModeFloat = EnvironmentSingleton.instance.keyboardModeFloat
            EnvironmentSingleton.instance.keyboardModeFloat = !keyboardModeFloat
            EnvironmentSingleton.instance.initData()
            KeyboardLoaderUtil.instance.clearKeyboardMap()
            KeyboardManager.instance.clearKeyboard()
            KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
        }
        SkbMenuMode.ClipBoard, SkbMenuMode.Phrases -> {
            val currentContainer = KeyboardManager.instance.currentContainer as? ClipBoardContainer
            if(currentContainer != null){
                if(currentContainer.getMenuMode() == skbMenuMode) KeyboardManager.instance.switchKeyboard(
                    InputModeSwitcherManager.skbLayout)
                else currentContainer.showClipBoardView(skbMenuMode)
            } else {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.ClipBoard)
                (KeyboardManager.instance.currentContainer as? ClipBoardContainer)?.showClipBoardView(skbMenuMode)
            }
            inputView.updateCandidateBar()
        }
        SkbMenuMode.Custom -> {
            KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
            (KeyboardManager.instance.currentContainer as? SettingsContainer)?.enableDragItem(true)
        }
        SkbMenuMode.CloseSKB -> {
            inputView.requestHideSelf()
        }
        SkbMenuMode.SettingsMenu -> {
            if (KeyboardManager.instance.isInputKeyboard) {
                KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.SETTINGS)
                (KeyboardManager.instance.currentContainer as? SettingsContainer)?.showSettingsView()
                inputView.updateCandidateBar()
            } else {
                KeyboardManager.instance.switchKeyboard(InputModeSwitcherManager.skbLayout)
            }
        }
        SkbMenuMode.CandidatesMore -> {
            KeyboardManager.instance.switchKeyboard(KeyboardManager.KeyboardType.CANDIDATES)
            (KeyboardManager.instance.currentContainer as? CandidatesContainer)?.showCandidatesView()
        }
        else ->{}
    }
}