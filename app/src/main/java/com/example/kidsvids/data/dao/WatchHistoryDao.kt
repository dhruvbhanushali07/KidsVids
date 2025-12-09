package com.example.kidsvids.data.dao
import androidx.room.*
import com.example.kidsvids.data.entities.WatchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertOrUpdate(history: WatchHistory)
    @Query("SELECT video_id FROM watch_history WHERE kid_Id = :kidId ORDER BY last_watched_at DESC")
    fun getHistoryVideoIdsForKid(kidId: Int): Flow<List<Int>>
    @Query("SELECT * FROM watch_history")
    fun getAllWatchHistory(): Flow<List<WatchHistory>>
}