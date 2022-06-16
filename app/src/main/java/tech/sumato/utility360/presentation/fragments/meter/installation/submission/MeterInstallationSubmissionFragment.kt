package tech.sumato.utility360.presentation.fragments.meter.installation.submission

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
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

    private val qrScannerOptions
        get() = ScanOptions()
            .setPrompt("Scan a Meter QR code")
            .setBeepEnabled(true)

    private val qrScannerLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                showSnackbar(message = "Invalid QR Scanned !")
            } else {
                //
                val qr = result.contents
                handleQrScanned(qrData = qr)
            }
        }


    private fun handleQrScanned(qrData: String) {
        // http://pbg-test.sumato.tech/qrcodes/qrdata
        //todo domain to be dynamic
        val qrSplitted = qrData.split("/")
        if (qrSplitted.size != 5 || !qrSplitted[2].contentEquals("pbg-test.sumato.tech")) {
            showSnackbar(message = "Invalid QR Scanned !")
            return
        }
        //valid qr, let's submit
        viewModel.submitMeterQr(qrData = qrSplitted.last())
        //viewModel.emulateMeterQrSubmit(qrData = qrSplitted.last())

    }


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
                        Log.d(
                            "mridx",
                            "onViewCreated: state collected ${postSubmitData.processStatus}"
                        )
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
                            is ProcessStatus.CompletedWithException -> {
                                hideProgressbar()
                                when (postSubmitData.processStatus.status) {
                                    Status.SUCCESS -> {
                                        if (postSubmitData.processStatus.primaryBtn == null) {
                                            showPrimaryBtn(
                                                label = getString(R.string.misf_postSuccessBtn),
                                                onClick = {
                                                    //
                                                    requireActivity().onBackPressed()
                                                }
                                            )
                                        } else {
                                            showPrimaryBtn(
                                                label = postSubmitData.processStatus.primaryBtn,
                                                onClick = {
                                                    //
                                                    openQrScanner()
                                                }
                                            )
                                            showSecondaryBtn(
                                                label = getString(R.string.misf_postSuccessBtn),
                                                onClick = {
                                                    //
                                                    requireActivity().onBackPressed()
                                                }
                                            )

                                        }

                                    }
                                    Status.FAILED -> {
                                        showPrimaryBtn(
                                            label = getString(R.string.misf_postFailureBtn),
                                            onClick = {
                                                //
                                                requireActivity().onBackPressed()
                                            }
                                        )
                                    }
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


    private fun openQrScanner() {
        qrScannerLauncher.launch(qrScannerOptions)
    }


}