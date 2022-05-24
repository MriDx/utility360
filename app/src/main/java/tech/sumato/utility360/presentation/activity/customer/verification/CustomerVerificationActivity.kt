package tech.sumato.utility360.presentation.activity.customer.verification

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.customer.verification.SiteVerificationFragment

@AndroidEntryPoint
class CustomerVerificationActivity : FragmentHolderActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActionBarTitle(getString(R.string.siteVerificationActivityTitle))

        addDefaultFragment()


    }

    private fun addDefaultFragment() {
        val fragment = SiteVerificationFragment()
        addFragment(
            fragment = fragment,
            addToBackStack = false,
            replace = true
        )
    }


}