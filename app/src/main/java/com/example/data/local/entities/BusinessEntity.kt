package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val ownerName: String,
    val phone: String,
    val address: String,
    val currencySymbol: String = "৳",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
