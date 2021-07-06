package com.stenleone.clenner.ui.adapters.binding

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stenleone.clenner.model.AppData
import com.stenleone.clenner.ui.adapters.recycler.AppDataAdapter

@BindingAdapter("app:setupAppDataRecycler")
fun setupAppDataRecycler(recycler: RecyclerView, data: ArrayList<AppData>) {

    recycler.layoutManager = LinearLayoutManager(recycler.context)
    recycler.adapter = AppDataAdapter().also {
        it.listItems.addAll(data)
    }
    recycler.adapter?.notifyDataSetChanged()
}