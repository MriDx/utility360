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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import com.mridx.watermarkdialog.Data
import com.mridx.watermarkdialog.Processor
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.MeterReadingFragmentBinding
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.presentation.activity.camera.CaptureOptions
import tech.sumato.utility360.presentation.activity.camera.CapturedResult
import tech.sumato.utility360.presentation.activity.camera.CustomCamera
import tech.sumato.utility360.presentation.activity.camera.CustomCameraContract
import tech.sumato.utility360.presentation.activity.camera.utils.compressBitmap
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivity
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel
import tech.sumato.utility360.utils.*
import java.io.File
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

    var primaryDetails = emptyMap<String, String>()
    var secondaryDetails = emptyMap<String, String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        primaryDetails =
            (arguments?.get("primaryDetails") as? Map<String, String>) ?: throw Exception("")
        secondaryDetails =
            (arguments?.get("secondaryDetails") as? Map<String, String>) ?: throw Exception("")

        binding.titleTextView.text = primaryDetails["name"].toString()
        binding.secondaryTextView.text = primaryDetails["id"].toString()

        val allowedParams = arrayOf("Meter Id", "Previous reading", "Previous reading on")

        secondaryDetails.forEach { item ->
            if (allowedParams.contains(item.key)) {
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

        binding.meterImageView.setOnClickListener {
            openCameraPreview()
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
                openCameraCapture.launch(CaptureOptions())
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
                        "Customer Id" to primaryDetails["id"].toString(),
                        "Meter Id" to secondaryDetails["Meter Id"].toString(),
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
            title = "Allow Camera access ?",
            message = "Allow Camera permission to access and capture Meter image. To complete meter reading process uploading of meter image is compulsory.",
            showNegativeBtn = true,
            positiveBtn = "Continue",
            negativeBtn = "Cancel",
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
            title = "Allow use of Camera Permission",
            message = "It seems you have been asked to allow Camera permission already and you denied. \nAs it is really important to upload Meter image for Meter Reading, you can allow Camera permission from App settings and proceed Meter reading process.",
            positiveBtn = "App settings",
            negativeBtn = "Cancel",
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
    private fun submitMeterReading() {
        viewModel.submitMeterReading()
    }

}