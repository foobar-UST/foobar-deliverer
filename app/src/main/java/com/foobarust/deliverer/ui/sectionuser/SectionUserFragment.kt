package com.foobarust.deliverer.ui.sectionuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.FragmentSectionUserBinding
import com.foobarust.deliverer.ui.shared.FullScreenDialogFragment
import com.foobarust.deliverer.utils.*
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 4/2/21
 */

@AndroidEntryPoint
class SectionUserFragment : FullScreenDialogFragment() {

    private var binding: FragmentSectionUserBinding by AutoClearedValue(this)
    private lateinit var navController: NavController
    private lateinit var viewModel: SectionUserViewModel
    private val navArgs: SectionUserFragmentArgs by navArgs()

    override var onBackPressed: (() -> Unit)? = { handleBackPressed() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSectionUserBinding.inflate(inflater, container, false).apply {
            root.applyLayoutFullscreen()
            appBarLayout.applySystemWindowInsetsPadding(applyTop = true)
        }

        // Setup navigation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.section_users_container)
            as NavHostFragment

        navController = navHostFragment.navController.apply {
            setGraph(R.navigation.navigation_section_user)
        }

        viewModel = getHiltNavGraphViewModel(
            navGraphId = R.id.navigation_section_user,
            navController = navController
        )

        // Record current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            viewModel.onUpdateCurrentDestination(destination.id)
        }

        // Setup toolbar
        with(binding.toolbar) {
            title = getString(
                R.string.section_user_toolbar_title,
                navArgs.property.joinedUsersCount,
                navArgs.property.maxUsersCount
            )

            setNavigationOnClickListener {
                handleBackPressed()
            }
        }

        return binding.root
    }

    private fun handleBackPressed() {
        // Dismiss the dialog when back pressing in start destination
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.sectionUserListFragment) {
            findNavController(R.id.sectionUserFragment)?.navigateUp()
        } else {
            viewModel.onBackPressed()
        }
    }
}