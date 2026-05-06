package com.example.kreeda_prerana.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.kreeda_prerana.data.entity.Athlete
import kotlinx.coroutines.flow.Flow

@Dao
interface AthleteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAthlete(athlete: Athlete): Long

    @Update
    suspend fun updateAthlete(athlete: Athlete)

    @Delete
    suspend fun deleteAthlete(athlete: Athlete)

    @Query("SELECT * FROM athletes ORDER BY name ASC")
    fun getAllAthletes(): Flow<List<Athlete>>

    @Query("SELECT * FROM athletes WHERE id = :id")
    suspend fun getAthleteById(id: Long): Athlete?

    @Query("SELECT * FROM athletes WHERE primarySport = :sport")
    fun getAthletesBySport(sport: String): Flow<List<Athlete>>
}
