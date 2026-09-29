package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.DailyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {
    @Query("SELECT * FROM daily_goals WHERE dateEpochDay = :dateEpochDay LIMIT 1")
    fun getGoalForDate(dateEpochDay: Long): Flow<DailyGoalEntity?>

    @Query("SELECT * FROM daily_goals WHERE dateEpochDay = :dateEpochDay LIMIT 1")
    suspend fun getGoalForDateDirect(dateEpochDay: Long): DailyGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertGoal(goal: DailyGoalEntity)

    @Query("UPDATE daily_goals SET waterMl = :waterMl WHERE dateEpochDay = :dateEpochDay")
    suspend fun updateWater(dateEpochDay: Long, waterMl: Int)
}
