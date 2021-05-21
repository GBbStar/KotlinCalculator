package org.techtown.clone_calculater.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.techtown.clone_calculater.model.CalculateHistory

@Dao
interface CalculateHistoryDao {
    @Query("SELECT * FROM CalculateHistory")
    fun getAll():List<CalculateHistory>

    @Insert
    fun insertCalculateHistory(history: CalculateHistory)

    @Query("DELETE FROM CalculateHistory")
    fun deleteAll()

}