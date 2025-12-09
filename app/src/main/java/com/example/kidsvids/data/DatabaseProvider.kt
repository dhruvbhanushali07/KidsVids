package com.example.kidsvids.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kidsvids.data.AppDatabase // Correct the import path if necessary

object DatabaseProvider {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "kids_video_library_db"
            )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)

                        // 1. Age Categories
                        db.execSQL("INSERT INTO agecategories (id, name) VALUES (1, 'Preschool')")
                        db.execSQL("INSERT INTO agecategories (id, name) VALUES (2, 'Younger')")
                        db.execSQL("INSERT INTO agecategories (id, name) VALUES (3, 'Older')")

                        // 2. Video Categories
                        db.execSQL("INSERT INTO categories (id, name) VALUES (1, 'Music')")
                        db.execSQL("INSERT INTO categories (id, name) VALUES (2, 'Educational')")
                        db.execSQL("INSERT INTO categories (id, name) VALUES (3, 'Cartoons')")
                        db.execSQL("INSERT INTO categories (id, name) VALUES (4, 'Animals')")
                        db.execSQL("INSERT INTO categories (id, name) VALUES (5, 'Art and Craft')")

                        // 3. Sample Videos (Using Cloudinary URLs)
                        // We generate thumbnail URLs by replacing 'video/upload' with 'image/upload' and '.mp4' with '.jpg'

                        // Your example video
                        db.execSQL("INSERT INTO videos (videoUrl, thumbnailUrl, title, ageCategory, videoCategory, sourceType, status) VALUES ('https://res.cloudinary.com/dpdvr2b0v/video/upload/v1749483713/samples/dance-2.mp4', 'https://res.cloudinary.com/dpdvr2b0v/image/upload/v1749483713/samples/dance-2.jpg', 'Kids Dancing', 1, 1, 'uploaded', 'published')")

                        // Added more Cloudinary samples
                        db.execSQL("INSERT INTO videos (videoUrl, thumbnailUrl, title, ageCategory, videoCategory, sourceType, status) VALUES ('https://res.cloudinary.com/dpdvr2b0v/video/upload/v1749483710/samples/sea-turtle.mp4', 'https://res.cloudinary.com/demo/image/upload/samples/sea-turtle.jpg', 'Sea Turtle Swimming', 2, 4, 'uploaded', 'published')")
                        db.execSQL("INSERT INTO videos (videoUrl, thumbnailUrl, title, ageCategory, videoCategory, sourceType, status) VALUES ('https://res.cloudinary.com/dpdvr2b0v/video/upload/v1749483713/samples/dance-2.mp4', 'https://res.cloudinary.com/demo/image/upload/samples/cat.jpg', 'Dance Video', 1, 3, 'uploaded', 'published')")
                        db.execSQL("INSERT INTO videos (videoUrl, thumbnailUrl, title, ageCategory, videoCategory, sourceType, status) VALUES ('https://res.cloudinary.com/dpdvr2b0v/video/upload/v1749483711/samples/cld-sample-video.mp4', 'https://res.cloudinary.com/demo/image/upload/samples/dog.jpg', 'Dog Playing Fetch', 2, 4, 'uploaded', 'published')")
                        db.execSQL("INSERT INTO videos (videoUrl, thumbnailUrl, title, ageCategory, videoCategory, sourceType, status) VALUES ('https://res.cloudinary.com/dpdvr2b0v/video/upload/v1749483710/samples/elephants.mp4', 'https://res.cloudinary.com/demo/image/upload/elephants.jpg', 'Elephants in the Wild', 3, 4, 'uploaded', 'published')")
                    }
                })
                .build()

            INSTANCE = instance
            instance
        }
    }
}