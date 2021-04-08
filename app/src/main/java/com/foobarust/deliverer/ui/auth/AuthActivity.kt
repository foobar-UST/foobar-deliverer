package com.foobarust.deliverer.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.ActivityAuthBinding
import com.foobarust.deliverer.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull

/**
 * Created by kevin on 2/17/21
 */

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private var binding: ActivityAuthBinding? = null
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Foobar_Auth_DayNight)
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Setup navigation
        lifecycleScope.launchWhenCreated {
            val startAuthNav = viewModel.startAuthNav.firstOrNull()

            if (startAuthNav != null) {
                binding = ActivityAuthBinding.inflate(layoutInflater).also {
                    setContentView(it.root)
                }
                setupNavigation()
            }
        }

        // Navigate to MainActivity
        lifecycleScope.launchWhenCreated {
            viewModel.navigateToMain.collect {
                setTheme(R.style.Theme_Foobar_Auth_DayNight)
                navigateToMain()
            }
        }

        // Show toast
        lifecycleScope.launchWhenStarted {
            viewModel.toastMessage.collect {
                showShortToast(it)
            }
        }
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragment_container
        ) as NavHostFragment

        navController = navHostFragment.navController
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}