package com.example.kreeda_prerana.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trials",
    foreignKeys = [
        ForeignKey(
            entity = Athlete::class,
            parentColumns = ["id"],
            childColumns = ["athleteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["athleteId"])]
)
data class Trial(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val athleteId: Long,
    val testType: String, // e.g., "100m Sprint", "Long Jump"
    val value: Double, // Time in seconds or distance in meters
    val unit: String, // "s" or "m"
    val timestamp: Long = System.currentTimeMillis()
)
