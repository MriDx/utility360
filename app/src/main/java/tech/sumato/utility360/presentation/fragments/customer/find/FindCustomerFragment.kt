package tech.sumato.utility360.presentation.fragments.customer.find

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import tech.sumato.utility360.databinding.UserFinderFragmentBinding
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel
import tech.sumato.utility360.presentation.fragments.meter.reading.MeterReadingFragment
import tech.sumato.utility360.utils.hideKeyboard

@AndroidEntryPoint
class FindCustomerFragment : Fragment() {

    private var binding_: UserFinderFragmentBinding? = null
    private val binding get() = binding_!!

    private val viewModel by activityViewModels<MeterReadingActivityViewModel>()

    val customers =
        listOf("Tony Stark", "Steve Rogers", "Clint Barton", "Thor Odinson", "Bruce Banner")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<UserFinderFragmentBinding?>(
            inflater,
            R.layout.user_finder_fragment,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            findUserBtn.setOnClickListener {
                val typedId = binding.customerIdField.text.toString()
                //emulateUserFinder(typedId)
                it.hideKeyboard()
                if (typedId == "ttmu") {
                    emulateDataReceived("ttmu")
                    return@setOnClickListener
                }
                binding.findUserBtn.showProgressbar(true)

                emulateUserNotFound()
            }

            findAnotherBtn.setOnClickListener {
                binding.customerFinderHolder.apply {
                    customerIdField.text?.clear()
                }.isVisible = true
                binding.customerHolder.isVisible = false
            }


        }

    }

    private fun emulateUserNotFound() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000 * 2)
            withContext(Dispatchers.Main) {
                binding.findUserBtn.showProgressbar(false)
                Snackbar.make(binding.root, "User not found !", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun emulateUserFinder(typedId: String) {
        // TODO: uncomment
        //if (typedId.isEmpty()) return
        viewLifecycleOwner.lifecycleScope.launch {
            //delay(1000)
            emulateDataReceived(customerId = typedId)
        }
    }

    private var customerId = ""

    private fun emulateDataReceived(customerId: String) {
        this.customerId = customerId

        val secondaryDetails = mapOf(
            "Connection date" to "1 Jan 2022",
            "Connection type" to "Commercial",
            "Meter Id" to "M0198IOH",
            "Meter status" to "Normal",
            "Previous reading" to "3344",
            "Previous reading on" to "1 Apr 2022",
            "Next reading" to "1 May 2022 - 10 May 2022"
        )

        val customerName = customers.shuffled().first()
        binding.customerNameView.text = customerName
        binding.customerIdView.text = customerId

        //binding.continueBtn.isEnabled = listOf(true, false).shuffled().first()
        binding.continueBtn.isEnabled = true

        binding.continueBtn.setOnClickListener {
            viewModel.navigate(
                fragment = MeterReadingFragment::class.java,
                args = bundleOf(
                    "secondaryDetails" to secondaryDetails,
                    "primaryDetails" to mapOf(
                        "name" to customerName,
                        "id" to customerId
                    )
                )
            )
        }

        binding.customerSecondaryInfoHolder.removeAllViews()

        secondaryDetails.forEach { item ->
            val secondaryItemView = DataBindingUtil.inflate<ProfileInfoItemViewBinding>(
                LayoutInflater.from(requireContext()),
                R.layout.profile_info_item_view,
                binding.customerSecondaryInfoHolder,
                false
            ).apply {
                headingView.text = item.key
                valueView.text = item.value
            }.root
            binding.customerSecondaryInfoHolder.addView(secondaryItemView)
        }

        binding.customerIdField.text?.clear()
        binding.customerFinderHolder.isVisible = false
        binding.customerHolder.isVisible = true

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()

    }

}