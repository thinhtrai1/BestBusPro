package com.app.bestbus.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.databinding.ItemRcvDealBinding
import com.app.bestbus.models.Deal

class DealAdapter : RecyclerView.Adapter<DealAdapter.ViewHolder>() {
    lateinit var itemLayout: ViewGroup.LayoutParams
    val deals = ArrayList<Deal>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRcvDealBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return deals.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.layoutParams = itemLayout
        holder.view.apply {
            if (position == 0) {
                viewSpaceStart.visibility = View.VISIBLE
            } else {
                viewSpaceStart.visibility = View.GONE
            }
            deal = deals[position]
            executePendingBindings()
        }
    }

    class ViewHolder(val view: ItemRcvDealBinding) : RecyclerView.ViewHolder(view.root)
}