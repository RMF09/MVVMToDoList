package com.rmf.mvvmtodolist.ui.tugas

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rmf.mvvmtodolist.data.PreferencesManager
import com.rmf.mvvmtodolist.data.SortOrder
import com.rmf.mvvmtodolist.data.TugasDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class TugasViewModel @ViewModelInject constructor(
    tugasDao: TugasDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")//default

    val prefFlow = preferencesManager.preferencesFlow

    /*val sortOrder = MutableStateFlow(SortOrder.BY_DATE) //default date
    val hideCompleted = MutableStateFlow(false) //default false*/

    @ExperimentalCoroutinesApi
    val tugasFlow =
        combine(searchQuery, prefFlow) { query, filterPref ->
            Pair(query, filterPref)

        }.flatMapLatest { (query, filterPref) ->
            tugasDao.getTugas(query, filterPref.sortOrder, filterPref.hideCompleted)
        }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    @ExperimentalCoroutinesApi
    val tugas = tugasFlow.asLiveData()
}

