package com.foobarust.deliverer.ui.sectionuser

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.data.models.UserDelivery
import com.foobarust.deliverer.databinding.FragmentUserInfoBinding
import com.foobarust.deliverer.states.Resource
import com.foobarust.deliverer.ui.shared.AppConfig.PHONE_NUM_PREFIX
import com.foobarust.deliverer.utils.AutoClearedValue
import com.foobarust.deliverer.utils.findNavController
import com.foobarust.deliverer.utils.loadGlideUrl
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by kevin on 4/7/21
 */

@AndroidEntryPoint
class UserInfoFragment : BottomSheetDialogFragment() {

    private var binding: FragmentUserInfoBinding by AutoClearedValue(this)
    private val viewModel: UserInfoViewModel by viewModels()
    private val navArgs: UserInfoFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onFetchUserProfile(navArgs.userId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserInfoBinding.inflate(inflater, container, false)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userProfile.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        setupViews(it.data)
                    }
                    is Resource.Error -> {
                        showShortToast(it.message)
                        findNavController(R.id.userInfoFragment)?.navigateUp()
                    }
                    is Resource.Loading -> Unit
                }
            }
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setupViews(userInfo: UserDelivery) = binding.run {
        delivererPhotoImageView.loadGlideUrl(
            imageUrl = userInfo.photoUrl,
            circularCrop = true,
            placeholder = R.drawable.ic_user
        )
        delivererNameTextView.text = userInfo.name
        phoneNumTextView.text = PHONE_NUM_PREFIX + ' ' + userInfo.phoneNum
    }
}