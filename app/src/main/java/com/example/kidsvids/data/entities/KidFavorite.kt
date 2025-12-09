package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(
    tableName = "kid_favorites",
    primaryKeys = ["kid_id", "video_id"], // composite primary key
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
data class KidFavorite(
    @ColumnInfo(name = "kid_id") val kidId: Int,
    @ColumnInfo(name = "video_id") val videoId: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
