package com.example.kidsvids.data.entities
import androidx.room.*

@Entity(
    tableName = "kids",
    foreignKeys = [ForeignKey(
        entity = Parent::class,
        parentColumns = ["id"],
        childColumns = ["parentId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("parentId")]
)
data class Kid(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val parentId: Int,
    val name: String,
//    val birthdate: String, // store as yyyy-MM-dd string
    val ageCategory: Int,
    val avatarUrl: Int,
//    val screen_time_minutes: Int? = null,
//    val bed_time: String? = null, // store as "HH:mm"
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)
