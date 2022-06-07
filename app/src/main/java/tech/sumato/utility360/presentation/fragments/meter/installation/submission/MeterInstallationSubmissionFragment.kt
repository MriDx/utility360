package tech.sumato.utility360.presentation.fragments.meter.installation.submission

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.utils.Status
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivityViewModel
import tech.sumato.utility360.presentation.fragments.progress.post_submit.PostSubmitProgressFragment
import tech.sumato.utility360.presentation.utils.ProcessStatus

@AndroidEntryPoint
class MeterInstallationSubmissionFragment : PostSubmitProgressFragment() {


    private val viewModel by activityViewModels<MeterInstallationActivityViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.startPendingJob()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusInfo(infoText = getString(R.string.misf_requestInProcess))

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.postSubmitProcessChannel.collect { postSubmitData ->
                        setStatusInfo(infoText = postSubmitData.message)

                        when (postSubmitData.processStatus) {
                            is ProcessStatus.LOADING -> {
                                //loading
                                showProgressbar()
                            }
                            is ProcessStatus.Completed -> {
                                hideProgressbar()
                                if (postSubmitData.processStatus.status == Status.FAILED) {
                                    //failed
                                    showPrimaryBtn(
                                        label = getString(R.string.misf_postFailureBtn),
                                        onClick = {
                                            //handle retry
                                            //viewModel.readySubmitJob()
                                        })
                                } else {
                                    //success
                                    showPrimaryBtn(
                                        label = getString(R.string.misf_postSuccessBtn),
                                        onClick = {
                                            //handle got it
                                            requireActivity().onBackPressed()
                                        })
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


}