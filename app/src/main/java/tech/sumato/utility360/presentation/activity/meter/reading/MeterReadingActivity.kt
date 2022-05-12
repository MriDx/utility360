package tech.sumato.utility360.presentation.activity.meter.reading

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragment
import tech.sumato.utility360.presentation.fragments.meter.reading.MeterReadingFragment

@AndroidEntryPoint
class MeterReadingActivity : FragmentHolderActivity() {


    private val viewModel by viewModels<MeterReadingActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addDefaultFragment()

        super.setActionBarTitle("Find customer")


        lifecycleScope.launch {
            launch {
                viewModel.navigation.collectLatest { fragmentNavigation ->
                    handleFragmentNavigation(fragmentNavigation)
                }

            }
        }

    }

    private fun handleFragmentNavigation(fragmentNavigation: MeterReadingActivityViewModel.FragmentNavigation) {
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

    private fun addDefaultFragment() {
        addFragment(
            fragment = FindCustomerFragment(),
            addToBackStack = false,
            replace = true
        )
    }

    override fun onFragmentChanged(fragment: Fragment) {
        super.onFragmentChanged(fragment)
        when (fragment) {
            is MeterReadingFragment -> {
                setActionBarTitle("Meter reading")
            }
            else -> {
                setActionBarTitle("Find customer")
            }
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
            return
        }
        super.onBackPressed()
    }

}