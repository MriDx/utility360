package tech.sumato.utility360.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import dagger.Provides
import dagger.assisted.AssistedInject
import javax.inject.Inject


const val LOCATION_PERMISSION = "location_permission"
const val CAMERA_PERMISSION = "camera_permission"

fun checkIfAlreadyAskedPermission(
    sharedPreferences: SharedPreferences,
    permission: String
): Boolean {
    return sharedPreferences.getBoolean(permission, false)
}

fun setPermissionAskedPreference(sharedPreferences: SharedPreferences, permission: String) {
    sharedPreferences.edit {
        putBoolean(permission, true)
    }
}


fun openAppSettings(context: Activity, fragment: Fragment?) {
    if (fragment != null)
        fragment.appSettings()
    else
        context.appSettings()
}