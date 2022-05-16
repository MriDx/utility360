package tech.sumato.utility360.domain.use_case.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.GnssStatus
import android.location.GpsStatus
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.concurrent.Executor
import javax.inject.Inject


/**
 * listen for gps status
 *
 * currently not working
 */

@RequiresApi(Build.VERSION_CODES.N)
@SuppressLint("MissingPermission")
class GnssStatusListenerUseCase
@Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    operator fun invoke() : Flow<Boolean> = callbackFlow<Boolean> {

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val callback = object : GnssStatus.Callback() {
            override fun onStarted() {
                super.onStarted()
                trySend(true)
            }

            override fun onStopped() {
                super.onStopped()
                trySend(false)
            }
        }

        locationManager.registerGnssStatusCallback(
            callback,
            Handler(Looper.getMainLooper())
        )

        awaitClose { locationManager.unregisterGnssStatusCallback(callback) }
    }

}