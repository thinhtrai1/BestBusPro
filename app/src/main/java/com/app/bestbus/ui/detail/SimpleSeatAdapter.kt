package com.app.bestbus.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.bestbus.R

class SimpleSeatAdapter(private val mSeats: List<String>) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return object : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_rcv_simple_seat, parent, false)) {}
    }

    override fun getItemCount(): Int {
        return mSeats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).text = mSeats[position]
    }
}