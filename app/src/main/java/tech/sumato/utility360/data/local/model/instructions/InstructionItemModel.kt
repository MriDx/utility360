package tech.sumato.utility360.data.local.model.instructions

import androidx.room.*
import org.json.JSONObject
import tech.sumato.utility360.data.local.entity.instructions.InstructionItemEntity

@Entity(
    tableName = "instruction_items",
    foreignKeys = [ForeignKey(
        entity = InstructionModel::class,
        parentColumns = ["type"],
        childColumns = ["type"],
        onDelete = ForeignKey.CASCADE
    )]
)
class InstructionItemModel {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "type")
    var type: String = ""

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "description")
    var description: String = ""

    @ColumnInfo(name = "accepted")
    var accepted: Boolean = false


    constructor(id: Int, type: String, title: String, description: String, accepted: Boolean) {
        this.id = id
        this.title = title
        this.description = description
        this.type = type
        this.accepted = accepted
    }

    @Ignore
    constructor(type: String, title: String, description: String) {
        this.type = type
        this.title = title
        this.description = description
    }


    companion object {

        const val TABLE_NAME = "instruction_items"

        fun fromJson(jsonObject: JSONObject): InstructionItemModel {
            val title = jsonObject.getString("title")
            val description = jsonObject.getString("desc")
            return InstructionItemModel(
                type = "",
                title = title,
                description = description
            )
        }
    }

    fun toEntity(): InstructionItemEntity {
        return InstructionItemEntity(
            id, type, title, description, accepted
        )
    }


}
