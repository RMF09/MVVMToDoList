package com.rmf.mvvmtodolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TugasDao {

    fun getTugas(
        query: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<DataTugas>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> getTugasSortedByName(query, hideCompleted)
            SortOrder.BY_DATE -> getTugasSortedByDateCreated(query, hideCompleted)
        }

    @Query("SELECT * FROM datatugas WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,name")
    fun getTugasSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<DataTugas>>

    @Query("SELECT * FROM datatugas WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,created")
    fun getTugasSortedByDateCreated(
        searchQuery: String,
        hideCompleted: Boolean
    ): Flow<List<DataTugas>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tugas: DataTugas)

    @Update
    suspend fun update(tugas: DataTugas)

    @Delete
    suspend fun delete(tugas: DataTugas)

    @Query("DELETE from datatugas WHERE completed = 1")
    suspend fun deleteCompletedTugas()

}