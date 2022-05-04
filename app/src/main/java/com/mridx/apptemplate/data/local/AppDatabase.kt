package com.mridx.apptemplate.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mridx.apptemplate.data.local.model.DemoData
import dagger.hilt.android.qualifiers.ApplicationContext

@Database(entities = [DemoData::class], version = 1, exportSchema = false)
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