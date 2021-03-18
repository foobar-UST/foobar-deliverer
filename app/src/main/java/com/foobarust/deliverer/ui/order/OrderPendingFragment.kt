package com.foobarust.deliverer.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.foobarust.deliverer.databinding.FragmentOrderPendingBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 3/8/21
 */

@AndroidEntryPoint
class OrderPendingFragment : Fragment() {

    private var binding: FragmentOrderPendingBinding by AutoClearedValue(this)
    private val viewModel: OrderPendingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderPendingBinding.inflate(inflater, container, false)

        return binding.root
    }
}