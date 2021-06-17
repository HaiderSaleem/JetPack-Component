package com.debugger.jetpack.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        private val appScope: CoroutineScope
    ) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //db operation
            val dao = database.get().taskDao()

            appScope.launch {
                dao.insert(Task("MVVM"))
                dao.insert(Task("Tasks",important = true))
                dao.insert(Task("1",important = true,completed = true))
                dao.insert(Task("2",completed = false))
                dao.insert(Task("3",important = true))
                dao.insert(Task("4",important = false,completed = true))
                dao.insert(Task("5"))
            }
        }
    }
}