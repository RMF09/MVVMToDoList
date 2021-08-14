package com.rmf.mvvmtodolist.ui.tugas

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rmf.mvvmtodolist.data.TugasDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

class TugasViewModel @ViewModelInject constructor(
    tugasDao: TugasDao
) : ViewModel() {

    val searchQuery = MutableStateFlow("")//default
    val sortOrder = MutableStateFlow(SortOrder.BY_DATE) //default date
    val hideCompleted = MutableStateFlow(false) //default false

    @ExperimentalCoroutinesApi
    val tugasFlow =
        combine(searchQuery, sortOrder, hideCompleted) { query, sortOrder, hideCompleted ->
            Triple(query, sortOrder, hideCompleted)

        }.flatMapLatest {(query, sortOrder, hideCompleted) ->
            tugasDao.getTugas(query,sortOrder,hideCompleted)
        }

    @ExperimentalCoroutinesApi
    val tugas = tugasFlow.asLiveData()
}

enum class SortOrder { BY_NAME, BY_DATE }