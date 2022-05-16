package tech.sumato.utility360.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationUtil @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpsUtils: GpsUtils
) {


    private var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun removeCallback() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.let {
                /*p0.locations.forEach {
                            //return location from here
                        }*/
                for (i in 0 until it.locations.size) {
                    //return first location
                   /* onLocation?.invoke(
                        LatLng(
                            it.locations[i].latitude,
                            it.locations[i].longitude
                        )
                    )*/
                    removeCallback()
                    return
                }
            }
        }
    }


    init {
        start()

    }

    private fun start() {
        gpsUtils.turnGpsOn { isEnabled, exception ->
            if (isEnabled) {
                requestLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 1000 * 5
            fastestInterval = 1000
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }


    /*fun start() {
        GpsUtils(_context).turnGPSOn {
            if (it) requestLocation()
            else {
                Log.d("mridx", "start: no thanks tipi disa bal")
            }
        }
        requestLocation()
    }*/


}