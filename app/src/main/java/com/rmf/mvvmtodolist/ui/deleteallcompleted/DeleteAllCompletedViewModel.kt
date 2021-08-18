package com.rmf.mvvmtodolist.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.rmf.mvvmtodolist.data.TugasDao
import com.rmf.mvvmtodolist.di.AppScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val tugasDao: TugasDao,
    @AppScope private val appScope: CoroutineScope
    ) : ViewModel() {

        fun deleteCompletedTugas() = appScope.launch {
            tugasDao.deleteCompletedTugas()
        }


}