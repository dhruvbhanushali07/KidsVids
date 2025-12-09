package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    // --- ADD THIS ---
    @Update
    suspend fun update(category: Category)

    // --- ADD THIS ---
    @Delete
    suspend fun delete(category: Category)
    @Query("SELECT * FROM categories")
    suspend fun getAllCategoriesList(): List<Category>

    @Query("SELECT * FROM categories")
    fun getAllCategoriesFlow(): Flow<List<Category>>

    // We might still need this one from the previous step
    @Query("SELECT * FROM categories WHERE id IN (:categoryIds)")
    suspend fun getCategoriesByIds(categoryIds: List<Int>): List<Category>
}