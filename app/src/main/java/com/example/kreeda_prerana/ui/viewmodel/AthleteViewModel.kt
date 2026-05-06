package com.example.kreeda_prerana.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.kreeda_prerana.data.AppDatabase
import com.example.kreeda_prerana.data.entity.Athlete
import com.example.kreeda_prerana.data.entity.Trial
import com.example.kreeda_prerana.data.repository.AthleteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AthleteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AthleteRepository
    val allAthletes: Flow<List<Athlete>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AthleteRepository(database.athleteDao(), database.trialDao())
        allAthletes = repository.allAthletes
    }

    // Athlete Operations
    fun addAthlete(name: String, age: Int, sport: String, gender: String) {
        viewModelScope.launch {
            repository.insertAthlete(Athlete(name = name, age = age, primarySport = sport, gender = gender))
        }
    }

    fun updateAthlete(athlete: Athlete) {
        viewModelScope.launch {
            repository.updateAthlete(athlete)
        }
    }

    fun deleteAthlete(athlete: Athlete) {
        viewModelScope.launch {
            repository.deleteAthlete(athlete)
        }
    }

    suspend fun getAthleteById(id: Long): Athlete? {
        return repository.getAthleteById(id)
    }

    // Trial Operations
    fun addTrial(athleteId: Long, testType: String, value: Double, unit: String) {
        viewModelScope.launch {
            repository.insertTrial(Trial(athleteId = athleteId, testType = testType, value = value, unit = unit))
        }
    }

    fun addBatchTrials(trials: List<Trial>) {
        viewModelScope.launch {
            repository.insertTrials(trials)
        }
    }

    fun getTrialsForAthlete(athleteId: Long): Flow<List<Trial>> {
        return repository.getTrialsForAthlete(athleteId)
    }

    fun getTrialsByType(testType: String): Flow<List<Trial>> {
        return repository.getTrialsByType(testType)
    }
}
