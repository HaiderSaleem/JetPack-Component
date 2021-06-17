package com.debugger.jetpack.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(
        searchQuery: String,
        sortOrder: SortedOrder,
        hideComplete: Boolean
    ): Flow<List<Task>> = when (sortOrder) {
        SortedOrder.SORT_BY_DATE -> getTasksByDateOrder(searchQuery, hideComplete)
        SortedOrder.SORT_BY_NAME -> getTasksByNameOrder(searchQuery, hideComplete)
    }

    @Query("SELECT * FROM task_table WHERE (completed!= :hideComplete or completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created ASC")
    fun getTasksByDateOrder(searchQuery: String, hideComplete: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed!= :hideComplete or completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC, name ASC")
    fun getTasksByNameOrder(searchQuery: String, hideComplete: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}