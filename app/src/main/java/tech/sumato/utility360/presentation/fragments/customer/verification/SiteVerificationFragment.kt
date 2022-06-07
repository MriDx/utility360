package tech.sumato.utility360.presentation.fragments.customer.verification

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.snackbar.Snackbar
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.tasks.SiteVerificationTaskRequest
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.databinding.SiteVerificationFragmentBinding
import tech.sumato.utility360.presentation.activity.camera.CaptureOptions
import tech.sumato.utility360.presentation.activity.camera.CapturedResult
import tech.sumato.utility360.presentation.activity.camera.CustomCameraContract
import tech.sumato.utility360.presentation.activity.camera.utils.compressBitmap
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivity
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivityViewModel
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivity
import tech.sumato.utility360.presentation.fragments.customer.verification.submission.SiteVerificationSubmissionFragment
import tech.sumato.utility360.presentation.fragments.meter.installation.submission.MeterInstallationSubmissionFragment
import tech.sumato.utility360.utils.*
import java.io.File
import java.util.*

@AndroidEntryPoint
class SiteVerificationFragment : Fragment() {

    private var binding_: SiteVerificationFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<CustomerVerificationActivityViewModel>()

    private val siteVerificationTaskRequest = ObservableField(SiteVerificationTaskRequest())

    private val siteVerificationTaskObject = SiteVerificationTaskRequest()


    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                //not granted
                return@registerForActivityResult
            }
            //granted , open camera
        }

    private val openCameraCapture = registerForActivityResult(CustomCameraContract()) {
        if (it.success) {
            //show captured image
            //binding.meterImageView.setImageURI(File(it.file).toUri())
            addWatermarks(capturedResult = it)
        }
    }

    private val openImageLauncher = registerForActivityResult(CropImageContract()) { cropResult ->
        if (cropResult.isSuccessful) {
            val imagePath = cropResult.getUriFilePath(requireContext())
            val file = File(imagePath)
            if (file.exists()) {
                addWatermarks(filePath = file.path)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = SiteVerificationFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
            viewModel = this@SiteVerificationFragment.viewModel
            siteVerificationRequest = this.siteVerificationRequest

        }
        return binding.root
    }

    private var customerResource: CustomerResource? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerResource =
            arguments?.getParcelable("data") ?: throw Exception("Customer data invalid")

        //siteVerificationTaskRequest.get()!!.customerUuid = customerResource!!.id!!
        siteVerificationTaskObject.customerUuid = customerResource!!.id!!

        renderCustomerDetails()

        binding.submitBtn.setOnClickListener {

            getFormData()

            navigateAndSubmit()


/*
            siteVerificationTaskObject.uploadableImagePath = rawImageFilePath

            if (!siteVerificationTaskObject.validate()) {
                //
                showSnackbar(message = "Fill all the fields and try again !")
                return@setOnClickListener
            }

            Log.d("mridx", "onViewCreated: ${siteVerificationTaskObject.toJson()}")

            navigateAndSubmit()*/


            /*siteVerificationTaskRequest.get()!!.uploadableImagePath = rawImageFilePath
            viewModel.submitVerification(
                paramsObject = siteVerificationTaskRequest.get()!!
            )
            viewModel.navigate(fragment = SiteVerificationSubmissionFragment::class.java)*/
        }

        binding.siteImageView.setOnClickListener {
            openCameraPreview()
        }


    }

    private fun navigateAndSubmit() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.navigate(fragment = SiteVerificationSubmissionFragment::class.java)

            /*viewModel.submitVerification(
                paramsObject = siteVerificationTaskObject
            )*/
        }
    }

    private fun getFormData() {

        siteVerificationTaskObject.site_address_verified = binding.addressVerifiedSwitch.isChecked
        siteVerificationTaskObject.commercial = binding.commercialUsageCheckbox.isChecked
        siteVerificationTaskObject.nearest_point = binding.nearestPointField.text.toString()
        siteVerificationTaskObject.floor = binding.connectionFloorField.text.toString()
        siteVerificationTaskObject.remarks = binding.remarksField.text.toString()
        siteVerificationTaskObject.site_feasibility = binding.recommendedCheckbox.isChecked

    }

    private fun openCameraPreview() {
        //check camera permission
        //ask camera permission if not granted
        checkCameraPermission()

    }

    private fun renderCustomerDetails() {
        binding.apply {
            binding.titleTextView.text = customerResource!!.name
            binding.secondaryTextView.text = customerResource!!.pbg_id

            customerResource!!.getSecondaryDetailsMap().forEach { item ->
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

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                //
                //openCameraCapture.launch(CaptureOptions())
                openImageLauncher.launch(options {
                    setGuidelines(CropImageView.Guidelines.ON)
                })
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
                        (requireActivity() as MeterReadingActivity).sharedPreferences,
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

    private fun addWatermarks(filePath: String) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val file = File(filePath)
            val processedBitmap = Processor.process(
                file = file,
                waterMarkData = Data.WaterMarkData(
                    waterMarks = mapOf(
                        "Customer name" to customerResource!!.name!!,
                        "Id" to customerResource!!.pbg_id!!,
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

            saveAndShowProcessedBitmap(bitmap = compressedBitmap)

        }
    }

    private fun addWatermarks(capturedResult: CapturedResult) {
        addWatermarks(filePath = capturedResult.file)
        /* viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
             val file = File(capturedResult.file)
             val processedBitmap = Processor.process(
                 file = file,
                 waterMarkData = Data.WaterMarkData(
                     waterMarks = mapOf(
                         "Customer name" to customerResource!!.name!!,
                         "Id" to customerResource!!.pbg_id!!,
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

             saveAndShowProcessedBitmap(bitmap = compressedBitmap)

         }*/
    }

    private var rawImageFilePath = ""
    private suspend fun saveAndShowProcessedBitmap(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val savedFile = storeImage(image = bitmap, pictureFile = createPictureFile())
                ?: throw Exception("could not save file")
            rawImageFilePath = savedFile.path
            withContext(Dispatchers.Main) {
                showProcessedImage(savedFile)
            }

        }

    }

    private fun showProcessedImage(file: File) {
        binding.siteImageView.setImageURI(file.toUri())
    }

    private fun createPictureFile(): File {
        val parentDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "site_verification_images"
        )
        if (parentDir.exists()) {
            parentDir.deleteRecursively()
        }
        parentDir.mkdirs()

        return File(
            parentDir,
            "${Date().time}.jpg"
        )
    }

    private fun askCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        setPermissionAskedPreference(
            getParentActivity<CustomerVerificationActivity>().sharedPreferences,
            CAMERA_PERMISSION
        )
    }

    private fun <T> getParentActivity(): T {
        return requireActivity() as T
    }


    private fun showCameraPermissionRational() {
        showDialog(
            title = getString(R.string.mrf_cameraPermissionTitle),
            message = getString(R.string.mrf_cameraPermissionMessage),
            showNegativeBtn = true,
            positiveBtn = getString(R.string.mrf_cameraPermissionPositiveBtn),
            negativeBtn = getString(R.string.meterReadingLocationPermissionNegativeBtn),
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
            title = getString(R.string.mrf_cameraPermissionAlreadyAskedTitle),
            message = getString(R.string.mrf_cameraPermissionAlreadyAskedMessage),
            positiveBtn = getString(R.string.appSettings),
            negativeBtn = getString(R.string.mrf_cameraPermissionAlreadyAskedNegativeBtn),
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

}