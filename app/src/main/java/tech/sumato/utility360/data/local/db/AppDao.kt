package tech.sumato.utility360.data.local.db

import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.data.local.model.instructions.InstructionModel

@Dao
interface AppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructionModel(instructionModel: InstructionModel): Long

    @Query("SELECT * FROM ${InstructionModel.TABLE_NAME}")
    suspend fun getAllInstructionModel(): List<InstructionModel>

    @Query("DELETE FROM ${InstructionModel.TABLE_NAME}")
    suspend fun deleteInstructionTable()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstructionItemModel(instructionItemModel: InstructionItemModel): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInstructionItemModel(item: InstructionItemModel)

    @Query("SELECT * FROM ${InstructionItemModel.TABLE_NAME}")
    suspend fun getAllInstructionItemModel(): List<InstructionItemModel>

    @Query("SELECT * FROM ${InstructionItemModel.TABLE_NAME} WHERE type == :type")
    suspend fun getInstructionItemsByType(type: String): List<InstructionItemModel>

    @Query("DELETE FROM ${InstructionItemModel.TABLE_NAME}")
    suspend fun deleteInstructionItemTable()


    @Transaction
    suspend fun insertInstructionItemsModels(items: List<InstructionItemsModel>) {
        withContext(Dispatchers.IO) {
            items.map {
                async {
                    insertInstructionItemsModel(instructionItemsModel = it)
                }
            }.awaitAll()
        }
    }

    @Transaction
    suspend fun insertInstructionItemsModel(instructionItemsModel: InstructionItemsModel) {
        withContext(Dispatchers.IO) {
            val id =
                insertInstructionModel(instructionModel = instructionItemsModel.instructionModel)
            instructionItemsModel.items.map {
                async {
                    insertInstructionItemModel(instructionItemModel = it)
                }
            }.awaitAll()
        }
    }

    @Transaction
    @Query("SELECT * FROM ${InstructionModel.TABLE_NAME} WHERE type = :type")
    fun getInstructionsByType(type: String): InstructionItemsModel

    @Transaction
    @Query("SELECT * FROM ${InstructionModel.TABLE_NAME}")
    fun getAllInstructions(): List<InstructionItemsModel>


}