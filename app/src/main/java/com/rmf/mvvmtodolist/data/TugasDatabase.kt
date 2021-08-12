package com.rmf.mvvmtodolist.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rmf.mvvmtodolist.di.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [DataTugas::class], version = 1)
abstract class TugasDatabase : RoomDatabase() {

    abstract fun tugasDao(): TugasDao

    class Callback @Inject constructor(
        private val database: Provider<TugasDatabase>,
        @AppScope private val appScope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            //db operation
            val dao = database.get().tugasDao()

            appScope.launch {
                dao.insert(DataTugas("Cuci piring", completed = true))
                dao.insert(DataTugas("Momotoran"))
                dao.insert(DataTugas("Marab ucing",important = true))
                dao.insert(DataTugas("Siap siap makan"))
                dao.insert(DataTugas("Memperbaiki skuter"))
                dao.insert(DataTugas("Call mom"))
                dao.insert(DataTugas("Beli grosiran",important = true))
                dao.insert(DataTugas("Mencuci pakaian"))
            }

        }
    }
}