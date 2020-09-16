package com.travelrights.room

import androidx.room.*
import com.travelrights.model.AirportResponse

@Dao
interface AirportDao {
    @get:Query("SELECT * FROM task")
    val all: List<AirportResponse>

    @Insert
    fun insert(task: AirportResponse?)

    @Delete
    fun delete(task: AirportResponse?)

    @Update
    fun update(task: AirportResponse?)

    @Query("DELETE FROM task WHERE id NOT IN (SELECT MAX(id) FROM task GROUP BY name, code)")
    fun deleteDuplicates()

    @Query("DELETE FROM task WHERE id = :id")
    fun deleteById(id: Int)
}