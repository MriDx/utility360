package tech.sumato.utility360.presentation.fragments.customer.verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.databinding.SiteVerificationFragmentBinding

@AndroidEntryPoint
class SiteVerificationFragment : Fragment() {

    private var binding_: SiteVerificationFragmentBinding? = null
    private val binding get() = binding_!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = SiteVerificationFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
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

    private fun emulateUserDetails() {

        val customers =
            listOf("Tony Stark", "Steve Rogers", "Clint Barton", "Thor Odinson", "Bruce Banner")

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
        binding_ = null
        super.onDestroyView()
    }


}