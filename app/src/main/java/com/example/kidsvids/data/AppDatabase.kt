package com.example.kidsvids.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kidsvids.data.dao.AgeCategoryDao
import com.example.kidsvids.data.dao.CategoryDao
import com.example.kidsvids.data.dao.KidBlockedVideoDao
import com.example.kidsvids.data.dao.KidDao
import com.example.kidsvids.data.dao.KidFavoriteDao
import com.example.kidsvids.data.dao.ParentDao
import com.example.kidsvids.data.dao.VideoDao
import com.example.kidsvids.data.dao.WatchHistoryDao
import com.example.kidsvids.data.entities.AgeCategory
import com.example.kidsvids.data.entities.Category
import com.example.kidsvids.data.entities.Kid
import com.example.kidsvids.data.entities.KidBlockedVideo
import com.example.kidsvids.data.entities.KidFavorite
import com.example.kidsvids.data.entities.Parent
import com.example.kidsvids.data.entities.Video
import com.example.kidsvids.data.entities.WatchHistory

@Database(
    entities = [
        Parent::class,
        Kid::class,
        Category::class,
        AgeCategory::class,
        Video::class,
        KidFavorite::class,
        WatchHistory::class,
        KidBlockedVideo::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun parentDao(): ParentDao
    abstract fun kidDao(): KidDao
    abstract fun categoryDao(): CategoryDao
    abstract fun ageCategoryDao(): AgeCategoryDao
    abstract fun videoDao(): VideoDao
    abstract fun kidFavoriteDao(): KidFavoriteDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun kidBlockedVideoDao(): KidBlockedVideoDao
}