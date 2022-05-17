package tech.sumato.utility360.presentation.activity.camera

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.DrawableRes
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.CustomCameraUiBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity
import tech.sumato.utility360.presentation.activity.camera.utils.afterMeasured
import tech.sumato.utility360.presentation.activity.camera.utils.scaleBitmap
import tech.sumato.utility360.presentation.activity.camera.utils.setScreenBright
import tech.sumato.utility360.presentation.activity.camera.utils.toBitmap
import tech.sumato.utility360.utils.checkIfAlreadyAskedPermission
import tech.sumato.utility360.utils.storeImage
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint
class CustomCamera : BaseActivity() {

    private lateinit var binding: CustomCameraUiBinding

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    /*@Inject
    lateinit var sharedPreferences: SharedPreferences*/


    var camera: Camera? = null

    var outputFile: String? = null
    var errorMessage: String = ""

    var currentJob: Job? = null

    var flashOn = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView<CustomCameraUiBinding?>(this, R.layout.custom_camera_ui)
                .apply {
                    setLifecycleOwner { lifecycle }
                }

        window.setScreenBright()

        outputFile = intent?.getStringExtra("output_file")

        startCameraPreview()

        binding.captureBtn.setOnClickListener {
            capturedHandler()
        }

        binding.cancelBtn.setOnClickListener {
            currentJob?.cancel(CancellationException("Cancelled by user !"))
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        binding.flashBtn.setOnClickListener {
            camera ?: return@setOnClickListener
            if (flashOn) {
                //
                binding.flashBtn.setImageResource(R.drawable.ic_baseline_flash_on_24)
                flashOn = false
            } else {
                binding.flashBtn.setImageResource(R.drawable.ic_baseline_flash_off_24)
                flashOn = true
            }
            controlFlash()

        }


    }

    private fun controlFlash(enable: Boolean = flashOn) {
        camera ?: return
        camera!!.cameraControl.enableTorch(enable)
    }

    /*private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) -> {
                //show rational
            }
            else -> {
                if (checkIfAlreadyAskedPermission(sharedPreferences = sharedPreferences, ))
            }

        }
    }*/


