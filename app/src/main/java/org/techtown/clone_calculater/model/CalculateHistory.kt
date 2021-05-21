package org.techtown.clone_calculater.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CalculateHistory (
    @PrimaryKey val uid:Int?,
    @ColumnInfo(name="expression") val expression:String?,
    @ColumnInfo(name="result") val result:String?
)