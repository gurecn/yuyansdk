
package com.yuyan.imemodule.ui.fragment.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.yuyan.imemodule.R
import com.yuyan.imemodule.data.theme.Theme
import com.yuyan.imemodule.data.theme.ThemeManager
import com.yuyan.imemodule.view.widget.applyNavBarInsetsBottomPadding
import kotlinx.coroutines.launch
import splitties.resources.styledDrawable
import java.util.UUID

class ThemeListFragment : Fragment() {

    private lateinit var imageLauncher: ActivityResultLauncher<Theme.Custom?>

    private lateinit var themeListAdapter: ThemeListAdapter

    private var followSystemDayNightTheme by ThemeManager.prefs.followSystemDayNightTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageLauncher = registerForActivityResult(CustomThemeActivity.Contract()) { result ->
            if (result == null) return@registerForActivityResult
            when (result) {
                is CustomThemeActivity.BackgroundResult.Created -> {
                    val theme = result.theme
                    themeListAdapter.prependTheme(theme)
                    ThemeManager.saveTheme(theme)
                    if (!followSystemDayNightTheme) {
                        ThemeManager.setNormalModeTheme(theme)
                    }
                }
                is CustomThemeActivity.BackgroundResult.Deleted -> {
                    val name = result.name
                    themeListAdapter.removeTheme(name)
                    ThemeManager.deleteTheme(name)
                }
                is CustomThemeActivity.BackgroundResult.Updated -> {
                    val theme = result.theme
                    themeListAdapter.replaceTheme(theme)
                    ThemeManager.saveTheme(theme)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        themeListAdapter = object : ThemeListAdapter() {
            override fun onAddNewTheme() = addTheme()
            override fun onSelectTheme(theme: Theme) = selectTheme(theme)
            override fun onEditTheme(theme: Theme.Custom) = editTheme(theme)
//            override fun onExportTheme(theme: Theme.Custom) = exportTheme(theme)
        }
        ThemeManager.refreshThemes()
        themeListAdapter.setThemes(ThemeManager.getAllThemes())
        updateSelectedThemes()
        return ResponsiveThemeListView(requireContext()).apply {
            adapter = themeListAdapter
            applyNavBarInsetsBottomPadding()
        }
    }

    private fun updateSelectedThemes(activeTheme: Theme? = null) {
        val active = activeTheme ?: ThemeManager.activeTheme
        var light: Theme? = null
        var dark: Theme? = null
        if (followSystemDayNightTheme) {
            light = ThemeManager.prefs.lightModeTheme.getValue()
            dark = ThemeManager.prefs.darkModeTheme.getValue()
        }
        themeListAdapter.setSelectedThemes(active, light, dark)
    }

    private fun addTheme() {
        val ctx = requireContext()
        val actions = arrayOf(
            getString(R.string.choose_image),
            getString(R.string.duplicate_builtin_theme)
        )
        AlertDialog.Builder(ctx)
            .setTitle(R.string.new_theme)
            .setNegativeButton(android.R.string.cancel, null)
            .setItems(actions) { _, i ->
                when (i) {
                    0 -> imageLauncher.launch(null)
                    1 -> {
                        val view = ResponsiveThemeListView(ctx).apply {
                            // force AlertDialog's customPanel to grow
                            minimumHeight = Int.MAX_VALUE
                        }
                        val dialog = AlertDialog.Builder(ctx)
                            .setTitle(getString(R.string.duplicate_builtin_theme).removeSuffix("…"))
                            .setNegativeButton(android.R.string.cancel, null)
                            .setView(view)
                            .create()
                        view.adapter = object :
                            SimpleThemeListAdapter<Theme.Builtin>(ThemeManager.BuiltinThemes) {
                            override fun onClick(theme: Theme.Builtin) {
                                val newTheme =
                                    theme.deriveCustomNoBackground(UUID.randomUUID().toString())
                                themeListAdapter.prependTheme(newTheme)
                                ThemeManager.saveTheme(newTheme)
                                dialog.dismiss()
                            }
                        }
                        dialog.show()
                    }
                }
            }
            .show()
    }

    private fun selectTheme(theme: Theme) {
        if (followSystemDayNightTheme) {
            val ctx = requireContext()
            AlertDialog.Builder(ctx)
                .setIcon(ctx.styledDrawable(android.R.attr.alertDialogIcon))
                .setTitle(R.string.configure)
                .setMessage(R.string.theme_message_follow_system_day_night_mode_enabled)
                .setPositiveButton(android.R.string.cancel, null)
                .setNegativeButton(R.string.disable_it) { _, _ ->
                    followSystemDayNightTheme = false
                    lifecycleScope.launch {
                        ThemeManager.setNormalModeTheme(theme)
                        updateSelectedThemes()
                    }
                }
                .show()
            return
        }
        ThemeManager.setNormalModeTheme(theme)
        updateSelectedThemes()
    }

    private fun editTheme(theme: Theme.Custom) {
        imageLauncher.launch(theme)
    }

}
