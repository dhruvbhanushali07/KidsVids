package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.AgeCategory

@Dao
interface AgeCategoryDao {
    @Insert suspend fun insert(ageCategory: AgeCategory)
    @Query("SELECT * FROM agecategories")
    suspend fun getAllAgeCategoriesList(): List<AgeCategory>
}