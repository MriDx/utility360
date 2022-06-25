package tech.sumato.utility360.data.repository.instructions

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import tech.sumato.utility360.data.local.db.AppDao
import tech.sumato.utility360.data.local.db.AppDatabase
import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.domain.repository.instructions.InstructionsRepository
import javax.inject.Inject

class InstructionsRepositoryImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val appDao: AppDao,
) : InstructionsRepository {

    override suspend fun readFromJson(): JSONArray? {
        return withContext(Dispatchers.IO) {
            try {

                val inputStream = context.assets.open("instructions.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                val data = String(buffer)
                JSONArray(data)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    override suspend fun getListFromJson(jsonArray: JSONArray?): List<InstructionItemsModel> {
        return withContext(Dispatchers.IO) {
            jsonArray ?: return@withContext emptyList<InstructionItemsModel>()
            val instructions = mutableListOf<InstructionItemsModel>()
            repeat(jsonArray.length()) { index ->
                instructions.add(
                    InstructionItemsModel.fromJson(
                        jsonObject = jsonArray.getJSONObject(
                            index
                        )
                    )
                )
            }
            instructions
        }
    }


    override suspend fun storeInstructions() {
        withContext(Dispatchers.IO) {
            val jsonData = readFromJson()
            val instructions = getListFromJson(jsonArray = jsonData)

            appDao.insertInstructionItemsModels(items = instructions)

        }
    }


    override suspend fun checkIfHasInstructions(): Boolean {
        return withContext(Dispatchers.IO) {
            val items = appDao.getAllInstructionModel()
            items.isNotEmpty()
        }
    }

    override suspend fun checkIfAcceptedInstructions(type: String): Boolean {
        return withContext(Dispatchers.IO) {
            val items = appDao.getInstructionItemsByType(type = type)

            items.all { it.accepted }
        }
    }

    override suspend fun getByType(type: String): InstructionItemsModel {
        return withContext(Dispatchers.IO) {
            appDao.getInstructionsByType(type = type)
        }
    }

    override suspend fun updateInstructionItem(item: InstructionItemModel) {
        withContext(Dispatchers.IO) {
            appDao.updateInstructionItemModel(item = item)
        }
    }

    override suspend fun updateAllUnselect() {
        withContext(Dispatchers.IO) {
            val items = appDao.getAllInstructionItemModel()
            items.filter { it.accepted }.map {
                it.accepted = false
                async { appDao.updateInstructionItemModel(it) }
            }.awaitAll()
        }

    }



}