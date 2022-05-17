package tech.sumato.utility360.presentation.activity.meter.installation

import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.meter.installation.MeterInstallationFragment

@AndroidEntryPoint
class MeterInstallationActivity : FragmentHolderActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addDefaultFragment()

        setActionBarTitle("Meter Installation")

    }

    private fun addDefaultFragment() {
        val fragment = MeterInstallationFragment()
        addFragment(
            fragment = fragment,
            replace = true,
            addToBackStack = false
        )
    }


}