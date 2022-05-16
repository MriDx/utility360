package tech.sumato.utility360.presentation.activity.camera.utils

import android.graphics.*
import android.view.View
import android.view.ViewTreeObserver
import android.view.Window
import android.view.WindowManager
import androidx.camera.core.ImageProxy
import java.nio.ByteBuffer


fun ImageProxy.toBitmap(): Bitmap {
    val buffer: ByteBuffer =
        this.planes[0].buffer // :- This line is where error was occurring

    buffer.rewind()
    val bytes = ByteArray(buffer.capacity())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}


inline fun View.afterMeasured(crossinline block: () -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block()
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (measuredWidth > 0 && measuredHeight > 0) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    block()
                }
            }
        })
    }
}

fun Window.setScreenBright() {
    with(this) {
        addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        attributes = attributes.also {
            it.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }
    }
}

fun scaleBitmap(image: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(image, width, height, true)
}


fun compressBitmap(bitmap: Bitmap, maxHeight: Float, maxWidth: Float): Bitmap? {

    var scaledBitmap: Bitmap?
    var bmp = bitmap.copy(bitmap.config, true)


    var actualHeight = /*options.outHeight*/ bmp.height
    var actualWidth = /*options.outWidth*/ bmp.width

    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight

    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        when {
            imgRatio < maxRatio -> {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            }
            imgRatio > maxRatio -> {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            }
            else -> {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
    }

    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565)
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    val ratioX = actualWidth / bmp.width.toFloat()
    val ratioY = actualHeight / bmp.height.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f

    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)

    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )
    bmp.recycle()

    return scaledBitmap
}