package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class CustomerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val businessId: Long = 1,
    val name: String,
    val phone: String,
    val address: String = "",
    val notes: String = "",
    val isFavorite: Boolean = false,
    val avatarColorHex: String = "#006A36",
    val createdAt: Long = System.currentTimeMillis()
)
