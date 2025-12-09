package com.example.kidsvids.data.entities

import androidx.room.*

@Entity(tableName = "parents")
data class Parent(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullname: String,
    val email: String,
    val password_hash: String,
    val pin: String,
    val isActive: Boolean = true,
    val last_login_at: Long? = null,
    val created_at: Long = System.currentTimeMillis(),
    val updated_at: Long = System.currentTimeMillis()
)
