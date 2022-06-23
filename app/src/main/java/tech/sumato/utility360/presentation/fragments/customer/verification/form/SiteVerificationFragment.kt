package tech.sumato.utility360.presentation.fragments.customer.verification.form

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.google.android.material.snackbar.Snackbar
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import com.sumato.etrack_agri.ui.utils.PlaceHolderDrawableHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.tasks.SiteVerificationTaskRequest
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.databinding.SiteVerificationFragmentBinding
import tech.sumato.utility360.presentation.activity.camera.CapturedResult
import tech.sumato.utility360.presentation.activity.camera.CustomCameraContract
import tech.sumato.utility360.presentation.activity.camera.utils.compressBitmap
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivity
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivityViewModel
import tech.sumato.utility360.presentation.fragments.customer.verification.submission.SiteVerificationSubmissionFragment
import tech.sumato.utility360.utils.*
import java.io.File
import java.util.*

@AndroidEntryPoint
class SiteVerificationFragment : Fragment() {

    private var binding_: SiteVerificationFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<CustomerVerificationActivityViewModel>()

    private val siteVerificationTaskObject = SiteVerificationTaskRequest()


    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                //not granted
                return@registerForActivityResult
            }
            //granted , open camera
        }

    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = results.all { it.value }
        }

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val allGranted = results.all { it.value }
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
    private var listingPosition: Int = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerResource =
            arguments?.getParcelable("data") ?: throw Exception("Customer data invalid")

        listingPosition = arguments?.getInt("position") ?: 0

        siteVerificationTaskObject.customerUuid = customerResource!!.id!!

        renderCustomerDetails()

        binding.submitBtn.setOnClickListener {

            getFormData()

            siteVerificationTaskObject.uploadableImagePath = rawImageFilePath

            if (!siteVerificationTaskObject.validate()) {
                showSnackbar("Fill all the fields and try again !")
                return@setOnClickListener
            }

            /**
             * as all the submit process will be shown in another
             * fragment, thus it needs to navigate to that fragment
             * and also invoke submission to view model
             */
            navigateAndSubmit()

        }

        binding.siteImageView.setOnClickListener {
            openCameraPreview()
        }


    }

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


    private fun renderCustomerDetails() {
        binding.apply {
            binding.titleTextView.text = customerResource!!.name
            binding.secondaryTextView.text = customerResource!!.pbg_id

            binding.siteImageView.setImageURI(File(siteVerificationTaskObject.uploadableImagePath).toUri())

            Glide.with(requireContext())
                .asBitmap()
                .load(customerResource?.photo)
                .placeholder(
                    PlaceHolderDrawableHelper.getAvatar(
                        requireContext(),
                        customerResource!!.name,
                        listingPosition
                    )
                )
                .into(avatarView)

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


    private fun getFormData() {

        siteVerificationTaskObject.site_address_verified = binding.addressVerifiedSwitch.isChecked
        siteVerificationTaskObject.commercial = binding.commercialUsageCheckbox.isChecked
        siteVerificationTaskObject.nearest_point = binding.nearestPointField.text.toString()
        siteVerificationTaskObject.floor = binding.connectionFloorField.text.toString()
        siteVerificationTaskObject.remarks = binding.remarksField.text.toString()
        siteVerificationTaskObject.site_feasibility = binding.recommendedCheckbox.isChecked

    }


    private fun navigateAndSubmit() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.navigate(fragment = SiteVerificationSubmissionFragment::class.java)

            viewModel.submitVerification(
                paramsObject = siteVerificationTaskObject
            )
        }
    }


    private fun openCameraPreview() {
        //check required permissions
        checkPermissions()
    }


    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }



    private fun <T> getParentActivity(): T {
        return requireActivity() as T
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


    private fun openCameraOrImagePicker() {
        openImageLauncher.launch(options {
            setGuidelines(CropImageView.Guidelines.ON)
        })
    }

    private fun checkPermissions() {

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                //all permission granted
                openCameraOrImagePicker()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA
            ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) -> {
                //
                showPermissionsRationalDialog()
            }

            else -> {
                if (checkIfAlreadyAskedPermission()) {
                    //all permissions already asked, show already asked dialog
                    showPermissionsAlreadyAskedDialog()
                    return
                }
                showPermissionsRationalDialog()
            }

        }

    }

    private val requiredPermissions = mapOf(
        CAMERA_PERMISSION to Manifest.permission.CAMERA,
        STORAGE_PERMISSION to Manifest.permission.READ_EXTERNAL_STORAGE,
        STORAGE_PERMISSION to Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun checkIfAlreadyAskedPermission(): Boolean {
        return requiredPermissions.map { permission ->
            checkIfAlreadyAskedPermission(
                getParentActivity<CustomerVerificationActivity>().sharedPreferences,
                permission.key
            )
        }.all { it }
    }

    private fun askRequiredPermissions() {
        permissionsLauncher.launch(
            requiredPermissions.values.toTypedArray()
        )
    }

    private fun showPermissionsRationalDialog() {
        showDialog(
            title = getString(R.string.svf_permissionsRationalTitle),
            message = getString(R.string.svf_permissionsRationalMessage),
            showNegativeBtn = true,
            positiveBtn = getString(R.string.svf_permissionsRationalPositiveBtn),
            negativeBtn = getString(R.string.svf_permissionsRationalNegativeBtn),
            cancellable = false
        ) { d, i ->
            d.dismiss()
            when (i) {
                POSITIVE_BTN -> {
                    askRequiredPermissions()
                }
                NEGATIVE_BTN -> {
                    //
                }
            }

        }.show()
    }

    private fun showPermissionsAlreadyAskedDialog() {
        showDialog(
            title = getString(R.string.svf_permissionsAlreadyAskedTitle),
            message = getString(R.string.svf_permissionsAlreadyAskedMessage),
            showNegativeBtn = true,
            positiveBtn = getString(R.string.appSettings),
            negativeBtn = getString(R.string.svf_permissionsAlreadyAskedNegativeBtn),
            cancellable = false
        ) { d, i ->
            d.dismiss()
            when (i) {
                POSITIVE_BTN -> {
                    appSettings()
                }
                NEGATIVE_BTN -> {
                    //
                }
            }

        }.show()
    }


}