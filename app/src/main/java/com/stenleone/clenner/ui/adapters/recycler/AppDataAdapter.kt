package com.stenleone.clenner.ui.adapters.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.stenleone.clenner.databinding.ItemAppDataBinding
import com.stenleone.clenner.interfaces.RecyclerViewInterface
import com.stenleone.clenner.model.AppData

class AppDataAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val listItems = arrayListOf<AppData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ItemHolder(ItemAppDataBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecyclerViewInterface).bind()
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ItemHolder(private val binding: ItemAppDataBinding) : RecyclerView.ViewHolder(binding.root), RecyclerViewInterface {

        override fun bind() {
            binding.data = listItems[adapterPosition]
        }

    }

}