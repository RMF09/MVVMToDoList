package com.rmf.mvvmtodolist.ui.tugas

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rmf.mvvmtodolist.data.DataTugas
import com.rmf.mvvmtodolist.data.PreferencesManager
import com.rmf.mvvmtodolist.data.SortOrder
import com.rmf.mvvmtodolist.data.TugasDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class TugasViewModel @ViewModelInject constructor(
    private val tugasDao: TugasDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")//default

    val prefFlow = preferencesManager.preferencesFlow

    /*val sortOrder = MutableStateFlow(SortOrder.BY_DATE) //default date
    val hideCompleted = MutableStateFlow(false) //default false*/


    private val tugasEventChannel = Channel<TugasEvent>()
    val tugasEvent = tugasEventChannel.receiveAsFlow()

    val tugasFlow =
        combine(searchQuery, prefFlow) { query, filterPref ->
            Pair(query, filterPref)

        }.flatMapLatest { (query, filterPref) ->
            tugasDao.getTugas(query, filterPref.sortOrder, filterPref.hideCompleted)
        }

    @ExperimentalCoroutinesApi
    val tugas = tugasFlow.asLiveData()

    @ExperimentalCoroutinesApi

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(dataTugas: DataTugas){

    }

    fun onTaskCheckedChanged(dataTugas: DataTugas, isChecked: Boolean) = viewModelScope.launch {
        tugasDao.update(dataTugas.copy(completed = isChecked))
    }

    fun onTaskSwiped(dataTugas: DataTugas) =  viewModelScope.launch {
        tugasDao.delete(dataTugas)
        tugasEventChannel.send(TugasEvent.ShowUndoTugasMessage(dataTugas))
    }

    fun onUndoDeletedClick(dataTugas: DataTugas) = viewModelScope.launch {
        tugasDao.insert(dataTugas)
    }

    sealed class TugasEvent{
        data class ShowUndoTugasMessage(val dataTugas: DataTugas) : TugasEvent()
    }

}

