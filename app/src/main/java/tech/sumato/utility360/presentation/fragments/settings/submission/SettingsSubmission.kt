package tech.sumato.utility360.presentation.fragments.settings.submission

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.presentation.activity.settings.SettingsActivityViewModel
import tech.sumato.utility360.presentation.fragments.progress.post_submit.PostSubmitProgressFragment
import tech.sumato.utility360.presentation.utils.ProcessStatus

@AndroidEntryPoint
class SettingsSubmission : PostSubmitProgressFragment() {


    private val viewModel by activityViewModels<SettingsActivityViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusInfo(infoText = "Your request is being processed ")

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.postSubmitProcessChannel.collect { postSubmitData ->
                    setStatusInfo(infoText = postSubmitData.message)

                    when (postSubmitData.processStatus) {
                        is ProcessStatus.LOADING -> {
                            showProgressbar()
                        }
                        is ProcessStatus.Completed -> {
                            hideProgressbar()
                            if (postSubmitData.processStatus.status == Status.FAILED) {
                                //failed
                                showPrimaryBtn(
                                    label = "Retry",
                                    onClick = {

                                    }
                                )
                            } else {
                                //success
                                showPrimaryBtn(
                                    label = "Got it",
                                    onClick = {
                                        requireActivity().onBackPressed()
                                    }
                                )
                            }
                        }
                        else -> {

                        }
                    }
                }

            }
        }


    }


}