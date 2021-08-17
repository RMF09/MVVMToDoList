package com.rmf.mvvmtodolist.ui.addedittugas

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmf.mvvmtodolist.data.DataTugas
import com.rmf.mvvmtodolist.data.TugasDao
import com.rmf.mvvmtodolist.ui.ADD_TUGAS_RESULT_OK
import com.rmf.mvvmtodolist.ui.EDIT_TUGAS_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
class AddEditViewModel @ViewModelInject constructor(
    private val tugasDao: TugasDao,
    @Assisted private val state: SavedStateHandle
): ViewModel() {

    val tugas = state.get<DataTugas>("tugas")

    var namaTugas = state.get<String>("namaTugas") ?: tugas?.name ?: ""
        set(value) {
            field = value
            state.set("namaTugas", value)
        }
    var importanceTugas = state.get<Boolean>("importanceTugas") ?: tugas?.important ?: false
        set(value) {
            field = value
            state.set("importanceTugas", value)
        }

    private val addEditEventChannel = Channel<AddEditTugasEvent>()
    val addEditTugasEvent = addEditEventChannel.receiveAsFlow()

    fun onSaveClick(){
        if (namaTugas.isBlank()) {
            showMessageError("Nama tugas tidak boleh kosong ")
            return
        }
        if (tugas != null) {
            val updateTugas =  tugas.copy(name = namaTugas, important = importanceTugas)
            updateTugas(updateTugas)
        }else{
            val dataTugas = DataTugas(name = namaTugas, important = importanceTugas)
            createTugas(dataTugas)
        }
    }
    private fun updateTugas(dataTugas: DataTugas) = viewModelScope.launch {
        tugasDao.update(dataTugas)
        addEditEventChannel.send(AddEditTugasEvent.NavigateBackWithResult(EDIT_TUGAS_RESULT_OK))
    }

    private fun createTugas(dataTugas: DataTugas) = viewModelScope.launch {
        tugasDao.insert(dataTugas)
        addEditEventChannel.send(AddEditTugasEvent.NavigateBackWithResult(ADD_TUGAS_RESULT_OK))
    }

    private fun showMessageError(msg: String) = viewModelScope.launch {
        addEditEventChannel.send(AddEditTugasEvent.ShowInvalidInputMessage(msg))
    }

    sealed class AddEditTugasEvent{
        data class ShowInvalidInputMessage(val msg: String) : AddEditTugasEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTugasEvent()
    }
}