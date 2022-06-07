package tech.sumato.utility360.presentation.activity.meter.installation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragment
import tech.sumato.utility360.presentation.fragments.customer.verification.submission.SiteVerificationSubmissionFragment
import tech.sumato.utility360.presentation.fragments.meter.installation.form.MeterInstallationFormFragment
import tech.sumato.utility360.presentation.fragments.meter.installation.submission.MeterInstallationSubmissionFragment
import tech.sumato.utility360.presentation.fragments.progress.post_submit.PostSubmitProgressFragment
import tech.sumato.utility360.presentation.fragments.tasks.meter.installation.MeterInstallationTasksFragment

@AndroidEntryPoint
class MeterInstallationActivity : FragmentHolderActivity() {


    private val viewModel by viewModels<MeterInstallationActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addDefaultFragment()

        setActionBarTitle(getString(R.string.meterInstallationActivityTitle))

        lifecycleScope.launch {
            launch {
                viewModel.navigation.collectLatest { fragmentNavigation ->
                    handleFragmentNavigation(fragmentNavigation = fragmentNavigation)
                }
            }
        }

    }

    private fun addDefaultFragment() {
        val fragment = MeterInstallationTasksFragment()
        // val fragment = FindCustomerFragment()
        addFragment(
            fragment = fragment,
            replace = true,
            addToBackStack = false
        )
    }

    override fun onFragmentChanged(fragment: Fragment) {
        super.onFragmentChanged(fragment)
        when (fragment) {
            is MeterInstallationSubmissionFragment -> {
                //
                //hideSystemUI()
                supportActionBar?.hide()
            }
            else -> {
                //showSystemUI()
                supportActionBar?.show()
            }
        }
    }

    private fun handleFragmentNavigation(fragmentNavigation: FragmentNavigation) {
        val tmpFragment = if (fragmentNavigation.args != null) {
            fragmentNavigation.fragment.apply {
                arguments = fragmentNavigation.args
            }
        } else fragmentNavigation.fragment
        addFragment(
            fragment = tmpFragment,
            addToBackStack = true,
            replace = true
        )
    }

    override fun onBackPressed() {
        if (currentVisibleFragment is MeterInstallationSubmissionFragment) {
            if (viewModel.jobInProgress) {
                return
            }
            if (!viewModel.jobSuccess) {
                supportFragmentManager.popBackStack()
                return
            }
            supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            return
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return
        }
        super.onBackPressed()
    }


}