package tech.sumato.utility360.data.local.model.instructions

import androidx.room.*
import org.json.JSONObject

@Entity(tableName = "instructions")
class InstructionModel {

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "type")
    var type: String = ""

    @ColumnInfo(name = "title")
    var title: String = ""

    @ColumnInfo(name = "description")
    var description: String = ""


    /*constructor(id: Int, type: String, title: String, description: String) {
        this.id = id
        this.type = type
        this.title = title
        this.description = description
    }*/



    constructor(type: String, title: String, description: String) {
        this.type = type
        this.title = title
        this.description = description
    }


    companion object {

        const val TABLE_NAME = "instructions"

        fun fromJson(jsonObject: JSONObject): InstructionModel {
            val type = jsonObject.getString("type")
            val title = jsonObject.getString("title")
            val description = jsonObject.getString("desc")
            return InstructionModel(
                type = type,
                title = title,
                description = description
            )
        }

    }


}
