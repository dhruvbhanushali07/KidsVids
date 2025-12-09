package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.Kid
import kotlinx.coroutines.flow.Flow

@Dao
interface KidDao {
    @Insert suspend fun insert(kid: Kid)
    @Update suspend fun update(kid: Kid)
    @Delete suspend fun delete(kid: Kid)
    @Query("SELECT * FROM kids WHERE parentId = :parentId")
    fun getKidsForParent(parentId: Int): Flow<List<Kid>>
    @Query("SELECT * FROM kids WHERE id = :kidId LIMIT 1")
    fun getKidById(kidId: Int): Flow<Kid?>
}