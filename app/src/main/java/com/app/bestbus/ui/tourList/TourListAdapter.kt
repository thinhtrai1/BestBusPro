package com.app.bestbus.ui.tourList

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.bestbus.R
import com.app.bestbus.databinding.ItemRcvTourListBinding
import com.app.bestbus.models.Tour
import com.app.bestbus.ui.selectSeat.SelectSeatActivity
import com.app.bestbus.utils.Util
import com.app.bestbus.utils.getString
import com.google.gson.Gson

class TourListAdapter(private val date: String): RecyclerView.Adapter<TourListAdapter.ViewHolder>() {
    val tours = ArrayList<Tour>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRcvTourListBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return tours.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.view) {
            tours[position].let {
                tvName.text = it.tourName
                tvPriceOld.text = getString(R.string.dollar_format, Util.formatFloat(it.oldPrice))
                tvPrice.text = getString(R.string.dollar_format, Util.formatFloat(it.price))
                tvStartTime.text = it.startTime
                tvTime.text = getString(R.string.hours, Util.formatFloat(it.time))
                tvEndTime.text = Util.getEndTime(it.startTime, it.time)
                tvFrom.text = it.fromCity
                tvTo.text = it.toCity
                root.setOnClickListener { v ->
                    it.date = date
                    v.context.startActivity(
                        Intent(v.context, SelectSeatActivity::class.java).putExtra("tour", Gson().toJson(it))
                    )
                }
            }
        }
    }

    class ViewHolder(val view: ItemRcvTourListBinding) : RecyclerView.ViewHolder(view.root)
}