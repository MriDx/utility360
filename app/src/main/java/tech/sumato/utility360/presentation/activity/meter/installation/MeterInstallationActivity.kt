package tech.sumato.utility360.presentation.activity.meter.installation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.meter.installation.form.MeterInstallationFormFragment
import tech.sumato.utility360.presentation.fragments.progress.post_submit.PostSubmitProgressFragment

@AndroidEntryPoint
class MeterInstallationActivity : FragmentHolderActivity() {


    private val viewModel by viewModels<MeterInstallationActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addDefaultFragment()

        setActionBarTitle("Meter Installation")

        lifecycleScope.launch {
            launch {
                viewModel.navigation.collectLatest { fragmentNavigation ->
                    addFragment(
                        fragment = fragmentNavigation.fragment,
                        addToBackStack = true,
                        replace = true
                    )
                }
            }
        }

    }

    private fun addDefaultFragment() {
        val fragment = MeterInstallationFormFragment()
        addFragment(
            fragment = fragment,
            replace = true,
            addToBackStack = false
        )
    }

    override fun onFragmentChanged(fragment: Fragment) {
        super.onFragmentChanged(fragment)
        when (fragment) {
            is PostSubmitProgressFragment -> {
                //
                hideSystemUI()
            }
            else -> {
                showSystemUI()
            }
        }
    }


}