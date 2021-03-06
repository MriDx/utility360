package tech.sumato.utility360.presentation.activity.meter.reading

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsStatusCodes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.utils.FragmentNavigation
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.base.instruction.BaseInstructionFragment
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragmentV2
import tech.sumato.utility360.presentation.fragments.meter.reading.form.MeterReadingFragment
import tech.sumato.utility360.presentation.fragments.meter.reading.instruction.MeterReadingInstructionFragment
import tech.sumato.utility360.presentation.fragments.meter.reading.submission.MeterReadingSubmissionFragment
import tech.sumato.utility360.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class MeterReadingActivity : FragmentHolderActivity() {


    private val viewModel by viewModels<MeterReadingActivityViewModel>()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.map {
                    it.value
                }.toList().contains(false)) {
                //not granted
                checkLocationPermission()
                return@registerForActivityResult
            }
            //permission granted
            viewModel.enableGps()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        addDefaultFragment()

        super.setActionBarTitle(getString(R.string.meterReadingActivityTitle_MeterReading))


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.navigation.collectLatest { fragmentNavigation ->
                        handleFragmentNavigation(fragmentNavigation)
                    }

                }
                launch {
                    viewModel.gpsResultFlow.collectLatest { gpsResult ->
                        if (gpsResult.exception != null) {
                            val statusCode = (gpsResult.exception as ApiException).statusCode
                            when (statusCode) {
                                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                                    (gpsResult.exception as? ResolvableApiException)?.startResolutionForResult(
                                        this@MeterReadingActivity,
                                        GPS_RESOLUTION_REQUEST
                                    )
                                }
                                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                    //open settings
                                    // TODO: handle by opening settings
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.instructionAccepted.collectLatest { accepted ->
                        handleInstructionAcceptance(accepted)
                    }
                }
            }
        }

        checkLocationPermission()


    }

    private fun handleInstructionAcceptance(accepted: Boolean) {
        if (accepted) return
        //show instructions fragment
        navigateToInstructionsFragment()
    }

    private fun navigateToInstructionsFragment() {
        viewModel.navigate(
            fragment = MeterReadingInstructionFragment::class.java
        )
    }


    private fun checkLocationPermission() {
        when {
            (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) -> {
                //permission granted
                viewModel.enableGps()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                //show rational
                showLocationServicePermissionDialog()
            }
            else -> {
                //request permission
                if (checkIfAlreadyAskedPermission(
                        sharedPreferences = sharedPreferences,
                        permission = LOCATION_PERMISSION
                    )
                ) {
                    //already asked and pressed denied
                    //invoke settings to allow permission
                    showLocationPermissionAlreadyAskedDialog()
                    return
                }
                showLocationServicePermissionDialog()
            }
        }
    }

    private fun showLocationPermissionAlreadyAskedDialog() {
        showDialog(
            title = getString(R.string.meterReadingLocationPermissionAlreadyAskedTitle),
            message = getString(R.string.meterReadingLocationPermissionAlreadyAskedMessage),
            positiveBtn = getString(R.string.appSettings),
            negativeBtn = getString(R.string.meterReadingLocationPermissionAlreadyAskedCancelBtn),
            showNegativeBtn = true,
            cancellable = false
        ) { d, i ->
            d.dismiss()
            when (i) {
                POSITIVE_BTN -> {
                    appSettings()
                }
                NEGATIVE_BTN -> {
                    handleLocationUsageDecline()
                }
            }
        }.show()
    }

    private fun showLocationServicePermissionDialog() {
        showDialog(
            title = getString(R.string.meterReadingLocationPermissionTitle),
            message = getString(R.string.meterReadingLocationPermissionMessage),
            positiveBtn = getString(R.string.meterReadingLocationPermissionPositiveBtn),
            negativeBtn = getString(R.string.meterReadingLocationPermissionNegativeBtn),
            showNegativeBtn = true,
            cancellable = false
        ) { d, i ->
            d.dismiss()
            when (i) {
                NEGATIVE_BTN -> {
                    handleLocationUsageDecline()
                }
                POSITIVE_BTN -> {
                    //open request launcher
                    launchLocationPermissionRequest()
                }
            }
        }.show()
    }

    private fun handleLocationUsageDecline() {
        finish()
    }

    private fun launchLocationPermissionRequest() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        setPermissionAskedPreference(sharedPreferences, LOCATION_PERMISSION)
    }

    private fun handleFragmentNavigation(fragmentNavigation: FragmentNavigation) {
        val tmpFragment = if (fragmentNavigation.args != null) {
            fragmentNavigation.fragment.apply {
                arguments = fragmentNavigation.args
            }
        } else fragmentNavigation.fragment
        val addToBackStack = when (tmpFragment) {
            is FindCustomerFragmentV2 -> {
                setActionBarTitle(getString(R.string.meterReadingActivityTitle_FindCustomer))
                false
            }
            is BaseInstructionFragment -> {
                setActionBarTitle(getString(R.string.meterReadingActivityTitle_MeterReading))
                false
            }
            else -> true
        }
        addFragment(
            fragment = tmpFragment,
            addToBackStack = addToBackStack,
            replace = true
        )
    }

    private fun addDefaultFragment() {
        /* addFragment(
             fragment = FindCustomerFragment(),
             addToBackStack = false,
             replace = true
         )*/
        addFragment(
            fragment = FindCustomerFragmentV2(),
            addToBackStack = false,
            replace = true
        )
    }

    override fun onFragmentChanged(fragment: Fragment) {
        super.onFragmentChanged(fragment)
        when (fragment) {
            is MeterReadingFragment -> {
                setActionBarTitle(getString(R.string.meterReadingActivityTitle_MeterReading))
                supportActionBar?.show()
            }
            is MeterReadingSubmissionFragment -> {
                supportActionBar?.hide()
            }
            is MeterReadingInstructionFragment -> {
                setActionBarTitle("Instructions")
                supportActionBar?.show()
            }
            else -> {
                setActionBarTitle(getString(R.string.meterReadingActivityTitle_FindCustomer))
                supportActionBar?.show()
            }
        }
    }

    override fun onBackPressed() {
        if (currentVisibleFragment is MeterReadingSubmissionFragment) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GPS_RESOLUTION_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                //fetch location updates
                viewModel.locationUpdates()
                return
            }
            //ask enable gps
            viewModel.enableGps()
            return
        }
        if (requestCode == APP_SETTINGS) {
            //
            checkLocationPermission()
        }

    }


}