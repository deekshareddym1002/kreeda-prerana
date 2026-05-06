package com.example.kreeda_prerana.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "athletes")
data class Athlete(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val age: Int,
    val gender: String = "Not Specified",
    val primarySport: String
)
