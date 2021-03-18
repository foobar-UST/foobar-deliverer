package com.foobarust.deliverer.ui.bottomdrawer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.foobarust.deliverer.databinding.FragmentBottomDrawerBinding
import com.foobarust.deliverer.utils.AutoClearedValue
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by kevin on 3/18/21
 */

@AndroidEntryPoint
class BottomDrawerFragment : Fragment() {

    private var binding: FragmentBottomDrawerBinding by AutoClearedValue(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomDrawerBinding.inflate(inflater, container, false)

        return binding.root
    }
}