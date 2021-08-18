package com.rmf.mvvmtodolist.ui.deleteallcompleted

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCompletedDialog : DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?) =
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi pengahapusa")
            .setMessage("Yakin ingin menghapus tugas yang telah selesai")
            .setNegativeButton("Batal",null)
            .setPositiveButton("Ya"){ _,_ ->
                viewModel.deleteCompletedTugas()
            }.create()
}