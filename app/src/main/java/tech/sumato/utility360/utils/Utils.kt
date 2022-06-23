package tech.sumato.utility360.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import tech.sumato.utility360.BuildConfig
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.net.UnknownHostException


fun Context.startActivity(activity: Class<*>, bundle: (() -> Bundle)? = null) {
    startActivity(Intent(this, activity).apply {
        bundle ?: return@apply
        putExtras(bundle.invoke())
    })
}


fun Fragment.startActivity(activity: Class<*>, bundle: (() -> Bundle)? = null) {
    requireContext().startActivity(activity = activity, bundle = bundle)
}


fun storeImage(image: Bitmap, pictureFile: File): File? {
    if (!pictureFile.exists()) {
        pictureFile.createNewFile()
    }
    try {
        val fos = FileOutputStream(pictureFile)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()
        return pictureFile
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        Log.d("mridx", "File not found: ${e.message}")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d("mridx", "Error accessing file: ${e.message}")
    }
    return null
}


const val POSITIVE_BTN = 1
const val NEGATIVE_BTN = 2
fun Context.showDialog(
    title: String,
    message: String,
    positiveBtn: String?,
    negativeBtn: String = "Cancel",
    showNegativeBtn: Boolean = false,
    cancellable: Boolean = false,
    onPressed: ((d: DialogInterface, i: Int) -> Unit)
) = run {
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(message)
        if (positiveBtn != null)
            setPositiveButton(positiveBtn) { d, _ -> onPressed.invoke(d, POSITIVE_BTN) }
        if (showNegativeBtn)
            setNegativeButton(negativeBtn) { d, _ -> onPressed.invoke(d, NEGATIVE_BTN) }
        setCancelable(cancellable)
    }.create()
}

fun Fragment.showDialog(
    title: String,
    message: String,
    positiveBtn: String?,
    negativeBtn: String = "Cancel",
    showNegativeBtn: Boolean = false,
    cancellable: Boolean = false,
    onPressed: ((d: DialogInterface, i: Int) -> Unit)
) = requireContext().showDialog(
    title,
    message,
    positiveBtn,
    negativeBtn,
    showNegativeBtn,
    cancellable,
    onPressed
)


fun Fragment.appSettings() {
    requireActivity().appSettings()
}

fun Activity.appSettings() {
    Intent().also { intent ->
        intent.action =
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts(
            "package",
            BuildConfig.APPLICATION_ID,
            null
        )
        startActivityForResult(
            intent,
            APP_SETTINGS
        )
    }
}


fun parseException(e: Throwable?): String {
    return when (e) {
        is UnknownHostException -> {
            "No internet available"
        }
        is Exception -> {
            when {
                e.message?.contains("Unable to resolve host", ignoreCase = true) ?: false -> {
                    "No internet available"
                }
                else -> e.message ?: "No internet available"
            }
        }
        else -> e?.message ?: "No internet available"
    }

}


fun View.hideKeyboard() {
    val imm: InputMethodManager =
        context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun TextInputEditText.parentLayout(): TextInputLayout {
    return parent.parent as TextInputLayout
}
