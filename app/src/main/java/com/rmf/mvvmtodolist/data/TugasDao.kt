package com.rmf.mvvmtodolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {

    @Query("SELECT * FROM datatugas")
    fun getTugas(): Flow<List<DataTugas>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tugas: DataTugas)

    @Update
    suspend fun update(tugas: DataTugas)

    @Delete
    suspend fun delete(tugas: DataTugas)

}