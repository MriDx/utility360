package tech.sumato.utility360.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.sumato.utility360.data.local.model.DemoData
import dagger.hilt.android.qualifiers.ApplicationContext
import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.data.local.model.instructions.InstructionModel

@Database(
    entities = [DemoData::class, InstructionModel::class, InstructionItemModel::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {


    companion object {

        private val DB_NAME = "app_db*"

        @Synchronized
        fun build(@ApplicationContext context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }
    }

    abstract fun appDao(): AppDao

}