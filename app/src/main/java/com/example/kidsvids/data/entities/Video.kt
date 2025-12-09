package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(
    tableName = "videos",
    foreignKeys = [
        ForeignKey(
            entity = AgeCategory::class,
            parentColumns = ["id"],
            childColumns = ["ageCategory"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["videoCategory"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("ageCategory"), Index("videoCategory")]
)
data class Video(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val videoUrl: String,
    val thumbnailUrl: String,
    val title: String,
    //val views: Int = 0,
    val ageCategory: Int,
    val videoCategory: Int,
//    val createdAt: Long = System.currentTimeMillis(),
    val sourceType: String, // "youtube" or "uploaded"
    val status: String // "published" or "archived"
)
