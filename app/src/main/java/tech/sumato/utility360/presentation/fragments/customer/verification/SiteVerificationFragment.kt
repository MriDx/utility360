package tech.sumato.utility360.presentation.fragments.customer.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.tasks.SiteVerificationTaskRequest
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.databinding.SiteVerificationFragmentBinding
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivityViewModel

@AndroidEntryPoint
class SiteVerificationFragment : Fragment() {

    private var binding_: SiteVerificationFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<CustomerVerificationActivityViewModel>()

    private val siteVerificationTaskRequest = ObservableField(SiteVerificationTaskRequest())


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

        //emulateUserDetails()

        renderCustomerDetails()

        binding.submitBtn.setOnClickListener {

            if (!siteVerificationTaskRequest.get()!!.validate()) {
                //
                return@setOnClickListener
            }

            viewModel.submitVerification(params = siteVerificationTaskRequest.get()!!.toJson())
        }


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


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}