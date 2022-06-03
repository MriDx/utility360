package tech.sumato.utility360.presentation.activity.customer.verification

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.customer.verification.SiteVerificationFragment
import tech.sumato.utility360.presentation.fragments.tasks.pending_verification_tasks.PendingSiteVerificationTasksFragment

@AndroidEntryPoint
class CustomerVerificationActivity : FragmentHolderActivity() {

    private val viewModel by viewModels<CustomerVerificationActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setActionBarTitle(getString(R.string.siteVerificationActivityTitle))

        addDefaultFragment()



        lifecycleScope.launch {
            launch {
                viewModel.navigation.collectLatest { fragmentNavigation ->
                    handleFragmentNavigation(fragmentNavigation)
                }
            }
        }


    }

    private fun addDefaultFragment() {
        val fragment = PendingSiteVerificationTasksFragment()
        addFragment(
            fragment = fragment,
            addToBackStack = false,
            replace = true
        )
    }


    private fun handleFragmentNavigation(fragmentNavigation: FragmentNavigation) {
        val tmpFragment = if (fragmentNavigation.args != null) {
            fragmentNavigation.fragment.apply {
                arguments = fragmentNavigation.args
            }
        } else fragmentNavigation.fragment
        addFragment(
            fragment = tmpFragment,
            addToBackStack = supportFragmentManager.backStackEntryCount == 0,
            replace = true
        )
    }

    override fun onFragmentChanged(fragment: Fragment) {
        setActionBarTitle(getString(R.string.siteVerificationActivityTitle))
    }


    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return
        }
        super.onBackPressed()
    }

}