    /**
     * binds camera preview with camera provider
     *
     */
    private fun startCameraPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }


    /**
     * initializes instance of ImageCapture
     * and store the instance in a local variable
     * as i will required capture image
     *
     * @return ImageCapture
     */
    private var imageCapture: ImageCapture? = null
    private fun buildImageCapture(): ImageCapture {
        if (imageCapture == null) {
            imageCapture = ImageCapture.Builder()
                .apply {
                    //setTargetResolution(Size(2100, 1080))
                    setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    //setTargetRotation(binding.cameraView.display.rotation)
                    setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                }.build()
        }
        return imageCapture!!
    }

    /**
     * builds and return an instance of Preview
     *
     * @return Preview
     */
    private fun buildPreview(): Preview {
        return Preview.Builder()
            .build()
            .apply {
                setSurfaceProvider(binding.cameraView.surfaceProvider)
            }
    }

    /**
     * builds and returns an instance of Camera selector
     *
     * @return CameraSelector
     */
    private fun buildCameraSelector(): CameraSelector {
        return CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

    }


    /**
     * binds the preview for camera view
     *
     * @param cameraProvider
     */
    private fun bindPreview(cameraProvider: ProcessCameraProvider) {

        /**
         * builds and stores the instance
         * to local variable
         */
        buildImageCapture()

        /**
         * this will bind camera provider
         * with lifecycle with preview
         * along with camera selector and image capture
         *
         * this generates an instance of Camera
         * and later this camera instance can be uses for
         * further operations like zoom, focus etc.
         */
        camera = cameraProvider.bindToLifecycle(
            this,
            buildCameraSelector(),
            buildPreview(),
            imageCapture
        )

        /**
         * starts listening on touch events for
         * tap-to-focus feature
         */
        binding.cameraView.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val factory: MeteringPointFactory = SurfaceOrientedMeteringPointFactory(
                        binding.cameraView.width.toFloat(), binding.cameraView.height.toFloat()
                    )
                    val autoFocusPoint = factory.createPoint(event.x, event.y)
                    try {
                        camera!!.cameraControl.startFocusAndMetering(
                            FocusMeteringAction.Builder(
                                autoFocusPoint,
                                FocusMeteringAction.FLAG_AF
                            ).apply {
                                //focus only when the user tap the preview
                                //disableAutoCancel()
                                //setAutoCancelDuration(2, TimeUnit.SECONDS)
                            }.build()
                        )
                    } catch (e: CameraInfoUnavailableException) {
                        e.printStackTrace()
                        Log.d("ERROR", "cannot access camera", e)
                    }
                    true
                }
                else -> false // Unhandled event.
            }
        }

        /**
         * this code will run after ui changes by any means
         * and tries to focus automatically
         */
        binding.cameraView.afterMeasured {
            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
                .createPoint(.5f, .5f)
            try {
                val autoFocusAction = FocusMeteringAction.Builder(
                    autoFocusPoint,
                    FocusMeteringAction.FLAG_AF
                ).apply {
                    //start auto-focusing after 2 seconds
                    setAutoCancelDuration(2, TimeUnit.SECONDS)
                }.build()
                camera!!.cameraControl.startFocusAndMetering(autoFocusAction)
            } catch (e: CameraInfoUnavailableException) {
                e.printStackTrace()
                Log.d("mridx", "cannot access camera", e)
            }
        }

    }

    /**
     * this will get called when capture icon is clicked
     *
     * this will capture an image with instance of ImageCapture
     * and later displays as preview
     */
    private fun capturedHandler() {

        imageCapture!!.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {

                override fun onCaptureSuccess(image: ImageProxy) {
                    super.onCaptureSuccess(image)
                    handleCapturedSuccess(imageProxy = image)
                }

                override fun onError(exception: ImageCaptureException) {
                    super.onError(exception)
                    exception.printStackTrace()
                }
            })

    }

    /**
     * if imageCapture captures an image successfully then this functions gets called
     * this functions basically converts the captured image from instance of ImageProxy
     * to bitmap and calls another function to show it as preview
     *
     * @param imageProxy instance of ImageProxy
     * @see ImageProxy
     */
    private fun handleCapturedSuccess(imageProxy: ImageProxy) {
        currentJob = lifecycleScope.launch(Dispatchers.IO) {

            //convert image proxy to bitmap
            val capturedBitmap = imageProxy.toBitmap()

            imageProxy.close()

            var height = 2100
            var width = 1080
            if (capturedBitmap.width > capturedBitmap.height) {
                //landscape image
                val tmp = width
                width = height
                height = tmp
            }

            val scaledBitmap = scaleBitmap(width = width, height = height, image = capturedBitmap)

            //show the converted bitmap in preview
            showPreview(bitmap = scaledBitmap)

        }
        return
    }

    /**
     * shows bitmap in image preview
     * and controls preview
     *
     * @param bitmap
     */
    private suspend fun showPreview(bitmap: Bitmap) {
        withContext(Dispatchers.Main) {
            binding.imagePreview.setImageBitmap(bitmap)
            binding.previewView.isVisible = true
            binding.captureView.isVisible = false

            controlFlash(enable = false)

            binding.done.setOnClickListener {
                //save the image and go back
                binding.progressBar.isVisible = true
                //binding.cancelBtn.isVisible = false
                saveImageAndExit(bitmap = bitmap)
            }

            binding.retake.setOnClickListener {
                //prepare for retake
                prepareRetake()
                controlFlash()
            }

        }
    }

    private fun saveImageAndExit(bitmap: Bitmap) {
        currentJob = lifecycleScope.launch(Dispatchers.IO) {
            val pictureFile = createPictureFile()
            val savedFile = storeImage(image = bitmap, pictureFile = pictureFile)
            if (savedFile != null) outputFile = savedFile.path

            withContext(Dispatchers.Main) {
                setResultAndFinish(success = savedFile != null)
            }

        }

    }

    private fun setResultAndFinish(success: Boolean) {
        if (!success) {
            setResult(Activity.RESULT_CANCELED)
            finish()
            return
        }

        setResult(
            Activity.RESULT_OK, Intent().putExtras(
                bundleOf(
                    "output_file" to outputFile,
                    "errorMessage" to errorMessage
                )
            )
        )
        finish()

    }

    private fun createPictureFile(): File {
        if (outputFile != null && outputFile!!.isNotEmpty()) {
            return File(outputFile!!)
        }
        return File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${Date().time}.jpg"
        )
    }


    private fun prepareRetake() {
        binding.previewView.isVisible = false
        binding.captureView.isVisible = true
    }


}


data class CaptureOptions(
    val outputFile: String = "",
    @DrawableRes val overlay: Int? = null,
)

data class CapturedResult(
    val success: Boolean = false,
    val file: String = "",
    val errorMessage: String = ""
)


class CustomCameraContract : ActivityResultContract<CaptureOptions?, CapturedResult>() {

    override fun createIntent(context: Context, input: CaptureOptions?): Intent {

        val intent = Intent(context, CustomCamera::class.java)

        if (input != null) {
            intent.putExtras(
                bundleOf(
                    "output_file" to input.outputFile,
                    "overlay" to input.overlay
                )
            )
        }

        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): CapturedResult {

        if (resultCode != Activity.RESULT_OK) {
            return CapturedResult(success = false, "", "Cancelled by user !")
        }
        val outputFile = intent?.getStringExtra("output_file")
        val errorMessage = intent?.getStringExtra("errorMessage") ?: ""

        return CapturedResult(
            success = true,
            file = outputFile!!,
            errorMessage = errorMessage
        )
    }

}





