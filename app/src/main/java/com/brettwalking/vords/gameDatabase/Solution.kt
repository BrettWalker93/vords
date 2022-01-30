package com.brettwalking.vords.gameDatabase

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solution_table")
class Solution(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "solution")
    val solution: String,

    @ColumnInfo(name = "size")
    val wordSize: Int?,

    @ColumnInfo(name = "list")
    val guessList: String?,

    @ColumnInfo(name = "count")
    val guessCount: Int?,) {

}
