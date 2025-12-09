package com.example.kidsvids.data.dao

import androidx.room.*
import com.example.kidsvids.data.entities.KidFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface KidFavoriteDao {

    // Check if a specific video is favorited by a specific kid
    @Query("SELECT EXISTS(SELECT 1 FROM kid_favorites WHERE kid_id = :kidId AND video_id = :videoId LIMIT 1)")
    fun isFavorite(kidId: Int, videoId: Int): Flow<Boolean>

    // Get all favorite video IDs for a specific kid
    @Query("SELECT video_id FROM kid_favorites WHERE kid_id = :kidId")
    fun getFavoriteVideoIdsForKid(kidId: Int): Flow<List<Int>>

    // Insert a new favorite
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: KidFavorite)

    // Delete a favorite (by kid_id and video_id)
    @Query("DELETE FROM kid_favorites WHERE kid_id = :kidId AND video_id = :videoId")
    suspend fun delete(kidId: Int, videoId: Int)

    @Query("SELECT * FROM kid_favorites")
    fun getAllFavorites(): Flow<List<KidFavorite>>
}
