package tech.sumato.utility360.presentation.fragments.meter.reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.MeterReadingFragmentBinding
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel

@AndroidEntryPoint
class MeterReadingFragment : Fragment() {

    private var binding_: MeterReadingFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<MeterReadingActivityViewModel>()

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

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}