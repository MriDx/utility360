package tech.sumato.utility360.presentation.fragments.meter.installation.form

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.tasks.MeterInstallationTaskRequest
import tech.sumato.utility360.databinding.MeterInstallationFragmentBinding
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.presentation.activity.camera.CaptureOptions
import tech.sumato.utility360.presentation.activity.camera.CapturedResult
import tech.sumato.utility360.presentation.activity.camera.CustomCameraContract
import tech.sumato.utility360.presentation.activity.camera.utils.compressBitmap
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivityViewModel
import tech.sumato.utility360.presentation.fragments.meter.installation.submission.MeterInstallationSubmissionFragment
import tech.sumato.utility360.presentation.fragments.progress.post_submit.PostSubmitProgressFragment
import tech.sumato.utility360.utils.storeImage
import java.io.File
import java.util.*

@AndroidEntryPoint
class MeterInstallationFormFragment : Fragment() {


    private val viewModel by activityViewModels<MeterInstallationActivityViewModel>()

    private var binding_: MeterInstallationFragmentBinding? = null
    private val binding get() = binding_!!


    private val meterImageCaptureLauncher =
        registerForActivityResult(CustomCameraContract()) { result ->
            if (result.success) {
                //val capturedImage = File(result.file)
                addWatermarks(capturedResult = result, type = "meter_image")
                /*meterInstallationTaskRequest.uploadableMeterPhoto = capturedImage.path
                binding.meterImageView.setImageURI(capturedImage.toUri())*/
            }
        }

    private val siteImageCaptureLauncher =
        registerForActivityResult(CustomCameraContract()) { result ->
            if (result.success) {
                //val capturedImage = File(result.file)
                addWatermarks(capturedResult = result, type = "site_image")
                /*meterInstallationTaskRequest.uploadableSitePhoto = capturedImage.path
                binding.siteImageView.setImageURI(capturedImage.toUri())*/
            }
        }

    private lateinit var customerResource: CustomerResource

    private var meterInstallationTaskRequest = MeterInstallationTaskRequest()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = MeterInstallationFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
            viewModel = this@MeterInstallationFormFragment.viewModel
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerResource = arguments?.getParcelable<CustomerResource>("data")
            ?: throw Exception("invalid customer data")

        meterInstallationTaskRequest.customerUuid = customerResource.id!!

        renderCustomerDetails()


        binding.extraPipeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            binding.extraPipeInfoView.isVisible = b
        }

        binding.pipeLengthSlider.addOnChangeListener { slider, value, fromUser ->
            binding.totalPipeInvloved.text = "${value.toInt()} meter"
        }

        binding.pipeLengthSlider.setLabelFormatter {
            getString(R.string.mif_sliderLabel, it.toInt())
        }


        binding.meterImageView.setOnClickListener {
            meterImageCaptureLauncher.launch(CaptureOptions())
        }

        binding.siteImageView.setOnClickListener {
            siteImageCaptureLauncher.launch(CaptureOptions())
        }

        binding.submitBtn.setOnClickListener {
            getFormData()

            if (!meterInstallationTaskRequest.validate()) {
                //
                return@setOnClickListener
            }

            navigateAndSubmit()
        }


    }

    private fun navigateAndSubmit() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.navigate(fragment = MeterInstallationSubmissionFragment::class.java)

            viewModel.submitMeterInstallation(meterInstallationTaskRequest = meterInstallationTaskRequest)
        }
    }

    private fun getFormData() {
        meterInstallationTaskRequest.meter_serial_no = binding.meterSerialField.text.toString()
        meterInstallationTaskRequest.initial_meter_reading =
            binding.initialMeterReadingField.text.toString()
        meterInstallationTaskRequest.extraPipeLength =
            binding.pipeLengthSlider.value.toInt().toString()
    }

    private fun renderCustomerDetails() {
        binding.apply {
            binding.titleTextView.text = customerResource.name
            binding.secondaryTextView.text = customerResource.pbg_id

            customerResource.getSecondaryDetailsMap().forEach { item ->
                val secondaryItemView = DataBindingUtil.inflate<ProfileInfoItemViewBinding>(
                    LayoutInflater.from(requireContext()),
                    R.layout.profile_info_item_view,
                    binding.customerInfoHolder,
                    false
                ).apply {
                    headingView.text = item.key
                    valueView.text = item.value
                }.root
                binding.customerInfoHolder.addView(secondaryItemView)
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }


    private fun addWatermarks(capturedResult: CapturedResult, type: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val file = File(capturedResult.file)
            val processedBitmap = Processor.process(
                file = file,
                waterMarkData = Data.WaterMarkData(
                    waterMarks = mapOf(
                        "Customer name" to customerResource.name!!,
                        "Id" to customerResource.pbg_id!!,
                        "Uploaded via" to getString(R.string.app_name)
                    ),
                    position = Data.WaterMarkPosition.BOTTOM_LEFT
                ),
                maxHeight = 1080f,
                maxWidth = 1080f
            ) ?: throw Exception("Could not processed image")

            val compressedBitmap =
                compressBitmap(bitmap = processedBitmap, maxHeight = 1080f, maxWidth = 1080f)
                    ?: processedBitmap

            saveAndShowProcessedBitmap(bitmap = compressedBitmap, type = type)

        }
    }

    private suspend fun saveAndShowProcessedBitmap(bitmap: Bitmap, type: String) {
        withContext(Dispatchers.IO) {
            val savedFile = storeImage(image = bitmap, pictureFile = createPictureFile())
                ?: throw Exception("could not save file")

            withContext(Dispatchers.Main) {
                when (type) {
                    "meter_image" -> {
                        meterInstallationTaskRequest.uploadableMeterPhoto = savedFile.path
                        binding.meterImageView.setImageURI(savedFile.toUri())
                    }
                    "site_image" -> {
                        meterInstallationTaskRequest.uploadableSitePhoto = savedFile.path
                        binding.siteImageView.setImageURI(savedFile.toUri())
                    }
                }
            }


        }
    }

    private fun createPictureFile(): File {
        val parentDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "meter_installation_image"
        )
        /*if (parentDir.exists()) {
            parentDir.deleteRecursively()
        }*/
        parentDir.mkdirs()

        return File(
            parentDir,
            "${Date().time}.jpg"
        )
    }

}