package com.example.todolist.data

import android.os.FileObserver.DELETE
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query:String, sortOrder: SortOrder, hideCompleted: Boolean)=
     when(sortOrder){
        SortOrder.BY_DATE->getTasksSortByDatecreated(query,hideCompleted)
         SortOrder.BY_NAME->getTasksSortByName(query,hideCompleted)
    }

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,name")
    fun getTasksSortByName(searchQuery: String,hideCompleted:Boolean):Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed=0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,created")
    fun getTasksSortByDatecreated(searchQuery: String,hideCompleted:Boolean):Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("DELETE FROM task_table WHERE completed=1")
    suspend fun deleteCompletedTasks()

}