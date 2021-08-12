package com.rmf.mvvmtodolist.ui.tugas

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmf.mvvmtodolist.R
import com.rmf.mvvmtodolist.databinding.FragmentTugasBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TugasFragment : Fragment(R.layout.fragment_tugas) {

    private val viewModel: TugasViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTugasBinding.bind(view)

        val tugasAdapter = TugasAdapter()

        binding.apply {
            rv.apply {
                adapter =  tugasAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.tugas.observe(viewLifecycleOwner){
            tugasAdapter.submitList(it)
        }
    }
}