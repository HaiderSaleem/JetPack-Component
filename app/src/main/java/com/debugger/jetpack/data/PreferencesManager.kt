package com.debugger.jetpack.data

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

enum class SortedOrder {
    SORT_BY_DATE,
    SORT_BY_NAME
}

data class FilterPreferences(val sortedOrder: SortedOrder, val hideComplete: Boolean)

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.createDataStore("user_preferences")

    val preferencesFlow = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d(TAG, ": Error Reading Preferences $exception")
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val sortOrder = SortedOrder.valueOf(
                preferences[PreferencesKeys.SORT_ORDER] ?: SortedOrder.SORT_BY_DATE.name
            )

            val hideComplete = preferences[PreferencesKeys.HIDE_COMPLETE] ?: false

            FilterPreferences(sortOrder, hideComplete)
        }

    suspend fun updateSortOrder(sortedOrder: SortedOrder) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORT_ORDER] = sortedOrder.name
        }
    }

    suspend fun updateHideComplete(hideComplete: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.HIDE_COMPLETE] = hideComplete
        }
    }

    private object PreferencesKeys {
        val SORT_ORDER = preferencesKey<String>("sort_order")
        val HIDE_COMPLETE = preferencesKey<Boolean>("hide_completed")
    }
}