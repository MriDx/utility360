package tech.sumato.utility360.presentation.fragments.settings.account.password

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.remote.model.user.ChangePasswordRequest
import tech.sumato.utility360.databinding.ChangePasswordFragmentBinding
import tech.sumato.utility360.presentation.activity.settings.SettingsActivityViewModel
import tech.sumato.utility360.presentation.fragments.settings.submission.SettingsSubmission
import tech.sumato.utility360.utils.parentLayout

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var binding_: ChangePasswordFragmentBinding? = null
    private val binding get() = binding_!!

    private var changePasswordRequest = ChangePasswordRequest()

    private val viewModel by activityViewModels<SettingsActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = ChangePasswordFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.submitBtn.setOnClickListener {

            handleSubmit()

        }


    }

    private fun handleSubmit() {
        getFormData()

        if (!changePasswordRequest.validate()) {
            //
            showErrors(errors = changePasswordRequest.errors)
            return
        }

        //submit

        Log.d("mridx", "handleSubmit: ${changePasswordRequest.toJson()}")

        navigateAndSubmit()


    }

    private fun navigateAndSubmit() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.navigate(SettingsSubmission::class.java)
            viewModel.changePassword(passwordChangeRequest = changePasswordRequest)
        }
    }

    private fun showErrors(errors: MutableMap<String, String?>) {
        errors.forEach {
            when (it.key) {
                "current_password" -> {
                    binding.currentPasswordField.parentLayout().error = it.value
                }
                "new_password" -> {
                    binding.newPasswordField.parentLayout().error = it.value
                }
                "confirm_new_password" -> {
                    binding.confirmNewPasswordField.parentLayout().error = it.value
                }
            }
        }
    }


    private fun getFormData() {
        changePasswordRequest.currentPassword = binding.currentPasswordField.text?.toString() ?: ""
        changePasswordRequest.newPassword = binding.newPasswordField.text?.toString() ?: ""
        changePasswordRequest.confirmNewPassword =
            binding.confirmNewPasswordField.text?.toString() ?: ""
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}