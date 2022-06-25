package tech.sumato.utility360.data.local.entity.instructions

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class InstructionItemEntity(
    var id: Int = 0,
    var type: String = "",
    var title: String = "",
    var description: String = "",
    var accepted: Boolean = false
) : Parcelable