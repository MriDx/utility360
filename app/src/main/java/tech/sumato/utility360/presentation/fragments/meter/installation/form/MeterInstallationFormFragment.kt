package tech.sumato.utility360.presentation.fragments.meter.installation.form

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
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
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivity
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivity
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivityViewModel
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivity
import tech.sumato.utility360.presentation.fragments.meter.installation.submission.MeterInstallationSubmissionFragment
import tech.sumato.utility360.presentation.fragments.progress.post_submit.PostSubmitProgressFragment
import tech.sumato.utility360.utils.*
import java.io.File
import java.util.*

@AndroidEntryPoint
class MeterInstallationFormFragment : Fragment() {


    private val viewModel by activityViewModels<MeterInstallationActivityViewModel>()

    private var binding_: MeterInstallationFragmentBinding? = null
    private val binding get() = binding_!!


    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                //not granted
                return@registerForActivityResult
            }
            //granted , open camera
        }

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

    private val meterImageLauncher = registerForActivityResult(CropImageContract()) { cropResult ->
        if (cropResult.isSuccessful) {
            val imagePath = cropResult.getUriFilePath(requireContext())
            val file = File(imagePath)
            if (file.exists()) {
                addWatermarks(filePath = file.path, type = "meter_image")
            }
        }
    }

    private val siteImageLauncher = registerForActivityResult(CropImageContract()) { cropResult ->
        if (cropResult.isSuccessful) {
            val imagePath = cropResult.getUriFilePath(requireContext())
            val file = File(imagePath)
            if (file.exists()) {
                addWatermarks(filePath = file.path, type = "site_image")
            }
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
            //meterImageCaptureLauncher.launch(CaptureOptions())
            checkCameraPermission(ifAllowed = {
                meterImageLauncher.launch(options {
                    setGuidelines(CropImageView.Guidelines.ON)
                })
            })

        }

        binding.siteImageView.setOnClickListener {
            //siteImageCaptureLauncher.launch(CaptureOptions())
            checkCameraPermission(ifAllowed = {
                siteImageLauncher.launch(options {
                    setGuidelines(CropImageView.Guidelines.ON)
                })
            })

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


    private fun addWatermarks(filePath: String, type: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val file = File(filePath)
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
                maxHeight = 980f,
                maxWidth = 980f
            ) ?: throw Exception("Could not processed image")

            val compressedBitmap =
                compressBitmap(bitmap = processedBitmap, maxHeight = 980f, maxWidth = 980f)
                    ?: processedBitmap

            saveAndShowProcessedBitmap(bitmap = compressedBitmap, type = type)

        }
    }

    private fun addWatermarks(capturedResult: CapturedResult, type: String) {
        addWatermarks(capturedResult.file, type)
        /*viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
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

        }*/
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


    private fun checkCameraPermission(ifAllowed: () -> Unit) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                //
                //openCameraCapture.launch(CaptureOptions())
                /*openImageLauncher.launch(options {
                    setGuidelines(CropImageView.Guidelines.ON)
                })*/
                ifAllowed.invoke()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) -> {
                //show rational
                showCameraPermissionRational()
            }
            else -> {
                if (checkIfAlreadyAskedPermission(
                        (requireActivity() as MeterInstallationActivity).sharedPreferences,
                        CAMERA_PERMISSION
                    )
                ) {
                    //permission already asked
                    showCameraPermissionAlreadyAsked()
                    return
                }
                //ask permission
                showCameraPermissionRational()
                //askCameraPermission()
            }
        }
    }

    private fun showCameraPermissionRational() {
        showDialog(
            title = getString(R.string.mif_cameraPermissionTitle),
            message = getString(R.string.mif_cameraPermissionMessage),
            showNegativeBtn = true,
            positiveBtn = getString(R.string.mif_cameraPermissionPositiveBtn),
            negativeBtn = getString(R.string.mif_cameraPermissionNegativeBtn),
            cancellable = false
        ) { d, i ->
            d.dismiss()
            when (i) {
                POSITIVE_BTN -> {
                    askCameraPermission()
                }
                NEGATIVE_BTN -> {
                    //
                }
            }

        }.show()
    }

    private fun showCameraPermissionAlreadyAsked() {
        showDialog(
            title = getString(R.string.mif_cameraPermissionAlreadyAskedTitle),
            message = getString(R.string.mif_cameraPermissionAlreadyAskedMessage),
            positiveBtn = getString(R.string.appSettings),
            negativeBtn = getString(R.string.mif_cameraPermissionAlreadyAskedNegativeBtn),
            showNegativeBtn = true,
            cancellable = false
        ) { d, i ->
            d.dismiss()
            when (i) {
                POSITIVE_BTN -> {
                    appSettings()
                }
                NEGATIVE_BTN -> {
                    handleCameraPermissionDecline()
                }
            }
        }.show()
    }

    private fun handleCameraPermissionDecline() {

    }

    private fun askCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        setPermissionAskedPreference(
            getParentActivity<MeterInstallationActivity>().sharedPreferences,
            CAMERA_PERMISSION
        )
    }

    private fun <T> getParentActivity(): T {
        return requireActivity() as T
    }

}