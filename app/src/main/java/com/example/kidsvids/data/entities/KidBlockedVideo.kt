package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(
    tableName = "kid_blocked_videos",
    primaryKeys = ["kid_id", "video_id"],
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
data class KidBlockedVideo(
    @ColumnInfo(name = "kid_id") val kidId: Int,
    @ColumnInfo(name = "video_id") val videoId: Int
)
