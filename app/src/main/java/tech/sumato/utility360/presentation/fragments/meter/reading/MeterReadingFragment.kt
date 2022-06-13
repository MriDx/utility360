package tech.sumato.utility360.presentation.fragments.meter.reading

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withCreated
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import com.sumato.etrack_agri.ui.utils.PlaceHolderDrawableHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.meter.MeterReadingResource
import tech.sumato.utility360.data.remote.model.tasks.MeterReadingTaskRequest
import tech.sumato.utility360.databinding.MeterReadingFragmentBinding
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.presentation.activity.camera.CaptureOptions
import tech.sumato.utility360.presentation.activity.camera.CapturedResult
import tech.sumato.utility360.presentation.activity.camera.CustomCamera
import tech.sumato.utility360.presentation.activity.camera.CustomCameraContract
import tech.sumato.utility360.presentation.activity.camera.utils.compressBitmap
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivity
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel
import tech.sumato.utility360.presentation.fragments.meter.reading.submission.MeterReadingSubmissionFragment
import tech.sumato.utility360.utils.*
import java.io.File
import java.text.DateFormat
import java.time.Instant
import java.util.*

@AndroidEntryPoint
class MeterReadingFragment : Fragment() {

    private var binding_: MeterReadingFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<MeterReadingActivityViewModel>()


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

    private var customerResource: CustomerResource? = null
    private var meterReadingTaskRequest = MeterReadingTaskRequest()
    private var listingPosition = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<MeterReadingFragmentBinding?>(
            inflater,
            R.layout.meter_reading_fragment,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerResource =
            arguments?.getParcelable("data") ?: throw Exception("Customer data invalid")
        listingPosition = arguments?.getInt("position") ?: 0

        meterReadingTaskRequest.customerUuid = customerResource!!.id!!


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    /*viewModel.lastMeterReadingFlow.collectLatest { customerResourceResponse ->
                        //show last meter reading
                        showLastMeterReading(customerResourceResponse.user!!.lastMeterReading!!)
                    }*/
                }
            }
        }


        //fetch last meter reading
        //viewModel.fetchLastMeterReading(uuid = customerResource!!.id!!)


        renderCustomerDetails()



        binding.meterImageView.setOnClickListener {
            openCameraPreview()
        }

        binding.meterReadingSubmitBtn.setOnClickListener {
            getFormData()
            if (!meterReadingTaskRequest.validate()) {
                //
                showSnackbar("Please fill all the fields")
                return@setOnClickListener
            }
            navigateAndSubmit()
        }

    }


    private fun navigateAndSubmit() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.navigate(MeterReadingSubmissionFragment::class.java)
            viewModel.submitMeterReading(meterReadingTaskRequest = meterReadingTaskRequest)
        }
    }

    private fun getFormData() {
        meterReadingTaskRequest.meter_readings = binding.meterReadingField.getOTP().toMeterReading()
        //meterReadingTaskRequest.date_of_billing = "2022-08-12"
        meterReadingTaskRequest.date_of_billing = Date().toMeterReadingDate()
        //DateFormat.getDateInstance(DateFormat.SHORT).format(Date())

    }

    private fun renderCustomerDetails() {
        binding.apply {
            binding.titleTextView.text = customerResource!!.name
            binding.secondaryTextView.text = customerResource!!.pbg_id

            binding.meterImageView.setImageURI(File(meterReadingTaskRequest.uploadableImagePath).toUri())

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

            customerResource!!.getSecondaryDetailsForMeterReading().forEach { item ->
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


    private fun openCameraPreview() {
        //check camera permission
        //ask camera permission if not granted
        checkCameraPermission()

    }


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                //
                openCameraCapture.launch(CaptureOptions(overlay = R.drawable.overlay))
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


    private fun addWatermarks(capturedResult: CapturedResult) {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val file = File(capturedResult.file)
            val processedBitmap = Processor.process(
                file = file,
                waterMarkData = Data.WaterMarkData(
                    waterMarks = mapOf(
                        "Customer name" to customerResource!!.name!!,
                        //"Meter Id" to secondaryDetails["Meter Id"].toString(),
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

        }
    }

    private suspend fun saveAndShowProcessedBitmap(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val savedFile = storeImage(image = bitmap, pictureFile = createPictureFile())
                ?: throw Exception("could not save file")
            meterReadingTaskRequest.uploadableImagePath = savedFile.path
            withContext(Dispatchers.Main) {
                showProcessedImage(savedFile)
            }

        }

    }

    private fun showProcessedImage(file: File) {
        binding.meterImageView.setImageURI(file.toUri())
    }

    private fun createPictureFile(): File {
        val parentDir = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "meter_images"
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

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
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

    private fun askCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        setPermissionAskedPreference(
            getParentActivity<MeterReadingActivity>().sharedPreferences,
            CAMERA_PERMISSION
        )
    }

    private fun <T> getParentActivity(): T {
        return (requireActivity() as MeterReadingActivity) as T
    }


    /**
     * submit meter submit action handler
     * sends event to viewModel
     * viewmodel prepares required information and handles further
     */
    /*private fun submitMeterReading() {
        viewModel.submitMeterReading()
    }
*/

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}