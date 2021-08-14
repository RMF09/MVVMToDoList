package com.rmf.mvvmtodolist.ui.tugas

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rmf.mvvmtodolist.R
import com.rmf.mvvmtodolist.databinding.FragmentTugasBinding
import com.rmf.mvvmtodolist.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class TugasFragment : Fragment(R.layout.fragment_tugas) {

    private val viewModel: TugasViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTugasBinding.bind(view)

        val tugasAdapter = TugasAdapter()

        binding.apply {
            rv.apply {
                adapter = tugasAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }
        viewModel.tugas.observe(viewLifecycleOwner) {
            tugasAdapter.submitList(it)
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tugas, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.sortOrder.value = SortOrder.BY_NAME
                true
            }
            R.id.action_sort_by_date_created -> {
                viewModel.sortOrder.value = SortOrder.BY_DATE
                true
            }
            R.id.action_hide_compeleted_tasks -> {
                item.isChecked = !item.isChecked
                viewModel.hideCompleted.value = item.isChecked
                true
            }
            R.id.action_delete_all_compeleted_tasks -> {
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }
}