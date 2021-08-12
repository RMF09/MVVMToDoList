package com.rmf.mvvmtodolist.ui.tugas

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rmf.mvvmtodolist.data.TugasDao
import javax.inject.Inject

class TugasViewModel @ViewModelInject constructor(
    tugasDao: TugasDao
) : ViewModel(){

    val tugas = tugasDao.getTugas().asLiveData()
}