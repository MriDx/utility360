package com.mridx.apptemplate.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "demo_db")
data class DemoData(@PrimaryKey val id: Int = 0, val name: String)
