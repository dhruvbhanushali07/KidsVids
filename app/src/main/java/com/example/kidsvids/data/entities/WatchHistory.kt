package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(
    tableName = "watch_history",
    foreignKeys = [
        ForeignKey(
            entity = Kid::class,
            parentColumns = ["id"],
            childColumns = ["kid_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Video::class,
            parentColumns = ["id"],
            childColumns = ["video_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["kid_id"]), Index(value = ["video_id"])]
)
data class WatchHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "kid_id") val kidId: Int,
    @ColumnInfo(name = "video_id") val videoId: Int,
    @ColumnInfo(name = "progress_seconds") val progressSeconds: Int = 0,
    @ColumnInfo(name = "last_watched_at") val lastWatchedAt: Long = System.currentTimeMillis()
)
