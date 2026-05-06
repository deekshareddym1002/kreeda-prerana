package com.example.kreeda_prerana.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kreeda_prerana.data.entity.Trial
import kotlinx.coroutines.flow.Flow

@Dao
interface TrialDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrial(trial: Trial)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrials(trials: List<Trial>)

    @Delete
    suspend fun deleteTrial(trial: Trial)

    @Query("SELECT * FROM trials WHERE athleteId = :athleteId ORDER BY timestamp DESC")
    fun getTrialsForAthlete(athleteId: Long): Flow<List<Trial>>

    @Query("SELECT * FROM trials WHERE testType = :testType ORDER BY value ASC")
    fun getTrialsByType(testType: String): Flow<List<Trial>>

    @Query("SELECT MIN(value) FROM trials WHERE athleteId = :athleteId AND testType = :testType AND unit = 's'")
    suspend fun getPersonalBestTime(athleteId: Long, testType: String): Double?

    @Query("SELECT MAX(value) FROM trials WHERE athleteId = :athleteId AND testType = :testType AND unit = 'm'")
    suspend fun getPersonalBestDistance(athleteId: Long, testType: String): Double?
}
