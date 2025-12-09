package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)