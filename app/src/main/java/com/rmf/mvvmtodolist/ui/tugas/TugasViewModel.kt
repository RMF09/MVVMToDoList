package com.rmf.mvvmtodolist.ui.tugas

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.rmf.mvvmtodolist.data.DataTugas
import com.rmf.mvvmtodolist.data.PreferencesManager
import com.rmf.mvvmtodolist.data.SortOrder
import com.rmf.mvvmtodolist.data.TugasDao
import com.rmf.mvvmtodolist.ui.ADD_TUGAS_RESULT_OK
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
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {


    val searchQuery = state.getLiveData("searchQuery","")

    //val searchQuery = MutableStateFlow("")//default

    val prefFlow = preferencesManager.preferencesFlow

    /*val sortOrder = MutableStateFlow(SortOrder.BY_DATE) //default date
    val hideCompleted = MutableStateFlow(false) //default false*/


    private val tugasEventChannel = Channel<TugasEvent>()
    val tugasEvent = tugasEventChannel.receiveAsFlow()

    val tugasFlow =
        combine(searchQuery.asFlow(), prefFlow) { query, filterPref ->
            Pair(query, filterPref)

        }.flatMapLatest { (query, filterPref) ->
            tugasDao.getTugas(query, filterPref.sortOrder, filterPref.hideCompleted)
        }

    val tugas = tugasFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
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

    fun onAddTugasBaruClick() = viewModelScope.launch {
        tugasEventChannel.send(TugasEvent.NavigateToAddTugasScreen)
    }

    fun onTaskSelected(dataTugas: DataTugas) = viewModelScope.launch {
        tugasEventChannel.send(TugasEvent.NavigateToEditTugasScreen(dataTugas))
    }
    fun onAddEditResult(result: Int) {
        when(result){
            ADD_TUGAS_RESULT_OK -> showTugasDisimpanConfirmationMessage("Tugas ditambahkan")
            ADD_TUGAS_RESULT_OK -> showTugasDisimpanConfirmationMessage("Tugas diperbarui")
        }
    }

    private fun showTugasDisimpanConfirmationMessage(msg : String) = viewModelScope.launch {
        tugasEventChannel.send(TugasEvent.ShowMessageTugasDisimpan(msg))
    }

    sealed class TugasEvent{
        object NavigateToAddTugasScreen : TugasEvent()
        data class NavigateToEditTugasScreen(val dataTugas: DataTugas) : TugasEvent()
        data class ShowUndoTugasMessage(val dataTugas: DataTugas) : TugasEvent()
        data class ShowMessageTugasDisimpan(val msg: String) : TugasEvent()
    }

}

