package com.rmf.mvvmtodolist.ui.addedittugas

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.rmf.mvvmtodolist.R
import com.rmf.mvvmtodolist.databinding.FragmentEditTugasBinding
import com.rmf.mvvmtodolist.utils.exhaustif
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_edit_tugas) {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentEditTugasBinding.bind(view)

        binding.apply {
            editTugas.setText(viewModel.namaTugas)
            checkboxPenting.isChecked = viewModel.importanceTugas
            checkboxPenting.jumpDrawablesToCurrentState()
            textDate.isVisible = viewModel.tugas !=null
            textDate.text = "Dibuat: ${viewModel.tugas?.createdDateFormatted}"

            editTugas.addTextChangedListener {
                viewModel.namaTugas = it.toString()
            }

            checkboxPenting.setOnCheckedChangeListener { _, isChecked ->
                viewModel.importanceTugas = isChecked
            }

            btnAdd.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTugasEvent.collect { event ->
                when(event){
                    is AddEditViewModel.AddEditTugasEvent.NavigateBackWithResult -> {
                        binding.editTugas.clearFocus()
                        setFragmentResult("add_edit_request",
                            bundleOf("add_edit_result" to event.result))
                        findNavController().popBackStack()
                    }
                    is AddEditViewModel.AddEditTugasEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustif
            }
        }
    }
}