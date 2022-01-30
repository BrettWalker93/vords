package com.brettwalking.vords.gameDatabase

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SolutionsApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { SolutionRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SolutionRepository(database.solutionDao()) }
}
