package org.techtown.clone_calculater

import androidx.room.Database
import androidx.room.RoomDatabase
import org.techtown.clone_calculater.dao.CalculateHistoryDao
import org.techtown.clone_calculater.model.CalculateHistory

@Database(entities = [CalculateHistory::class], version = 1)
abstract class AppRoomDatabase :RoomDatabase(){
    abstract fun calculateHistoryDao():CalculateHistoryDao
}