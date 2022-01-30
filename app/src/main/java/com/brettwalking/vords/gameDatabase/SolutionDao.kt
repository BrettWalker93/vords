package com.brettwalking.vords.gameDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SolutionDao {

    @Query("SELECT * FROM solution_table ORDER BY solution ASC")
    fun getAlphabetizedSolutions(): Flow<List<Solution>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(solution: Solution)

    @Query("DELETE FROM solution_table")
    suspend fun deleteAll()
}
