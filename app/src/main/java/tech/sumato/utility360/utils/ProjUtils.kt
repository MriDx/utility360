package tech.sumato.utility360.utils

import java.text.SimpleDateFormat
import java.util.*


fun String.toMeterReading(): String {
    return if (this.length == 7) this.substring(0, this.length - 2) else this
}

fun Date.toMeterReadingDate(): String {
    return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}