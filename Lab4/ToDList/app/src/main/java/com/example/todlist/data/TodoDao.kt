package com.example.todlist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    // ---------- Liste ----------

    @Query(
        """
        SELECT l.id, l.name,
               COUNT(t.id) AS total,
               COALESCE(SUM(t.done), 0) AS done
        FROM lists l
        LEFT JOIN tasks t ON t.listId = l.id
        GROUP BY l.id
        ORDER BY l.id DESC
        """
    )
    fun observeLists(): Flow<List<ListSummary>>

    @Query("SELECT * FROM lists WHERE id = :id")
    fun observeList(id: Long): Flow<TodoList?>

    @Insert
    suspend fun insertList(list: TodoList): Long

    @Query("UPDATE lists SET name = :name WHERE id = :id")
    suspend fun renameList(id: Long, name: String)

    @Query("DELETE FROM lists WHERE id = :id")
    suspend fun deleteList(id: Long)

    // ---------- Task-uri ----------

    /** Task-urile nefinalizate apar primele, cele noi deasupra. */
    @Query("SELECT * FROM tasks WHERE listId = :listId ORDER BY done ASC, id DESC")
    fun observeTasks(listId: Long): Flow<List<TaskItem>>

    @Insert
    suspend fun insertTask(task: TaskItem)

    @Update
    suspend fun updateTask(task: TaskItem)

    @Delete
    suspend fun deleteTask(task: TaskItem)
}
