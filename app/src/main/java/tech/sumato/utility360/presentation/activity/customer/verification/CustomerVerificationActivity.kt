package tech.sumato.utility360.presentation.activity.customer.verification

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
import tech.sumato.utility360.presentation.fragments.customer.verification.instruction.SiteVerificationInstructionFragment
import tech.sumato.utility360.presentation.fragments.customer.verification.submission.SiteVerificationSubmissionFragment
import tech.sumato.utility360.presentation.fragments.meter.reading.instruction.MeterReadingInstructionFragment
import tech.sumato.utility360.presentation.fragments.tasks.pending_verification_tasks.PendingSiteVerificationTasksFragment
import tech.sumato.utility360.utils.*
import javax.inject.Inject

@AndroidEntryPoint
class CustomerVerificationActivity : FragmentHolderActivity() {

    private val viewModel by viewModels<CustomerVerificationActivityViewModel>()

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

        setActionBarTitle(getString(R.string.siteVerificationActivityTitle))

        addDefaultFragment()



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
                                        this@CustomerVerificationActivity,
                                        GPS_RESOLUTION_REQUEST
                                    )
                                }
                                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                    //open settings

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

    private fun addDefaultFragment() {
        val fragment = PendingSiteVerificationTasksFragment()
        addFragment(
            fragment = fragment,
            addToBackStack = false,
            replace = true
        )
    }

    private fun handleInstructionAcceptance(accepted: Boolean) {
        if (accepted) return
        //show instructions fragment
        navigateToInstructionsFragment()
    }

    private fun navigateToInstructionsFragment() {
        viewModel.navigate(
            fragment = SiteVerificationInstructionFragment::class.java
        )
    }


    private fun handleFragmentNavigation(fragmentNavigation: FragmentNavigation) {
        val tmpFragment = if (fragmentNavigation.args != null) {
            fragmentNavigation.fragment.apply {
                arguments = fragmentNavigation.args
            }
        } else fragmentNavigation.fragment
        val addToBackStack = when (tmpFragment) {
            is PendingSiteVerificationTasksFragment -> {
                //setActionBarTitle(getString(R.string.siteVerificationActivityTitle))
                false
            }
            is BaseInstructionFragment -> {
                //setActionBarTitle(getString(R.string.meterReadingActivityTitle_MeterReading))
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


    override fun onBackPressed() {
        if (currentVisibleFragment is SiteVerificationSubmissionFragment) {
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
            title = getString(R.string.sva_locationPermissionAlreadyAskedTitle),
            message = getString(R.string.sva_locationPermissionAlreadyAskedMessage),
            positiveBtn = getString(R.string.appSettings),
            negativeBtn = getString(R.string.sva_locationPermissionAlreadyAskedNegativeBtn),
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
            title = getString(R.string.sva_locationPermissionTitle),
            message = getString(R.string.sva_locationPermissionMessage),
            positiveBtn = getString(R.string.sva_locationPermissionPositiveBtn),
            negativeBtn = getString(R.string.sva_locationPermissionNegativeBtn),
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

    override fun onFragmentChanged(fragment: Fragment) {
        super.onFragmentChanged(fragment)
        when (fragment) {
            is SiteVerificationSubmissionFragment -> {
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


}