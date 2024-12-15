package com.yuyan.imemodule.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.yuyan.imemodule.R
import com.yuyan.imemodule.application.CustomConstant
import com.yuyan.imemodule.databinding.ActivitySettingsBinding
import com.yuyan.imemodule.ui.setup.SetupActivity
import com.yuyan.imemodule.ui.utils.AppUtil
import com.yuyan.imemodule.ui.utils.startActivity
import com.yuyan.imemodule.utils.TimeUtils
import splitties.dimensions.dp
import splitties.views.topPadding

class SettingsActivity : AppCompatActivity() {

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
            // always show back icon regardless of `navController.currentDestination`
            topLevelDestinationIds = setOf()
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

    }

    override fun onStart() {
        super.onStart()
        if (SetupActivity.shouldShowUp()) {
            startActivity<SetupActivity>()
        } else {
            val buildDiffDays = TimeUtils.getBuildDiffDays()
            if(buildDiffDays >= 30){
                AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_tips_title)
                    .setMessage(getString(if(buildDiffDays >= 60) R.string.app_build_timeout_60 else R.string.app_build_timeout_30))
                    .setCancelable(buildDiffDays < 60)
                    .setNeutralButton(R.string.go_download_appstore) { _, _ ->
                        AppUtil.launchMarketforYuyan(this)
                    }
                    .setNegativeButton(R.string.go_download_gitee) { _, _ ->
                        val uri = Uri.parse("${CustomConstant.YUYAN_IME_REPO_GITEE}/releases/latest")
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                    .setPositiveButton(R.string.go_download_github) { _, _ ->
                        val uri = Uri.parse("${CustomConstant.YUYAN_IME_REPO}/releases/latest")
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }.show()
            }
        }
    }
}