package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.KidBlockedVideo
import kotlinx.coroutines.flow.Flow

@Dao
interface KidBlockedVideoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(blockedVideo: KidBlockedVideo)
    @Delete suspend fun delete(blocked: KidBlockedVideo)
    @Query("SELECT * FROM kid_blocked_videos WHERE kid_id = :kidId")
    suspend fun getBlockedVideosForKid(kidId: Int): List<KidBlockedVideo>
    @Query("SELECT video_id FROM kid_blocked_videos WHERE kid_id = :kidId")
    fun getBlockedVideoIdsForKid(kidId: Int): Flow<List<Int>>

    @Query("SELECT * FROM kid_blocked_videos")
    fun getAllBlocked(): Flow<List<KidBlockedVideo>>
}