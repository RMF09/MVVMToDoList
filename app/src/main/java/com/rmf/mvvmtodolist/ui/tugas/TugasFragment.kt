package com.rmf.mvvmtodolist.ui.tugas

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rmf.mvvmtodolist.R
import com.rmf.mvvmtodolist.data.DataTugas
import com.rmf.mvvmtodolist.data.SortOrder
import com.rmf.mvvmtodolist.databinding.FragmentTugasBinding
import com.rmf.mvvmtodolist.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TugasFragment : Fragment(R.layout.fragment_tugas), TugasAdapter.OnItemClickListener {

    private val viewModel: TugasViewModel by viewModels()

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTugasBinding.bind(view)

        val tugasAdapter = TugasAdapter(this)

        binding.apply {
            rv.apply {
                adapter = tugasAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean { return false }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val dataTugas = tugasAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(dataTugas)
                }
            }).attachToRecyclerView(rv)
        }
        viewModel.tugas.observe(viewLifecycleOwner) {
            tugasAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tugasEvent.collect { event ->
                when(event){
                    is TugasViewModel.TugasEvent.ShowUndoTugasMessage -> {
                        Snackbar.make(requireView(),"Tugas dihapus", Snackbar.LENGTH_LONG)
                            .setAction("Urungkan"){
                                viewModel.onUndoDeletedClick(event.dataTugas)
                            }.show()
                    }
                }
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onItemClick(dataTugas: DataTugas) {
        viewModel.onTaskSelected(dataTugas)
    }

    override fun onCheckBoxClick(dataTugas: DataTugas, isChecke: Boolean) {
        viewModel.onTaskCheckedChanged(dataTugas, isChecke)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_tugas, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }

        viewLifecycleOwner.lifecycleScope.launch {
            menu.findItem(R.id.action_hide_compeleted_tasks).isChecked =
                viewModel.prefFlow.first().hideCompleted
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort_by_name -> {
                //viewModel.sortOrder.value = SortOrder.BY_NAME
                viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                true
            }
            R.id.action_sort_by_date_created -> {
                //viewModel.sortOrder.value = SortOrder.BY_DATE
                viewModel.onSortOrderSelected(SortOrder.BY_DATE)

                true
            }
            R.id.action_hide_compeleted_tasks -> {
                item.isChecked = !item.isChecked
                //viewModel.hideCompleted.value = item.isChecked
                viewModel.onHideCompletedClick(item.isChecked)
                true
            }
            R.id.action_delete_all_compeleted_tasks -> {
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }
}