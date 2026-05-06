package com.example.kreeda_prerana.data.repository

import com.example.kreeda_prerana.data.dao.AthleteDao
import com.example.kreeda_prerana.data.dao.TrialDao
import com.example.kreeda_prerana.data.entity.Athlete
import com.example.kreeda_prerana.data.entity.Trial
import kotlinx.coroutines.flow.Flow

class AthleteRepository(
    private val athleteDao: AthleteDao,
    private val trialDao: TrialDao
) {
    // Athlete operations
    val allAthletes: Flow<List<Athlete>> = athleteDao.getAllAthletes()

    suspend fun insertAthlete(athlete: Athlete): Long = athleteDao.insertAthlete(athlete)
    suspend fun updateAthlete(athlete: Athlete) = athleteDao.updateAthlete(athlete)
    suspend fun deleteAthlete(athlete: Athlete) = athleteDao.deleteAthlete(athlete)
    suspend fun getAthleteById(id: Long): Athlete? = athleteDao.getAthleteById(id)

    // Trial operations
    suspend fun insertTrial(trial: Trial) = trialDao.insertTrial(trial)
    suspend fun insertTrials(trials: List<Trial>) = trialDao.insertTrials(trials)
    suspend fun deleteTrial(trial: Trial) = trialDao.deleteTrial(trial)
    fun getTrialsForAthlete(athleteId: Long): Flow<List<Trial>> = trialDao.getTrialsForAthlete(athleteId)
    fun getTrialsByType(testType: String): Flow<List<Trial>> = trialDao.getTrialsByType(testType)

    suspend fun getPersonalBest(athleteId: Long, testType: String, unit: String): Double? {
        return if (unit == "s") {
            trialDao.getPersonalBestTime(athleteId, testType)
        } else {
            trialDao.getPersonalBestDistance(athleteId, testType)
        }
    }
}
