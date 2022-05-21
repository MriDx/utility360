package tech.sumato.utility360.presentation.fragments.meter.installation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.MeterInstallationFragmentBinding
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.presentation.activity.camera.CaptureOptions
import tech.sumato.utility360.presentation.activity.camera.CustomCameraContract
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivity
import java.io.File

@AndroidEntryPoint
class MeterInstallationFragment : Fragment() {

    private var binding_: MeterInstallationFragmentBinding? = null
    private val binding get() = binding_!!

    val customers =
        listOf("Tony Stark", "Steve Rogers", "Clint Barton", "Thor Odinson", "Bruce Banner")

    val meterImageCaptureLauncher = registerForActivityResult(CustomCameraContract()) { result ->
        if (result.success) {
            val capturedImage = File(result.file)
            binding.meterImageView.setImageURI(capturedImage.toUri())
        }
    }

    val siteImageCaptureLauncher = registerForActivityResult(CustomCameraContract()) { result ->
        if (result.success) {
            val capturedImage = File(result.file)
            binding.siteImageView.setImageURI(capturedImage.toUri())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = MeterInstallationFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        //setuptoolbar()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        emulateUserDetails()

        binding.extraPipeSwitch.setOnCheckedChangeListener { compoundButton, b ->
            binding.extraPipeInfoView.isVisible = b
        }

        binding.pipeLengthSlider.addOnChangeListener { slider, value, fromUser ->
            binding.totalPipeInvloved.text = "${value.toInt()} meter"
        }

        binding.pipeLengthSlider.setLabelFormatter {
            "${it.toInt()} meter"
        }


        binding.meterImageView.setOnClickListener {
            meterImageCaptureLauncher.launch(CaptureOptions())
        }

        binding.siteImageView.setOnClickListener {
            siteImageCaptureLauncher.launch(CaptureOptions())
        }




    }

    /*private fun setuptoolbar() {
        with(requireActivity() as MeterInstallationActivity) {
            setSupportActionBar(binding.toolbar)
        }
    }*/

    private fun emulateUserDetails() {

        val primaryDetails = mapOf(
            "name" to customers.shuffled().first(),
            "id" to "123Cust007"
        )

        val secondaryDetails = mapOf(
            "Connection type" to "Commercial",
            "Phone number" to "995548971",
            "Address" to "Flat-09, Some city, random area, 123456"
        )

        secondaryDetails.forEach { item ->
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

        binding.titleTextView.text = primaryDetails["name"]
        binding.secondaryTextView.text = primaryDetails["id"]

    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding_ = null
    }

}