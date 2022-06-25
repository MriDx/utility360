package tech.sumato.utility360.data.local.model.instructions

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import org.json.JSONObject

@Parcelize
data class InstructionItemsModel(
    @Embedded
    val instructionModel: @RawValue InstructionModel,
    @Relation(
        parentColumn = "type",
        entityColumn = "type"
    )
    val items: @RawValue List<InstructionItemModel>
) : Parcelable {


    companion object {
        fun fromJson(jsonObject: JSONObject): InstructionItemsModel {
            val instructionModel = InstructionModel.fromJson(jsonObject = jsonObject)
            val items = jsonObject.getJSONArray("items")
            val itemList = mutableListOf<InstructionItemModel>()
            repeat(items.length()) { index ->
                itemList.add(InstructionItemModel.fromJson(jsonObject = items.getJSONObject(index)))
            }
            return InstructionItemsModel(
                instructionModel = instructionModel,
                items = itemList.map {
                    it.type = instructionModel.type
                    it
                }.toList()
            )
        }
    }

}