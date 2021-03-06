package com.rmf.mvvmtodolist.ui.tugas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rmf.mvvmtodolist.data.DataTugas
import com.rmf.mvvmtodolist.databinding.ItemTugasBinding

class TugasAdapter(private val listener: OnItemClickListener) : ListAdapter<DataTugas, TugasAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTugasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class ViewHolder(private val binding: ItemTugasBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding){
                root.setOnClickListener {
                    if(adapterPosition != RecyclerView.NO_POSITION){
                        val dataTugas = getItem(adapterPosition)
                        listener.onItemClick(dataTugas)
                    }
                }

                checkbox.setOnClickListener {
                    if(adapterPosition != RecyclerView.NO_POSITION){
                        val dataTugas = getItem(adapterPosition)
                        listener.onCheckBoxClick(dataTugas,checkbox.isChecked)
                    }
                }
            }
        }

        fun bind(dataTugas: DataTugas) {
            with(binding) {
                checkbox.isChecked = dataTugas.completed
                textItem.text = dataTugas.name
                textItem.paint.isStrikeThruText = dataTugas.completed
                imagePriority.isVisible = dataTugas.important
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(dataTugas: DataTugas)
        fun onCheckBoxClick(dataTugas: DataTugas, isChecke: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<DataTugas>() {
        override fun areItemsTheSame(oldItem: DataTugas, newItem: DataTugas) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DataTugas, newItem: DataTugas) =
            oldItem == newItem
    }
}