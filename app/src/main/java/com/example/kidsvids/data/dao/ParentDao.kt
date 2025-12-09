package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.Parent
import kotlinx.coroutines.flow.Flow

@Dao
interface ParentDao {
    @Insert suspend fun insert(parent: Parent)
    @Update suspend fun update(parent: Parent)
    @Delete suspend fun delete(parent: Parent)
    @Query("SELECT * FROM parents WHERE email = :email LIMIT 1")
    suspend fun getParentByEmail(email: String): Parent?

    // --- ADD THIS FUNCTION ---
    @Query("SELECT * FROM parents WHERE id = :parentId LIMIT 1")
    suspend fun getParentById(parentId: Int): Parent?

    @Query("SELECT * FROM parents ORDER BY fullname ASC")
    fun getAllParents(): Flow<List<Parent>>
}