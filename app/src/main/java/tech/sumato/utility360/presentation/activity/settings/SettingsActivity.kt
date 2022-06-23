package tech.sumato.utility360.presentation.activity.settings

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.meter.reading.submission.MeterReadingSubmissionFragment
import tech.sumato.utility360.presentation.fragments.settings.SettingsFragment
import tech.sumato.utility360.presentation.fragments.settings.account.password.ChangePasswordFragment
import tech.sumato.utility360.presentation.fragments.settings.submission.SettingsSubmission

@AndroidEntryPoint
class SettingsActivity : FragmentHolderActivity() {


    private val viewModel by viewModels<SettingsActivityViewModel>()

    private lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        from = intent?.getStringExtra("from") ?: "change_password"


        addAppropriateFragment(from = from)

        addDefaultFragment()


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.navigation.collectLatest { fragmentNavigation ->
                        handleFragmentNavigation(fragmentNavigation)
                    }
                }
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

    private fun addAppropriateFragment(from: String) {
        val fragment = generateFragment(from = from)
    }

    private fun generateFragment(from: String): Fragment {
        return when (from) {
            "change_password" -> {
                setActionBarTitle("Change password")
                ChangePasswordFragment()
            }
            else -> {
                SettingsFragment()
            }
        }
    }


    private fun addDefaultFragment() {
        val fragment = ChangePasswordFragment()
        addFragment(
            fragment = fragment,
            addToBackStack = false,
            replace = true
        )
    }

    override fun onFragmentChanged(fragment: Fragment) {
        super.onFragmentChanged(fragment)

        when (fragment) {
            is ChangePasswordFragment -> {
                setActionBarTitle("Change password")
                supportActionBar?.show()
            }
            is SettingsSubmission -> {
                supportActionBar?.hide()
            }
            else -> {

            }
        }

    }

    override fun onBackPressed() {
        if (currentVisibleFragment is SettingsSubmission) {
            if (viewModel.jobInProgress) {
                return
            }
            if (!viewModel.jobSuccess) {
                supportFragmentManager.popBackStack()
                return
            }
            /*supportFragmentManager.popBackStackImmediate(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )*/
            /**
             * as settings activity wont hold another default fragment, like listing or other
             * so finishing the activity after a successful form submissions
             */
            finish()
            return
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return
        }
        super.onBackPressed()
    }


}