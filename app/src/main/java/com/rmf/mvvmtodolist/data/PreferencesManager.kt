package com.rmf.mvvmtodolist.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.emptyPreferences
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "PreferencesManager"

enum class SortOrder { BY_NAME, BY_DATE }

data class FilterPreferences(val sortOrder: SortOrder, val hideCompleted: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("settings")

    val preferencesFlow = dataStore.data
        .catch { e ->
            if (e is IOException) {
                Log.e(TAG, ": Error reading preferences",e )
                emit(emptyPreferences())
            } else {
                throw e
            }
        }
        .map { pref ->
            val sortOrder = SortOrder.valueOf(
                pref[PreferenceKeys.SORT_ORDER] ?: SortOrder.BY_DATE.name
            )
            val hideCompleted = pref[PreferenceKeys.HIDE_COMPLETED] ?: false
            FilterPreferences(sortOrder, hideCompleted)
        }

    suspend fun updateSortOrder(sortOrder: SortOrder){
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SORT_ORDER] = sortOrder.name
        }
    }

    suspend fun updateHideCompleted(hideCompleted: Boolean){
        dataStore.edit {
            it[PreferenceKeys.HIDE_COMPLETED] =hideCompleted
        }
    }

    private object PreferenceKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETED = preferencesKey<Boolean>("hide_completed")
    }
}