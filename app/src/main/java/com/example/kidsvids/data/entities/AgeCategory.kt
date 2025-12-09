package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(tableName = "agecategories")
data class AgeCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)