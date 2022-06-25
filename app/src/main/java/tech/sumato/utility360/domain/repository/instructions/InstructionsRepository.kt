package tech.sumato.utility360.domain.repository.instructions

import org.json.JSONArray
import org.json.JSONObject
import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel

interface InstructionsRepository {


    suspend fun readFromJson(): JSONArray?

    suspend fun getListFromJson(jsonArray: JSONArray?): List<InstructionItemsModel>

    suspend fun storeInstructions()

    suspend fun checkIfHasInstructions(): Boolean

    suspend fun checkIfAcceptedInstructions(type: String): Boolean

    suspend fun getByType(type: String): InstructionItemsModel

    suspend fun updateInstructionItem(item: InstructionItemModel)

    suspend fun updateAllUnselect()


}