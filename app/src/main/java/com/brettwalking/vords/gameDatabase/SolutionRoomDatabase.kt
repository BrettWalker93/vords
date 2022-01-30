package com.brettwalking.vords.gameDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Solution::class], version = 1, exportSchema = false)
abstract class SolutionRoomDatabase : RoomDatabase() {

    abstract fun solutionDao(): SolutionDao

    private class SolutionDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    var solutionDao = database.solutionDao()
                    // Delete all content here.
                    solutionDao.deleteAll()
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SolutionRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): SolutionRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SolutionRoomDatabase::class.java,
                    "solution_database"
                )
                    .addCallback(SolutionDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
