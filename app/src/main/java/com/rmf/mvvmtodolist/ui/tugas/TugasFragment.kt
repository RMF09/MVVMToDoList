package com.rmf.mvvmtodolist.ui.tugas

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.rmf.mvvmtodolist.R
import com.rmf.mvvmtodolist.data.DataTugas
import com.rmf.mvvmtodolist.data.SortOrder
import com.rmf.mvvmtodolist.databinding.FragmentTugasBinding
import com.rmf.mvvmtodolist.utils.exhaustif
import com.rmf.mvvmtodolist.utils.onQueryTextChanged
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TugasFragment : Fragment(R.layout.fragment_tugas), TugasAdapter.OnItemClickListener {

    private val viewModel: TugasViewModel by viewModels()

    private lateinit  var searchView : SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("TAG", "onViewCreated: ")
        val binding = FragmentTugasBinding.bind(view)

        val tugasAdapter = TugasAdapter(this)

        binding.apply {
            rv.apply {
                adapter = tugasAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(
                    0,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                ) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val dataTugas = tugasAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(dataTugas)
                }
            }).attachToRecyclerView(rv)

            btnAdd.setOnClickListener {
                viewModel.onAddTugasBaruClick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)

        }

        viewModel.tugas.observe(viewLifecycleOwner) {
            tugasAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tugasEvent.collect { event ->
                when (event) {
                    is TugasViewModel.TugasEvent.ShowUndoTugasMessage -> {
                        Snackbar.make(requireView(), "Tugas dihapus", Snackbar.LENGTH_LONG)
                            .setAction("Urungkan") {
                                viewModel.onUndoDeletedClick(event.dataTugas)
                            }.show()
                    }
                    is TugasViewModel.TugasEvent.NavigateToAddTugasScreen -> {
                        val action =
                            TugasFragmentDirections.actionTugasFragmentToEditFragment(title = "Tambah Tugas Baru")
                        findNavController().navigate(action)
                    }

                    is TugasViewModel.TugasEvent.NavigateToEditTugasScreen -> {
                        val action = TugasFragmentDirections.actionTugasFragmentToEditFragment(
                            event.dataTugas,
                            "Edit Tugas ${event.dataTugas.id}"
                        )
                        findNavController().navigate(action)

                    }
                    is TugasViewModel.TugasEvent.ShowMessageTugasDisimpan -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT)
                           .show()
                    }
                    is TugasViewModel.TugasEvent.NavigateToDialogDeleteAllCompleted -> {
                        val action = TugasFragmentDirections.actionGlobalDeleteCompletedDialog()
                        findNavController().navigate(action)
                    }
                }.exhaustif
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
        searchView = searchItem.actionView as SearchView

        val pendingQuery = viewModel.searchQuery.value

        pendingQuery?.let {
            if(pendingQuery.isNotEmpty()){
                searchItem.expandActionView()
                searchView.setQuery(it,false)
            }
        }

        searchView.onQueryTextChanged {
            viewModel.searchQuery.value = it
        }
        Log.d("Perubahan", "onCreateOptionsMenu: ${viewModel.searchQuery.value}")


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
                viewModel.onDeleteAllCompeletedTugas()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}