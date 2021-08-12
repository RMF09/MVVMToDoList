package com.rmf.mvvmtodolist.di

import android.app.Application
import androidx.room.Room
import com.rmf.mvvmtodolist.data.TugasDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: TugasDatabase.Callback
    ) = Room.databaseBuilder(app, TugasDatabase::class.java, "tugas_db")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTugasDao(db: TugasDatabase) = db.tugasDao()

    @AppScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class AppScope