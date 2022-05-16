package tech.sumato.utility360.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnFailureListener
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.Exception
import javax.inject.Inject
import kotlin.time.Duration

class GpsUtils @Inject constructor(@ApplicationContext private val context: Context) {

    private var mSettingsClient: SettingsClient? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var locationManager: LocationManager? = null
    private var locationRequest: LocationRequest? = null

    init {

        mSettingsClient = LocationServices.getSettingsClient(context)

        locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 * 10 //in every 10 sec
            fastestInterval = 1000 * 2 // 2 sec
        }

        mLocationSettingsRequest =
            LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest!!)
                .setAlwaysShow(true) //this is the key ingredient
                .build()


    }

    fun turnGpsOn(listener: (isEnabled: Boolean, exception: Exception?) -> Unit) {

        if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            //gps is enabled
            listener.invoke(true, null)
            return
        }

        mSettingsClient!!.checkLocationSettings(mLocationSettingsRequest!!)
            .addOnSuccessListener {

                //gps is enabled

                listener.invoke(true, null)

            }
            .addOnFailureListener { exception ->

                val statusCode = (exception as ApiException).statusCode

                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        listener.invoke(false, exception)
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        listener.invoke(false, exception)
                    }
                }

            }

    }


}