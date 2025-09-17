package com.yuyan.imemodule.ui.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.yuyan.imemodule.R
import com.yuyan.imemodule.databinding.ActivitySettingsBinding
import com.yuyan.imemodule.prefs.AppPrefs
import com.yuyan.imemodule.ui.setup.SetupActivity
import com.yuyan.imemodule.utils.startActivity
import splitties.dimensions.dp
import splitties.views.topPadding

open class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding =ActivitySettingsBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val statusBars = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBars = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            binding.root.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                leftMargin = navBars.left
                rightMargin = navBars.right
            }
            binding.toolbar.topPadding = statusBars.top
            windowInsets
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(
            // 设置不显示返回箭头的界面
            topLevelDestinationIds = setOf(R.id.privacyPolicyFragment)
        )
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        navController = navHostFragment!!.navController

        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        binding.toolbar.setNavigationOnClickListener {
            // prevent navigate up when child fragment has enabled `OnBackPressedCallback`
            if (onBackPressedDispatcher.hasEnabledCallbacks()) {
                onBackPressedDispatcher.onBackPressed()
                return@setNavigationOnClickListener
            }
            // "minimize" the activity if we can't go back
            navController.navigateUp() || onSupportNavigateUp() || moveTaskToBack(false)
        }
        viewModel.toolbarTitle.observe(this) {
            binding.toolbar.title = it
        }
        viewModel.toolbarShadow.observe(this) {
            binding.toolbar.elevation = dp(if (it) 4f else 0f)
        }
        navController.addOnDestinationChangedListener { _, dest, _ ->
            when (dest.id) {
                R.id.themeFragment -> viewModel.disableToolbarShadow()
                else -> viewModel.enableToolbarShadow()
            }
        }
        if(!AppPrefs.getInstance().internal.privacyPolicySure.getValue()){
            navController.navigate(R.id.action_settingsFragment_to_privacyPolicyFragment)
            return
        }
        if (SetupActivity.shouldShowUp()) {
            startActivity<SetupActivity>()
        }
    }

    fun onclick(view: View) {
        when (view.id){
            R.id.privacy_policy_sure -> {
                navController.navigateUp()
                AppPrefs.getInstance().internal.privacyPolicySure.setValue(true)
                if (SetupActivity.shouldShowUp()) {
                    startActivity<SetupActivity>()
                }
            }
            R.id.privacy_policy_cancel ->{
                navController.navigateUp()
            }
        }
    }
}

class LauncherActivity : SettingsActivity()