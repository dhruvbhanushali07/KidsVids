package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.Video
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Insert suspend fun insert(video: Video)
    @Update suspend fun update(video: Video)
    @Delete suspend fun delete(video: Video)
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE ageCategory <= :maxAgeCategory")
    fun getVideosForAgeCategory(maxAgeCategory: Int): Flow<List<Video>>
    @Query("""
    SELECT * FROM videos 
    WHERE ageCategory <= (SELECT ageCategory FROM kids WHERE id = :kidId)
    AND id NOT IN (SELECT video_Id FROM kid_blocked_videos WHERE kid_Id = :kidId)
    AND (:selectedCategoryId IS NULL OR videoCategory = :selectedCategoryId)
""")
    fun getVideosForKid(kidId: Int, selectedCategoryId: Int?): Flow<List<Video>>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    suspend fun getVideoById(videoId: Int): Video?

    @Query("SELECT * FROM videos WHERE id IN (:videoIds)")
    fun getVideosByIds(videoIds: List<Int>): Flow<List<Video>>
}