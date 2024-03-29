package com.foobarust.deliverer.ui.settings

import android.app.Dialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.foobarust.android.utils.showShortToast
import com.foobarust.deliverer.R
import com.foobarust.deliverer.databinding.DialogTextInputBinding
import com.foobarust.deliverer.ui.settings.TextInputType.*
import com.foobarust.deliverer.usecases.shared.GetFormattedPhoneNumUseCase
import com.foobarust.deliverer.utils.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TextInputDialog : DialogFragment() {

    private var binding: DialogTextInputBinding? = null
    private val viewModel: TextInputViewModel by viewModels()
    private val navArgs: TextInputDialogArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        viewModel.onUpdateTextInputProperty(navArgs.property)

        binding = DialogTextInputBinding.inflate(LayoutInflater.from(requireContext())).apply {
            with(valueEditText) {
                inputType = when (navArgs.property.type) {
                    NORMAL -> InputType.TYPE_CLASS_TEXT
                    NAME -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
                    PHONE_NUM -> InputType.TYPE_CLASS_NUMBER
                }

                setText(navArgs.property.value)
            }

            // Set input constraints
            if (navArgs.property.type == PHONE_NUM) {
                // Set max length
                valueEditText.filters = arrayOf<InputFilter>(
                    InputFilter.LengthFilter(GetFormattedPhoneNumUseCase.LENGTH)
                )
                valueTextInputLayout.prefixText = GetFormattedPhoneNumUseCase.AREA_CODE
            }

            // Update input value
            valueEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.inputValue = text.toString()
            }
        }


        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(navArgs.property.title)
            .setView(binding?.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                // Check if input is valid
                if (!viewModel.isInputValid()) {
                    showShortToast(getString(R.string.profile_edit_field_input_invalid))
                    return@setPositiveButton
                }

                // Send back input value
                findNavController().previousBackStackEntry?.savedStateHandle?.set(
                    navArgs.property.id,
                    viewModel.inputValue
                )

                findNavController(R.id.textInputDialog)?.navigateUp()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                findNavController(R.id.textInputDialog)?.navigateUp()
            }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}