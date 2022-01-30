package com.brettwalking.vords.gameDatabase

import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class SolutionRepository(private val solutionDao: SolutionDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSolutions: Flow<List<Solution>> = solutionDao.getAlphabetizedSolutions()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(solution: Solution) {
        val myMsg = solution.solution + " - " + solution.wordSize.toString()
        Log.i("Inserting: ", myMsg)
        solutionDao.insert(solution)
    }
}
