package tech.sumato.utility360.domain.use_case.location

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception
import javax.inject.Inject

data class GpsResult(val enabled: Boolean = false, val exception: Exception? = null)

class EnableGpsUseCase
@Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    operator fun invoke() = callbackFlow<GpsResult> {

        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //gps enabled
            trySend(element = GpsResult(enabled = true))
        }
        /*val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 * 10 //in every 10 sec
            fastestInterval = 1000 * 2 // 2 sec

        }*/
        val settingsClient = LocationServices.getSettingsClient(context)
        settingsClient.checkLocationSettings(
            LocationSettingsRequest
                .Builder()
                .addLocationRequest(LocationRequest.create())
                .setAlwaysShow(true)
                .build()
        )
            .addOnSuccessListener {
                trySend(element = GpsResult(enabled = true))
            }
            .addOnFailureListener {
                trySend(element = GpsResult(enabled = false, exception = it))
            }

        awaitClose { }

    }

}