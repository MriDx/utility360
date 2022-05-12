package tech.sumato.utility360.presentation.fragments.meter.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.MeterImageFragmentBinding
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel

@AndroidEntryPoint
class MeterImageFragment : Fragment() {


    private var binding_: MeterImageFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<MeterReadingActivityViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<MeterImageFragmentBinding>(
            inflater,
            R.layout.meter_image_fragment,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}